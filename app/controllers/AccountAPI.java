package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.RarefactionChartMultiple.RarefactionChart;
import models.Account;
import models.IPAddress;
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
import sun.org.mozilla.javascript.json.JsonParser;
import utils.CacheType.CacheType;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;
import utils.server.CacheServerResponse;
import utils.server.Configuration;
import utils.server.ServerResponse;
import utils.server.WSResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SecureSocial.SecuredAction
public class AccountAPI extends Controller {

    public static Boolean checkIP() {
        String ip = request().remoteAddress();
        IPAddress ipAddress = IPAddress.findByIp(ip);
        if (ipAddress == null) {
            ipAddress = new IPAddress(ip, 1L, false);
            ipAddress.save();
            return true;
        } else {
            if (ipAddress.isBanned()) {
                return false;
            } else {
                ipAddress.count();
                return true;
            }
        }
    }

    public static Result account() {
        if (!checkIP()) {
            return badRequest();
        }
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(views.html.account.accountMainPage.render(localUser.getAccountUserName()));
    }

    public static Result upload() {

        if (!checkIP()) {
            return badRequest();
        }

        //Identifying user using the SecureSocial API
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;


        if (account == null) {
            return ok(Json.toJson(new ServerResponse("error", "Unknown error")));
        }

        //Checking files count;
        if (account.isMaxFilesCountExceeded()) {
            Logger.of("user." + account.getUserName()).info("User " + account.getUserName() + ": exceeded the limit of the number of files");
            return ok(Json.toJson(new ServerResponse("error", "You have exceeded the limit of the number of files")));

        }

        //Getting the file from request body
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            return ok(Json.toJson(new ServerResponse("error", "You should upload the file")));
        };

        if (account.getMaxFilesSize() > 0) {
            Long sizeMB = file.getFile().length() / 1024;
            if (sizeMB > account.getMaxFilesSize()) {
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

        String pattern = "^[a-zA-Z0-9_.+-]{1,40}$";
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

            try {
                newFile.setSampleCount();
            } catch (Exception e) {
                return ok(Json.toJson(new ServerResponse("error", "Error while rendering")));
            }

            //Long maxFileSize = Play.application().configuration().getLong("maxFileSize");;

            if (account.getMaxClonotypesCount() > 0) {
                if (newFile.getClonotypesCount() > account.getMaxClonotypesCount()) {
                    UserFile.cleanTemporaryFiles(newFile);
                    return ok(Json.toJson(new ServerResponse("error", "Number of clonotypes should be less than " + Configuration.getMaxClonotypesCount())));
                }
            }

            //Updating database UserFile <-> Account
            Ebean.save(newFile);
            return ok(Json.toJson(new ServerResponse("success", "Successfully uploaded")));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.of("user." + account.getUserName()).error("Error while uploading new file for user : " + account.getUserName());
            return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
        }
    }

    public static Result delete() {
        if (!checkIP()) {
            return badRequest();
        }
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
                    account.cleanAllFiles();
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
        if (!checkIP()) {
            return badRequest();
        }
        //Identifying user using the Secure Social API
        //Return meta information about all files
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        return ok(Json.toJson(account.getFilesInformation()));
    }

    public static Result accountInformation() {
        if (!checkIP()) {
            return badRequest();
        }
        //Identifying user using the Secure Social API
        //Return account information
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        return ok(Json.toJson(new CacheServerResponse("ok", account.getAccountInformation())));
    }

    public static Result data() throws Exception {
        if (!checkIP()) {
            return badRequest();
        }
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
                        Boolean needToCreateNew = request.findValue("new").asBoolean();
                        return rarefaction(account, needToCreateNew);
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
        if (!checkIP()) {
            return badRequest();
        }
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
                e.printStackTrace();
                Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                        ": cache file does not exists [" + file.getFileName() + "," + cacheName + "]");
                return ok(Json.toJson(new CacheServerResponse("error", "Cache file does not exist", null)));
            }
        } else {
            Logger.of("user." + account.getUserName()).error("User: " + account.getUserName() + " File: "
                    + file.getFileName() + " did not rendered");
            return ok(Json.toJson(new CacheServerResponse("error", "File did not rendered yet")));
        }
    }

    private static Result basicStats(Account account) throws FileNotFoundException {
        if (!checkIP()) {
            return badRequest();
        }
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

    private static Result rarefaction(Account account, Boolean needToCreateNew) throws Exception {
        if (!checkIP()) {
            return badRequest();
        }

        if (account.getFilesCount() == 0) {
            return badRequest(Json.toJson(new ServerResponse("error", "You have no files")));
        }

        RarefactionChart rarefactionChart = new RarefactionChart(account);
        return ok(Json.toJson(new CacheServerResponse("success", rarefactionChart.create(needToCreateNew))));
    }

    public static WebSocket<JsonNode> ws() {
        if (!checkIP()) {
            return null;
        }
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
                                file.rendering();
                                Ebean.update(file);
                                try {
                                    //Trying to render cache files for sample
                                    ComputationUtil computationUtil = new ComputationUtil(file, out);
                                    computationUtil.createSampleCache();
                                    out.close();
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
