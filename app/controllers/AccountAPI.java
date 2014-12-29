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
import utils.ArrayUtils.Data;
import utils.CacheType.CacheType;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;
import graph.RarefactionChart.RarefactionColor;

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

        Data serverResponse = new Data(new String[]{"result", "message"});

        if (account == null) {
            serverResponse.addData(new Object[]{"error", "Unknown error"});
            return ok(Json.toJson(serverResponse.getData()));
        }

        //Checking files count
        Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
        if (maxFilesCount > 0) {
            if (account.getFilesCount() >= maxFilesCount) {
                serverResponse.addData(new Object[]{"error", "You have exceeded the limit of the number of files"});
                Logger.of("user." + account.getUserName()).info("User " + account.getUserName() + ": exceeded the limit of the number of files");
                return ok(Json.toJson(serverResponse.getData()));
            }
        }

        //Getting the file from request body
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            serverResponse.addData(new Object[]{"error", "You should upload file"});
            return ok(Json.toJson(serverResponse.getData()));
        }
        Long maxFileSize = Play.application().configuration().getLong("maxFileSize");
        if (maxFileSize > 0) {
            Long sizeMB = file.getFile().length() / 1024;
            if (sizeMB > maxFileSize) {
                serverResponse.addData(new Object[]{"error", "File is too large"});
                return ok(Json.toJson(serverResponse.getData()));
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
            serverResponse.addData(new Object[]{"error", "Invalid name"});
            return ok(Json.toJson(serverResponse.getData()));
        }


        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.getFileName().equals(fileName)) {
                serverResponse.addData(new Object[]{"error", "You should use unique names for your files"});
                return ok(Json.toJson(serverResponse.getData()));
            }
        }

        try {

            File accountDir = new File(account.getDirectoryPath());
            if (!accountDir.exists()) {
                Boolean accountDirCreated = accountDir.mkdir();
                if (!accountDirCreated) {
                    serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
                    Logger.of("user." + account.getUserName()).error("Error creating main directory for user " + account.getUserName());
                    return ok(Json.toJson(serverResponse.getData()));
                }
            }

            File uploadedFile = file.getFile();
            String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(5, CommonUtil.RandomStringGenerator.Mode.ALPHA);
            File fileDir = (new File(account.getDirectoryPath() + "/" + unique_name + "/"));

            //Trying to create file directory
            if (!fileDir.exists()) {
                Boolean created = fileDir.mkdir();
                if (!created) {
                    serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
                    Logger.of("user." + account.getUserName()).error("Error creating file directory for user " + account.getUserName());
                    return ok(Json.toJson(serverResponse.getData()));
                }
            }

            String fileExtension = body.asFormUrlEncoded().get("fileExtension")[0].equals("") ? "txt" : body.asFormUrlEncoded().get("fileExtension")[0];
            Boolean uploaded = uploadedFile.renameTo(new File(account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension));
            if (!uploaded) {
                serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
                Logger.of("user." + account.getUserName()).error("Error upload file for user " + account.getUserName());
                return ok(Json.toJson(serverResponse.getData()));
            }

            final UserFile newFile = new UserFile(account, fileName,
                    unique_name, body.asFormUrlEncoded().get("softwareTypeName")[0],
                    account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension,
                    fileDir.getAbsolutePath(), fileExtension);

            //Updating database UserFile <-> Account
            Ebean.save(newFile);
            serverResponse.addData(new Object[]{"success", ""});
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
            return ok(Json.toJson(serverResponse.getData()));
        } catch (Exception e) {
            serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
            e.printStackTrace();
            Logger.of("user." + account.getUserName()).error("Error while uploading new file for user : " + account.getUserName());
            return ok(Json.toJson(serverResponse.getData()));
        }
    }

    public static Result delete() {
        //Identifying user using the Secure Social API
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        Data serverResponse = new Data(new String[]{"result", "message"});
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
                    serverResponse.addData(new Object[]{"error", "You have no file named " + fileName});
                    return forbidden(Json.toJson(serverResponse.getData()));
                }
                fileDir = new File(file.getDirectoryPath());
                files = fileDir.listFiles();
                if (files == null) {
                    serverResponse.addData(new Object[]{"error", "File does not exist"});
                    return ok(Json.toJson(serverResponse.getData()));
                }
                try {
                    UserFile.deleteFile(file);
                    serverResponse.addData(new Object[]{"ok", "Successfully deleted"});
                    return ok(Json.toJson(serverResponse.getData()));
                } catch (Exception e) {
                    Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + "Error while deleting file " + fileName);
                    e.printStackTrace();
                    serverResponse.addData(new Object[]{"error", "Error while deleting file " + fileName});
                    return ok(Json.toJson(serverResponse.getData()));
                }
            //Delete all files
            case "deleteAll":
                try {
                    for (UserFile f : account.getUserfiles()) {
                        UserFile.deleteFile(f);
                    }
                    serverResponse.addData(new Object[]{"ok", ""});
                    return ok(Json.toJson(serverResponse.getData()));
                } catch (Exception e) {
                    Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " Error while deleting files for  " + account.getUserName());
                    e.printStackTrace();
                    serverResponse.addData(new Object[]{"error", "Error while deleting files"});
                    return ok(Json.toJson(serverResponse.getData()));
                }
            default:
                serverResponse.addData(new Object[]{"error", "Invalid action"});
                return badRequest(Json.toJson(serverResponse.getData()));
        }
    }

    public static Result files() {
        //Identifying user using the Secure Social API
        //Return meta information about all files
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        List<HashMap<String, Object>> files = new ArrayList<>();
        for (UserFile file: account.getUserfiles()) {
            HashMap<String, Object> fileInformation = new HashMap<>();
            fileInformation.put("fileName", file.getFileName());
            fileInformation.put("softwareTypeName", file.getSoftwareTypeName());
            if (file.isRendered()) {
                fileInformation.put("state", "rendered");
            } else if (file.isRendering()) {
                fileInformation.put("state", "rendering");
            } else {
                fileInformation.put("state", "wait");
            }
            files.add(fileInformation);
        }
        Data serverResponse = new Data(new String[]{"files", "maxFileSize", "maxFilesCount"});
        Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
        Integer maxFileSize = Play.application().configuration().getInt("maxFileSize");
        serverResponse.addData(new Object[]{files, maxFileSize, maxFilesCount});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static Result accountInformation() {
        //Identifying user using the Secure Social API
        //Return account information
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        Data serverResponse = new Data(new String[]{"result", "data"});
        Data accountInformation = new Data(new String[]{"email", "firstName", "lastName", "userName", "filesCount"});
        accountInformation.addData(new Object[]{localUser.email, localUser.firstName, localUser.lastName, account.getUserName(), account.getFilesCount()});
        serverResponse.addData(new Object[]{"ok", accountInformation.getData()});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static Result data() throws IOException {
        //Identifying user using the Secure Social API
        //Return data for chart
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        Data serverResponse = new Data(new String[]{"result", "message"});
        JsonNode request = request().body().asJson();
        if (!request.findValue("action").asText().equals("data")
                || request.findValue("fileName") == null
                || request.findValue("type") == null) {
            serverResponse.addData(new Object[]{"error", "Invalid action"});
            return badRequest(Json.toJson(serverResponse.getData()));
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
            serverResponse.addData(new Object[]{"error", "Unknown type " + type});
            return badRequest(Json.toJson(serverResponse.getData()));
        }
    }

    private static Result cache(UserFile file, String cacheName, Account account) {
        //Return cache file transformed into json format
        Data serverResponse = new Data(new String[]{"result", "message", "data"});
        if (file == null) {
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    " have no requested file");
            serverResponse.addData(new Object[]{"error", "You have no this file", null});
            return forbidden(Json.toJson(serverResponse.getData()));
        }
        if (file.isRendered()) {
            try {
                File jsonFile = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                FileInputStream fis = new FileInputStream(jsonFile);
                JsonNode jsonData = Json.parse(fis);
                serverResponse.addData(new Object[]{"success", "", jsonData});
                return ok(Json.toJson(serverResponse.getData()));
            } catch (Exception e) {
                Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                        ": cache file does not exists [" + file.getFileName() + "," + cacheName + "]");
                serverResponse.addData(new Object[]{"error", "Cache file does not exist", null});
                return forbidden(Json.toJson(serverResponse.getData()));
            }
        } else {
            Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " File: "
                    + file.getFileName() + " did not rendered");
            serverResponse.addData(new Object[]{"error", "File did not rendered", null});
            return forbidden(Json.toJson(serverResponse.getData()));
        }
    }

    private static Result basicStats(Account account) throws FileNotFoundException {
        //Return basicStats cache for all files in json format
        List<JsonNode> basicStatsData = new ArrayList<>();
        Data serverResponse = new Data(new String[]{"result", "data"});
        for (UserFile file : account.getUserfiles()) {
            if (file.isRendered() && !file.isRendering()) {
                File basicStatsCache = new File(file.getDirectoryPath() + "/basicStats.cache");
                FileInputStream cacheFile = new FileInputStream(basicStatsCache);
                JsonNode basicStatsNode = Json.parse(cacheFile);
                basicStatsData.add(basicStatsNode);
            }
        }
        serverResponse.addData(new Object[]{"success", basicStatsData});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static class Points {
        public double x;
        public double y;
    }

    public static class RarefactionLineCache {
        public List<Points> values;
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
        Data serverResponse = new Data(new String[]{"result", "data"});
        Long maxCount = UserFile.getMaxSampleCount();
        int count = 0;
        for (UserFile file : account.getUserfiles()) {
            if (file.isRendered() && !file.isRendering()) {
                FileInputStream cacheFile = new FileInputStream(new File(file.getDirectoryPath() + "/rarefaction.cache"));
                ObjectMapper objectMapper = new ObjectMapper();
                RarefactionCache rarefactionCache = objectMapper.readValue(cacheFile, RarefactionCache.class);
                rarefactionCache.line.color = RarefactionColor.getColor(count++);
                if (file.getSampleCount() < maxCount) {
                    FrequencyTable frequencyTable = new FrequencyTable(rarefactionCache.freqTableCache);
                    Rarefaction rarefaction = new Rarefaction(frequencyTable);
                    ArrayList<Rarefaction.RarefactionPoint> extrapolate = rarefaction.extrapolate(maxCount, 5);
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
        serverResponse.addData(new Object[]{"success", rarefactionData});
        return ok(Json.toJson(serverResponse.getData()));
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
                        Data serverResponse = new Data(new String[]{"result", "action", "fileName", "message"});
                        switch (event.findValue("action").asText()) {
                            case "render" :
                                String fileName = event.findValue("data").findValue("fileName").asText();
                                if (fileName == null) {
                                    serverResponse.addData(new Object[]{"error", "render", "", "Missing file name"});
                                    out.write(Json.toJson(serverResponse.getData()));
                                    return;
                                }

                                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                                if (file == null) {
                                    serverResponse.addData(new Object[]{"error", "render", fileName, "You have no file named " + fileName});
                                    out.write(Json.toJson(serverResponse.getData()));
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
                                    serverResponse.addData(new Object[]{"error", "render", fileName, "Error while rendering"});
                                    out.write(Json.toJson(serverResponse.getData()));
                                    UserFile.deleteFile(file);
                                    e.printStackTrace();
                                    return;
                                }
                            default:
                                Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " Render: unknown type");
                                serverResponse.addData(new Object[]{"error", "render", "", "Unknown Action"});
                                out.write(Json.toJson(serverResponse.getData()));
                                out.close();
                        }
                    }
                });
            }
        };
    }
}
