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

        Data serverResponse = new Data(new String[]{"result", "message"});

        if (account == null) {
            serverResponse.addData(new Object[]{"error", "Unknown error"});
            return ok(Json.toJson(serverResponse.getData()));
        }

        /**
         * Checking files count
         */

        if (account.userfiles.size() >= 25) {
            serverResponse.addData(new Object[]{"error", "You have exceeded the limit of the number of files"});
            Logger.of("user." + account.userName).info("User" + account.userName + " exceeded  the limit of the number of files");
            return ok(Json.toJson(serverResponse.getData()));
        }

        /**
         * Getting the file from request body
         */

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            serverResponse.addData(new Object[]{"error", "You should upload file"});
            return ok(Json.toJson(serverResponse.getData()));
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
            serverResponse.addData(new Object[]{"error", "Invalid name"});
            return ok(Json.toJson(serverResponse.getData()));
        }


        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.fileName.equals(fileName)) {
                serverResponse.addData(new Object[]{"error", "You should use unique names for your files"});
                return ok(Json.toJson(serverResponse.getData()));
            }
        }

        /**
         * Verification of the existence the account and the file
         */

        try {

            /**
             * Creation the UserFile class
             */

            File accountDir = new File(account.userDirPath);
            if (!accountDir.exists()) {
                accountDir.mkdir();
            }

            File uploadedFile = file.getFile();
            String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(5, CommonUtil.RandomStringGenerator.Mode.ALPHA);
            File fileDir = (new File(account.userDirPath + "/" + unique_name + "/"));

            /**
             * Trying to create file's directory
             */

            if (!fileDir.exists()) {
                Boolean created = fileDir.mkdir();
                if (!created) {
                    serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
                    Logger.of("user." + account.userName).error("Error creating file directory for user " + account.userName);
                    return ok(Json.toJson(serverResponse.getData()));
                }
            }

            String fileExtension = body.asFormUrlEncoded().get("fileExtension")[0].equals("") ? "txt" : body.asFormUrlEncoded().get("fileExtension")[0];
            Boolean uploaded = uploadedFile.renameTo(new File(account.userDirPath + "/" + unique_name + "/" + fileName + "." + fileExtension));
            if (!uploaded) {
                serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
                Logger.of("user." + account.userName).error("Error upload file for user " + account.userName);
                return ok(Json.toJson(serverResponse.getData()));
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
            serverResponse.addData(new Object[]{"success", ""});
            return ok(Json.toJson(serverResponse.getData()));
        } catch (Exception e) {
            serverResponse.addData(new Object[]{"error", "Server currently unavailable"});
            e.printStackTrace();
            Logger.of("user." + account.userName).error("Error while uploading new file for user : " + account.userName);
            return ok(Json.toJson(serverResponse.getData()));
        }
    }

    public static Result delete() {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        Data serverResponse = new Data(new String[]{"result", "message"});
        JsonNode request = request().body().asJson();

        File fileDir;
        File[] files;

        switch (request.findValue("action").asText()) {
            case "delete":
                String fileName = request.findValue("fileName").asText();
                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                if (file == null) {
                    Logger.of("user." + account.userName).error("User " + account.userName +" have no file named " + fileName);
                    serverResponse.addData(new Object[]{"error", "You have no file named " + fileName});
                    return forbidden(Json.toJson(serverResponse.getData()));
                }
                fileDir = new File(file.fileDirPath);
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
                    Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + fileName);
                    e.printStackTrace();
                    serverResponse.addData(new Object[]{"error", "Error while deleting file " + fileName});
                    return ok(Json.toJson(serverResponse.getData()));
                }
            case "deleteAll":
                try {
                    for (UserFile f : account.userfiles) {
                        UserFile.deleteFile(f);
                    }
                    serverResponse.addData(new Object[]{"ok", ""});
                    return ok(Json.toJson(serverResponse.getData()));
                } catch (Exception e) {
                    Logger.of("user." + account.userName).error("User: " + account.userName + " Error while deleting files for  " + account.userName);
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
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        List<HashMap<String, Object>> files = new ArrayList<>();
        for (UserFile file: account.userfiles) {
            HashMap<String, Object> fileInformation = new HashMap<>();
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
        return ok(Json.toJson(files));
    }

    public static Result accountInformation() {

        /**
         * Identifying user using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
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
            case "sizeClassifying":
                return cache(file, "sizeClassifying",account);
            case "annotation" :
                return cache(file, "annotation", account);
            case "summary" :
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
            serverResponse.addData(new Object[]{"error", "You have no this file", null});
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
        Data serverResponse = new Data(new String[]{"result", "errors", "data"});
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
        serverResponse.addData(new Object[]{"success", errors, basicStatsData});
        return ok(Json.toJson(serverResponse.getData()));
    }

    public static Result diversity(Account account) {
        List<JsonNode> diversityData = new ArrayList<>();
        Data serverResponse = new Data(new String[]{"result", "errors", "data"});
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
        serverResponse.addData(new Object[]{"success", errors, diversityData});
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
                                    ComputationUtil computationUtil = new ComputationUtil(file, out);
                                    computationUtil.createSampleCache();
                                    return;
                                } catch (Exception e) {
                                    Logger.of("user." + account.userName).error("User: " + account.userName + " Error while rendering file " + file.fileName);
                                    serverResponse.addData(new Object[]{"error", "render", fileName, "Error while rendering"});
                                    out.write(Json.toJson(serverResponse.getData()));
                                    UserFile.deleteFile(file);
                                    e.printStackTrace();
                                    return;
                                }
                            default:
                                Logger.of("user." + account.userName).error("User: " + account.userName + " Render: unknown type");
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
