package controllers;

import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.diversity.Rarefaction;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.RarefactionChart.RarefactionLine;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import scala.concurrent.duration.Duration;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.CacheType.CacheType;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;
import graph.RarefactionChart.RarefactionColor;
import utils.server.CacheServerResponse;
import utils.server.ServerResponse;
import utils.server.WSResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SecureSocial.SecuredAction
public class AccountAPI extends Controller {

    public static Result account() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(views.html.account.accountMainPage.render(localUser.getAccountUserName()));
    }

    public static Result upload() {

        //Identifying user using the SecureSocial API
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;


        if (account == null) {
            return ok(Json.toJson(new ServerResponse("error", "Unknown error")));
        }

        //Checking files count
        Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
        if (maxFilesCount > 0) {
            if (account.getFilesCount() >= maxFilesCount) {
                Logger.of("user." + account.getUserName()).info("User " + account.getUserName() + ": exceeded the limit of the number of files");
                return ok(Json.toJson(new ServerResponse("error", "You have exceeded the limit of the number of files")));
            }
        }

        //Getting the file from request body
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            return ok(Json.toJson(new ServerResponse("error", "You should upload the file")));
        }
        Long maxFileSize = Play.application().configuration().getLong("maxFileSize");
        if (maxFileSize > 0) {
            Long sizeMB = file.getFile().length() / 1024;
            if (sizeMB > maxFileSize) {
                return ok(Json.toJson(new ServerResponse("error", "File is too large")));
            }
        }


        //Getting fileName
        //If User do not enter the name of file
        //it will be fills automatically using current file name
        String fileName;
        if (file.getFilename().equals("")) {
            fileName = FilenameUtils.removeExtension(file.getFilename());
        } else {
            fileName = body.asFormUrlEncoded().get("fileName")[0];
        }

        String pattern = "^[a-zA-Z0-9_.-]{1,20}$";
        if (fileName == null || !fileName.matches(pattern)) {
            return ok(Json.toJson(new ServerResponse("error", "Invalid name")));
        }


        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.getFileName().equals(fileName)) {
                return ok(Json.toJson(new ServerResponse("error", "You should use unique names for your files")));
            }
        }

        try {

            File accountDir = new File(account.getDirectoryPath());
            if (!accountDir.exists()) {
                Boolean accountDirCreated = accountDir.mkdir();
                if (!accountDirCreated) {
                    Logger.of("user." + account.getUserName()).error("Error creating main directory for user " + account.getUserName());
                    return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
                }
            }

            File uploadedFile = file.getFile();
            String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(5, CommonUtil.RandomStringGenerator.Mode.ALPHA);
            File fileDir = (new File(account.getDirectoryPath() + "/" + unique_name + "/"));

            //Trying to create file directory
            if (!fileDir.exists()) {
                Boolean created = fileDir.mkdir();
                if (!created) {
                    Logger.of("user." + account.getUserName()).error("Error creating file directory for user " + account.getUserName());
                    return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
                }
            }

            String fileExtension = body.asFormUrlEncoded().get("fileExtension")[0].equals("") ? "txt" : body.asFormUrlEncoded().get("fileExtension")[0];
            Boolean uploaded = uploadedFile.renameTo(new File(account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension));
            if (!uploaded) {
                Logger.of("user." + account.getUserName()).error("Error upload file for user " + account.getUserName());
                return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
            }

            final UserFile newFile = new UserFile(account, fileName,
                    unique_name, body.asFormUrlEncoded().get("softwareTypeName")[0],
                    account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension,
                    fileDir.getAbsolutePath(), fileExtension);

            //Updating database UserFile <-> Account
            Ebean.save(newFile);
            Integer deleteAfter = Play.application().configuration().getInt("deleteAfter");
            if (deleteAfter > 0) {
                Akka.system().scheduler().scheduleOnce(
                        Duration.create(deleteAfter, TimeUnit.HOURS),
                        new Runnable() {
                            public void run() {
                                UserFile.asyncDeleteFile(newFile);
                            }
                        },
                        Akka.system().dispatcher()
                );
            }
            return ok(Json.toJson(new ServerResponse("success", "Successfully uploaded")));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.of("user." + account.getUserName()).error("Error while uploading new file for user : " + account.getUserName());
            return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
        }
    }

    public static Result delete() {
        //Identifying user using the Secure Social API
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        JsonNode request = request().body().asJson();

        File fileDir;
        File[] files;

        switch (request.findValue("action").asText()) {
            //Delete one file
            case "delete":
                String fileName = request.findValue("fileName").asText();
                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                if (file == null) {
                    Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +" have no file named " + fileName);
                    return forbidden(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                }
                fileDir = new File(file.getDirectoryPath());
                files = fileDir.listFiles();
                if (files == null) {
                    return ok(Json.toJson(new ServerResponse("error", "File does not exist")));
                }
                try {
                    UserFile.deleteFile(file);
                    return ok(Json.toJson(new ServerResponse("ok", "Successfully deleted")));
                } catch (Exception e) {
                    Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + "Error while deleting file " + fileName);
                    e.printStackTrace();
                    return ok(Json.toJson(new ServerResponse("error", "Error while deleteing file " + fileName)));
                }
            //Delete all files
            case "deleteAll":
                try {
                    for (UserFile f : account.getUserfiles()) {
                        UserFile.deleteFile(f);
                    }
                    return ok(Json.toJson(new ServerResponse("ok", "Successfully deleted")));
                } catch (Exception e) {
                    Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " Error while deleting files for  " + account.getUserName());
                    e.printStackTrace();
                    return ok(Json.toJson(new ServerResponse("error", "Error while deleting files")));
                }
            default:
                return badRequest(Json.toJson(new ServerResponse("error", "Invalid action")));
        }
    }

    public static Result files() {
        //Identifying user using the Secure Social API
        //Return meta information about all files
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        return ok(Json.toJson(account.getFilesInformation()));
    }

    public static Result accountInformation() {
        //Identifying user using the Secure Social API
        //Return account information
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        return ok(Json.toJson(new CacheServerResponse("ok", account.getAccountInformation())));
    }

    public static Result data() throws IOException {
        //Identifying user using the Secure Social API
        //Return data for chart
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        JsonNode request = request().body().asJson();
        if (!request.findValue("action").asText().equals("data")
                || request.findValue("fileName") == null
                || request.findValue("type") == null) {
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid Action")));
        }

        String fileName = request.findValue("fileName").asText();
        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
        String type = request.findValue("type").asText();
        try {
            CacheType cacheType = CacheType.findByType(type);
            if (cacheType.getSingle()) {
                return cache(file, cacheType.getCacheFileName(), account);
            } else {
                switch (type) {
                    case "summary":
                        return basicStats(account);
                    case "rarefaction":
                        return rarefaction(account);
                    default:
                        throw new IllegalArgumentException("Unknown type " + type);
                }
            }
        } catch (IllegalArgumentException | FileNotFoundException e) {
            e.printStackTrace();
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    ": error while requesting " + type + " data");
            return badRequest(Json.toJson(new ServerResponse("error", "Unknown type " + type)));
        }
    }

    private static Result cache(UserFile file, String cacheName, Account account) {
        //Return cache file transformed into json format
        if (file == null) {
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    " have no requested file");
            return badRequest(Json.toJson(new CacheServerResponse("error", "You have no requested file", null)));
        }
        if (file.isRendered()) {
            try {
                File jsonFile = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                FileInputStream fis = new FileInputStream(jsonFile);
                JsonNode jsonData = Json.parse(fis);
                return ok(Json.toJson(new CacheServerResponse("success", jsonData)));
            } catch (Exception e) {
                Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                        ": cache file does not exists [" + file.getFileName() + "," + cacheName + "]");
                return badRequest(Json.toJson(new CacheServerResponse("error", "Cache file does not exist", null)));
            }
        } else {
            Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " File: "
                    + file.getFileName() + " did not rendered");
            return badRequest(Json.toJson(new CacheServerResponse("error", "File did not rendered yet")));
        }
    }

    private static Result basicStats(Account account) throws FileNotFoundException {
        //Return basicStats cache for all files in json format

        if (account.getFilesCount() == 0) {
            return badRequest(Json.toJson(new ServerResponse("error", "You have no files")));
        }

        List<JsonNode> basicStatsData = new ArrayList<>();
        for (UserFile file : account.getUserfiles()) {
            if (file.isRendered() && !file.isRendering()) {
                File basicStatsCache = new File(file.getDirectoryPath() + "/basicStats.cache");
                FileInputStream cacheFile = new FileInputStream(basicStatsCache);
                JsonNode basicStatsNode = Json.parse(cacheFile);
                basicStatsData.add(basicStatsNode);
            }
        }
        return ok(Json.toJson(new CacheServerResponse("success", basicStatsData)));
    }

    public static class Point {
        public double x;
        public double y;
    }

    public static class RarefactionLineCache {
        public List<Point> values;
        public String key;
        public boolean area;
        public boolean hideTooltip;
        public String color;
        public boolean dash;
        public int x_start;
    }

    public static class RarefactionCache {
        public Map<Long, Long> freqTableCache;
        public RarefactionLineCache line;
        public RarefactionLineCache areaLine;
    }

    private static Result rarefaction(Account account) throws IOException {
        //Return rarefaction cache for all files in json format
        List<JsonNode> rarefactionData = new ArrayList<>();
        Long maxCount = UserFile.getMaxSampleCount();
        int count = 0;

        if (account.getFilesCount() == 0) {
            return badRequest(Json.toJson(new ServerResponse("error", "You have no files")));
        }

        for (UserFile file : account.getUserfiles()) {
            if (file.isRendered() && !file.isRendering()) {
                FileInputStream cacheFile = new FileInputStream(new File(file.getDirectoryPath() + "/rarefaction.cache"));
                ObjectMapper objectMapper = new ObjectMapper();
                RarefactionCache rarefactionCache = objectMapper.readValue(cacheFile, RarefactionCache.class);
                rarefactionCache.line.color = RarefactionColor.getColor(count++);
                if (file.getSampleCount() < maxCount) {
                    FrequencyTable frequencyTable = new FrequencyTable(rarefactionCache.freqTableCache);
                    Rarefaction rarefaction = new Rarefaction(frequencyTable);
                    ArrayList<Rarefaction.RarefactionPoint> extrapolate = rarefaction.extrapolate(maxCount, 20);
                    RarefactionLine additionalLine = new RarefactionLine(file.getFileName() + "_rarefaction_add_line", rarefactionCache.line.color, false, true, true, extrapolate.get(0).x);
                    for (Rarefaction.RarefactionPoint rarefactionPoint : extrapolate) {
                        additionalLine.addPoint(rarefactionPoint.x, rarefactionPoint.mean);
                    }
                    RarefactionLine additionalAreaLine = new RarefactionLine(file.getFileName() + "rarefaction_add_area", "#dcdcdc", true, true, true, extrapolate.get(0).x);

                    for (Rarefaction.RarefactionPoint rarefactionPoint : extrapolate) {
                        additionalAreaLine.addPoint(rarefactionPoint.x, rarefactionPoint.ciL);
                    }
                    for (int i = extrapolate.size() - 1; i >= 0; --i) {
                        additionalAreaLine.addPoint(extrapolate.get(i).x, extrapolate.get(i).ciU);
                    }
                    rarefactionData.add(Json.toJson(additionalLine));
                    rarefactionData.add(Json.toJson(additionalAreaLine));
                }
                rarefactionData.add(Json.toJson(rarefactionCache.areaLine));
                rarefactionData.add(Json.toJson(rarefactionCache.line));
            }
        }
        return ok(Json.toJson(new CacheServerResponse("success", rarefactionData)));
    }

    public static WebSocket<JsonNode> ws() {
        //Socket for updating information about computation progress
        LocalUser localUser = LocalUser.find.byId(SecureSocial.currentUser().identityId().userId());
        final Account account = localUser.account;
        return new WebSocket<JsonNode>() {

            @Override
            public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
                in.onMessage(new F.Callback<JsonNode>() {
                    public void invoke(JsonNode event) {
                        switch (event.findValue("action").asText()) {
                            case "render" :
                                String fileName = event.findValue("data").findValue("fileName").asText();
                                if (fileName == null) {
                                    out.write(Json.toJson(new WSResponse("error", "render", "Unknown", "Missing file name")));
                                    return;
                                }

                                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                                if (file == null) {
                                    out.write(Json.toJson(new WSResponse("error", "render", fileName, "You have no file named " + fileName)));
                                    return;
                                }
                                file.changeRenderingState(true);
                                Ebean.update(file);
                                try {
                                    //Trying to render cache files for sample
                                    ComputationUtil computationUtil = new ComputationUtil(file, out);
                                    computationUtil.createSampleCache();
                                    return;
                                } catch (Exception e) {
                                    //On exception delete file and inform user about fail
                                    Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " Error while rendering file " + file.getFileName());
                                    out.write(Json.toJson(new WSResponse("error", "render", fileName, "Error while rendering")));
                                    UserFile.deleteFile(file);
                                    e.printStackTrace();
                                    return;
                                }
                            default:
                                Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " Render: unknown type");
                                out.write(Json.toJson(new WSResponse("error", "render", "", "Unknown action")));
                                out.close();
                        }
                    }
                });
            }
        };
    }
}
