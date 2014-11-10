package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.ArrayUtils.Data;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.util.*;


@SecureSocial.SecuredAction
public class API extends Controller {

    public static Result upload() {

        /**
         * Identifying user using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, String> serverResponse = new HashMap<>();

        if (account == null) {
            serverResponse.put("result", "error");
            serverResponse.put("message", "Unknown error");
            return ok(Json.toJson(serverResponse));
        }

        Logger.of("user." + account.userName).info("User " + account.userName + " is uploading new file");

        /**
         * Checking files count
         */

        if (account.userfiles.size() >= 10) {
            serverResponse.put("result", "error");
            serverResponse.put("message", "You have exceeded the limit of the number of files");
            Logger.of("user." + account.userName).info("User" + account.userName + " exceeded  the limit of the number of files");
            return ok(Json.toJson(serverResponse));
        }

        /**
         * Getting the file from request body
         */

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            serverResponse.put("result", "error");
            serverResponse.put("message", "You should upload file");
            return ok(Json.toJson(serverResponse));
        }

        /**
         * Getting fileName
         * If User do not enter the name of file
         * it will be fills automatically using current file name
         */

        String fileName;
        if (file.getFilename().equals("")) {
            fileName = FilenameUtils.removeExtension(file.getFilename());
        } else {
            fileName = body.asFormUrlEncoded().get("fileName")[0];
        }

        String pattern = "^[a-zA-Z0-9_.-]{1,20}$";
        if (!fileName.matches(pattern)) {
            serverResponse.put("result", "error");
            serverResponse.put("message", "Invalid name");
            return ok(Json.toJson(serverResponse));
        }


        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.fileName.equals(fileName)) {
                serverResponse.put("result", "error");
                serverResponse.put("message", "You should use unique names for your files");
                return ok(Json.toJson(serverResponse));
            }
        }

        /**
         * Verification of the existence the account and the file
         */

        try {

            /**
             * Creation the UserFile class
             */

            File uploadedFile = file.getFile();
            String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(5, CommonUtil.RandomStringGenerator.Mode.ALPHA);
            File fileDir = (new File(account.userDirPath + "/" + unique_name + "/"));

            /**
             * Trying to create file's directory
             */

            if (!fileDir.exists()) {
                Boolean created = fileDir.mkdir();
                if (!created) {
                    serverResponse.put("result", "error");
                    serverResponse.put("message", "Server currently unavailable");
                    Logger.of("user." + account.userName).error("Error creating file directory for user " + account.userName);
                    return ok(Json.toJson(serverResponse));
                }
            }

            String fileExtension = body.asFormUrlEncoded().get("fileExtension")[0].equals("") ? "txt" : body.asFormUrlEncoded().get("fileExtension")[0];
            Boolean uploaded = uploadedFile.renameTo(new File(account.userDirPath + "/" + unique_name + "/" + fileName + "." + fileExtension));
            if (!uploaded) {
                serverResponse.put("result", "error");
                serverResponse.put("message", "Server currently unavailable");
                Logger.of("user." + account.userName).error("Error upload file for user " + account.userName);
                return ok(Json.toJson(serverResponse));
            }

            UserFile newFile = new UserFile(account, fileName,
                    unique_name, body.asFormUrlEncoded().get("softwareTypeName")[0],
                    account.userDirPath + "/" + unique_name + "/" + fileName + "." + fileExtension,
                    fileDir.getAbsolutePath(), fileExtension);

            /**
             * Database updating with relationships
             * UserFile <-> Account
             */

            Ebean.save(newFile);
            serverResponse.put("result", "success");
            return ok(Json.toJson(serverResponse));
        } catch (Exception e) {
            serverResponse.put("result", "error");
            serverResponse.put("message", "Server currently unavailable");
            e.printStackTrace();
            Logger.of("user." + account.userName).error("Error while uploading new file for user : " + account.userName);
            return ok(Json.toJson(serverResponse));
        }
    }

    public static Result delete() {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, Object> serverResponse = new HashMap<>();
        JsonNode request = request().body().asJson();

        File fileDir;
        File[] files;
        Boolean deleted = false;

        switch (request.findValue("action").asText()) {
            case "delete":
                String fileName = request.findValue("fileName").asText();
                Logger.of("user." + account.userName).info("User " + account.userName + "is trying to delete file named " + fileName);
                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                if (file == null) {
                    Logger.of("user." + account.userName).error("User " + account.userName +" have no file named " + fileName);
                    serverResponse.put("result", "error");
                    serverResponse.put("message", "You have no file named " + fileName);
                    return forbidden(Json.toJson(serverResponse));
                }
                fileDir = new File(file.fileDirPath);
                files = fileDir.listFiles();
                if (files == null) {
                    serverResponse.put("result", "error");
                    serverResponse.put("message", "File does not exist");
                    return ok(Json.toJson(serverResponse));
                }
                try {
                    for (File cache : files) {
                        Files.deleteIfExists(cache.toPath());
                    }
                    if (fileDir.delete()) {
                        deleted = true;
                    }
                } catch (Exception e) {
                    Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + fileName);
                    e.printStackTrace();
                    serverResponse.put("result", "error");
                    serverResponse.put("message", "Error while deleting file " + fileName);
                    return ok(Json.toJson(serverResponse));
                }
                if (deleted) {
                    Ebean.delete(file);
                    Logger.of("user." + account.userName).info("User " + account.userName + " successfully deleted file named " + fileName);
                    serverResponse.put("result", "ok");
                    serverResponse.put("message", "Successfully deleted");
                    return ok(Json.toJson(serverResponse));
                } else {
                    serverResponse.put("result", "error");
                    serverResponse.put("message", "Error while deleting file " + fileName);
                    Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + fileName);
                    return ok(Json.toJson(serverResponse));
                }
            case "deleteAll":
                for (UserFile f: UserFile.findByAccount(account)) {
                    fileDir = new File(f.fileDirPath);
                    Logger.of("user." + account.userName).info("User " + account.userName + "is trying to delete file named " + f.fileName);
                    files = fileDir.listFiles();
                    deleted = false;
                    try {
                        if (files != null) {
                            for (File cache : files) {
                                Files.deleteIfExists(cache.toPath());
                            }
                        }
                        if (fileDir.delete()) {
                            deleted = true;
                        }
                    } catch (Exception e) {
                        Logger.of("user." + account.userName).error("User: " + account.userName + " Error while deleting file " + f.fileName);
                        e.printStackTrace();
                        serverResponse.put("result", "error");
                        serverResponse.put("message", "Error while deleting file " + f.fileName);
                        return ok(Json.toJson(serverResponse));
                    }
                    if (deleted) {
                        Ebean.delete(f);
                        Logger.of("user." + account.userName).info("User " + account.userName + " successfully deleted file named " + f.fileName);
                    } else {
                        serverResponse.put("result", "error");
                        serverResponse.put("message", "Error while deleting file " + f.fileName);
                        Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + f.fileName);
                        return ok(Json.toJson(serverResponse));
                    }
                }
                serverResponse.put("result", "ok");
                return ok(Json.toJson(serverResponse));
            default:
                serverResponse.put("result", "error");
                serverResponse.put("message", "Invalid action");
                return badRequest(Json.toJson(serverResponse));
        }
    }

    public static Result files() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        List<HashMap<String, Object>> files = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (UserFile file: account.userfiles) {
            HashMap<String, Object> fileInformation = new HashMap<>();
            names.add(file.fileName);
            fileInformation.put("fileName", file.fileName);
            fileInformation.put("softwareTypeName", file.softwareTypeName);
            if (file.rendered) {
                fileInformation.put("state", "rendered");
            } else if (file.rendering) {
                fileInformation.put("state", "rendering");
            } else {
                fileInformation.put("state", "wait");
            }
            files.add(fileInformation);
        }
        HashMap<String, Object> serverResponse = new HashMap<>();
        serverResponse.put("data", files);
        serverResponse.put("names", names);
        return ok(Json.toJson(serverResponse));
    }

    public static Result accountInformation() {

        /**
         * Identifying user using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        HashMap<String, Object> jsonResults = new HashMap<>();
        Data serverResponse = new Data(new String[]{"result", "data"});
        Data accountInformation = new Data(new String[]{"email", "firstName", "lastName", "userName", "filesCount"});
        accountInformation.addData(new Object[]{localUser.email, localUser.firstName, localUser.lastName, account.userName, account.userfiles.size()});
        serverResponse.addData(new Object[]{"ok", accountInformation.getData()});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static Result data() {

        /**
         * Identifying user using the SecureSocial API
         */

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
        switch (request.findValue("type").asText()) {
            case "vjusage" :
                return cache(file, "vjUsage", account);
            case "spectrotype" :
                return cache(file, "spectrotype", account);
            case "spectrotypeV" :
                return cache(file, "spectrotypeV", account);
            case "kernelDensity" :
                return cache(file, "kernelDensity", account);
            case "annotation" :
                return cache(file, "annotation", account);
            case "basicStats" :
                return basicStats(account);
            case "diversity" :
                return diversity(account);
            default:
                Logger.of("user." + account.userName).error("User " + account.userName +
                        ": unknown type: " + request.findValue("type").asText());
                serverResponse.addData(new Object[]{"error", "Unknown type"});
                return badRequest(Json.toJson(serverResponse.getData()));
        }
    }

    public static Result cache(UserFile file, String cacheName, Account account) {
        Data serverResponse = new Data(new String[]{"result", "message", "data"});
        if (file == null) {
            Logger.of("user." + account.userName).error("User " + account.userName +
                    " have no requested file");
            serverResponse.addData(new Object[]{"error", "You have no file named " + file.fileName, null});
            return forbidden(Json.toJson(serverResponse.getData()));
        }
        if (file.rendered) {
            try {
                File jsonFile = new File(file.fileDirPath + "/" + cacheName + ".cache");
                FileInputStream fis = new FileInputStream(jsonFile);
                JsonNode jsonData = Json.parse(fis);
                serverResponse.addData(new Object[]{"success", "", jsonData});
                return ok(Json.toJson(serverResponse.getData()));
            } catch (Exception e) {
                Logger.of("user." + account.userName).error("User " + account.userName +
                        ": cache file does not exists");
                serverResponse.addData(new Object[]{"error", "Cache file does not exist", null});
                return ok(Json.toJson(serverResponse.getData()));
            }
        } else {
            Logger.of("user." + account.userName).error("User: " + account.userName + " File: "
                    + file.fileName + " did not rendered");
            serverResponse.addData(new Object[]{"error", "File did not rendered", null});
            return ok(Json.toJson(serverResponse.getData()));
        }
    }

    public static Result basicStats(Account account) {
        List<JsonNode> basicStatsData = new ArrayList<>();
        Data serverResponse = new Data(new String[]{"result", "errors", "data", "message"});
        int errors = 0;
        for (UserFile file : account.userfiles) {
            if (file.rendered && !file.rendering) {
                File basicStatsCache = new File(file.fileDirPath + "/basicStats.cache");
                try {
                    FileInputStream cacheFile = new FileInputStream(basicStatsCache);
                    JsonNode basicStatsNode = Json.parse(cacheFile);
                    basicStatsData.add(basicStatsNode);
                } catch (Exception e) {
                    errors++;
                }
            }
        }
        serverResponse.addData(new Object[]{"success", errors, basicStatsData, ""});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static Result diversity(Account account) {
        List<JsonNode> diversityData = new ArrayList<>();
        Data serverResponse = new Data(new String[]{"result", "errors", "data", "message"});
        int errors = 0;
        for (UserFile file : account.userfiles) {
            if (file.rendered && !file.rendering) {
                File diversityCache = new File(file.fileDirPath + "/diversity.cache");
                try {
                    FileInputStream cacheFile = new FileInputStream(diversityCache);
                    JsonNode diversityNode = Json.parse(cacheFile);
                    diversityData.add(diversityNode);
                } catch (Exception e) {
                    errors++;
                }
            }
        }
        serverResponse.addData(new Object[]{"success", errors, diversityData, ""});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static WebSocket<JsonNode> ws() {

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
                                file.rendering = true;
                                Ebean.update(file);
                                try {
                                    ComputationUtil.createSampleCache(file, out);
                                    file.rendered = true;
                                    file.rendering = false;
                                    Ebean.update(file);
                                    return;
                                } catch (Exception e) {
                                    Logger.of("user." + account.userName).error("User: " + account.userName + " Error while rendering file " + file.fileName);
                                    serverResponse.addData(new Object[]{"error", "render", fileName, "Error while rendering"});
                                    out.write(Json.toJson(serverResponse.getData()));
                                    CommonUtil.deleteFile(file, account);
                                    return;
                                }
                            default:
                                Logger.of("user." + account.userName).error("User: " + account.userName + " Render: unknown type");
                                serverResponse.addData(new Object[]{"error", "render", "", "Unknown Action"});
                                out.write(Json.toJson(serverResponse.getData()));
                        }
                    }
                });
            }
        };
    }
}
