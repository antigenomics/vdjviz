package controllers;

import com.antigenomics.vdjtools.Software;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@SecureSocial.SecuredAction
public class API extends Controller {

    public static Result uploadFile() {
        /**
         * Identifying user using the SecureSocial API
         */
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, String> resultJson = new HashMap<>();

        if (account == null) {
            resultJson.put("error", "Unknow Error");
            return ok(Json.toJson(resultJson));
        }
        Logger.of("user." + account.userName).info("User " + account.userName + " is uploading new file");

        /**
         * Checking files count
         */

        if (account.userfiles.size() >= 10) {
            resultJson.put("error", "You have exceeded the limit of the number of files");
            Logger.of("user." + account.userName).info("User" + account.userName + " exceeded  the limit of the number of files");
            return ok(Json.toJson(resultJson));
        }

        /**
         * Getting the file from request body
         */

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("files[]");

        if (file == null) {
            resultJson.put("error", "You should upload file");
            return ok(Json.toJson(resultJson));
        }

        /**
         * Getting fileName
         * If User do not enter the name of file
         * it will be fills automatically using current file name
         */

        String fileName = null;
        //TODO
        if (file.getFilename().equals("")) {
            fileName = FilenameUtils.removeExtension(file.getFilename());
        } else {
            fileName = body.asFormUrlEncoded().get("fileName")[0];
        }

        String pattern = "^[a-zA-Z0-9_.-]{1,20}$";
        if (!fileName.matches(pattern)) {
            resultJson.put("error", "Invalid name");
            return ok(Json.toJson(resultJson));
        }


        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.fileName.equals(fileName)) {
                resultJson.put("error", "You should use unique names for your files");
                return ok(Json.toJson(resultJson));
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
            String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
            File fileDir = (new File(account.userDirPath + "/" + unique_name + "/"));

            /**
             * Trying to create file's directory
             * if failed redirect to the account
             */

            if (!fileDir.exists()) {
                Boolean created = fileDir.mkdir();
                if (!created) {
                    resultJson.put("error", "Server currently unavailable");
                    Logger.of("user." + account.userName).error("Error creating file directory for user " + account.userName);
                    return ok(Json.toJson(resultJson));
                }
            }

            /**
             * Checking
             */

            Boolean uploaded = uploadedFile.renameTo(new File(account.userDirPath + "/" + unique_name + "/file"));
            if (!uploaded) {
                resultJson.put("error", "Server currently unavailable");
                Logger.of("user." + account.userName).error("Error upload file for user " + account.userName);
                return ok(Json.toJson(resultJson));
            }

            UserFile newFile = new UserFile(account, fileName,
                    unique_name, body.asFormUrlEncoded().get("softwareTypeName")[0],
                    account.userDirPath + "/" + unique_name + "/file",
                    fileDir.getAbsolutePath());

            /**
             * Database updating with relationships
             * UserFile <-> Account
             */

            Ebean.save(newFile);
            account.userfiles.add(newFile);
            Ebean.update(account);
            resultJson.put("success", "success");
            return ok(Json.toJson(resultJson));
        } catch (Exception e) {
            resultJson.put("error", "Unknown error");
            e.printStackTrace();
            Logger.of("user." + account.userName).error("Error while uploading new file for user : " + account.userName);
            return ok(Json.toJson(resultJson));
        }
    }

    public static Result deleteFile() {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, Object> jsonResults = new HashMap<>();
        JsonNode jsonData = request().body().asJson();

        if (!jsonData.findValue("action").asText().equals("delete")) {
            jsonResults.put("result", "error");
            jsonResults.put("message", "Invalid action");
            return badRequest(Json.toJson(jsonResults));
        }

        String fileName = jsonData.findValue("fileName").asText();
        Logger.of("user." + account.userName).info("User " + account.userName + "is trying to delete file named " + fileName);
        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
        if (file == null) {
            Logger.of("user." + account.userName).error("User " + account.userName +" have no file named " + fileName);
            jsonResults.put("result", "error");
            jsonResults.put("message", "You have no file named " + fileName);
            return forbidden(Json.toJson(jsonResults));
        }

        /**
         * Getting file directory
         * and try to delete it
         */

        String fileDirectoryName =  account.userDirPath + "/" + file.uniqueName;

        File f = new File(fileDirectoryName + "/file");
        File histogram = new File(fileDirectoryName + "/histogram.cache");
        File histogramV = new File(fileDirectoryName + "/histogramV.cache");
        File vdjUsage = new File(fileDirectoryName + "/vdjUsage.cache");
        File annotation = new File(fileDirectoryName + "/annotation.cache");
        File basicStats = new File(fileDirectoryName + "/basicStats.cache");
        File diversity = new File(fileDirectoryName + "/diversity.cache");
        File fileDir = new File(fileDirectoryName + "/");
        Boolean deleted = false;
        try {
            f.delete();
            annotation.delete();
            basicStats.delete();
            histogram.delete();
            histogramV.delete();
            vdjUsage.delete();
            diversity.delete();
            if (fileDir.delete()) {
                deleted = true;
            }
        } catch (Exception e) {
            Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + fileName);
            e.printStackTrace();
            jsonResults.put("result", "error");
            jsonResults.put("message", "Error while deleting file " + fileName);
            return ok(Json.toJson(jsonResults));
        }
        if (deleted) {
            Ebean.delete(file);
            Logger.of("user." + account.userName).info("User " + account.userName + " successfully deleted file named " + fileName);
            jsonResults.put("result", "ok");
            jsonResults.put("message", "Successfully deleted");
            return ok(Json.toJson(jsonResults));
        } else {
            jsonResults.put("result", "error");
            jsonResults.put("message", "Error while deleting file " + fileName);
            Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + fileName);
            return ok(Json.toJson(jsonResults));
        }
    }

    public static Result getAccountAllFilesInformation() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        List<HashMap<String, Object>> fileNames = new ArrayList<>();
        for (UserFile file: account.userfiles) {
            HashMap<String, Object> fileInformation = new HashMap<>();
            fileInformation.put("fileName", file.fileName);
            fileInformation.put("softwareTypeName", file.softwareTypeName);
            fileInformation.put("rendered", file.rendered);
            fileInformation.put("rendering", file.rendering);
            fileInformation.put("renderCount", file.renderCount);
            fileNames.add(fileInformation);
        }
        return ok(Json.toJson(fileNames));
    }

    public static Result getFileInformation() {
        /**
         * Identifying user using the SecureSocial API
         */
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, Object> jsonResults = new HashMap<>();
        JsonNode jsonData = request().body().asJson();

        if (!jsonData.findValue("action").asText().equals("fileInformation")) {
            jsonResults.put("result", "error");
            jsonResults.put("message", "Invalid action");
            return badRequest(Json.toJson(jsonResults));
        }

        String fileName = jsonData.findValue("fileName").asText();

        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

        if (file == null) {
            jsonResults.put("result", "error");
            return forbidden(Json.toJson(jsonResults));
        }

        jsonResults.put("result", "ok");
        HashMap<String, Object> fileInformationNode = new HashMap<>();
        fileInformationNode.put("fileName", file.fileName);
        fileInformationNode.put("softwareTypeName", file.softwareTypeName);
        fileInformationNode.put("renderCount", file.renderCount);
        fileInformationNode.put("rendered", file.rendered);
        fileInformationNode.put("rendering", file.rendering);
        jsonResults.put("data", fileInformationNode);
        return ok(Json.toJson(jsonResults));
    }

    public static Result getAccountInformation() {

        /**
         * Identifying user using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        HashMap<String, Object> jsonResults = new HashMap<>();

        jsonResults.put("result", "ok");
        HashMap<String, Object> accountInformation = new HashMap<>();

        accountInformation.put("email", localUser.email);
        accountInformation.put("firstName", localUser.firstName);
        accountInformation.put("lastName", localUser.lastName);
        accountInformation.put("userName", account.userName);
        accountInformation.put("filesCount", account.userfiles.size());
        jsonResults.put("data", accountInformation);
        return ok(Json.toJson(jsonResults));
    }

    public static Result returnVdjUsageData(String fileName) throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * vdjUsage data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/vdjUsage.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
    }

    public static Result returnSpectrotypeHistogram(String fileName) throws JsonProcessingException, FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/histogram.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
    }

    public static Result returnSpectrotypeVHistogram(String fileName) throws JsonProcessingException, FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/histogramV.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
    }

    public static Result returnAnnotationData(String fileName) throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (file != null && !localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file != null && file.rendered) {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            FileInputStream jsonFile = new FileInputStream(annotationCacheFile);
            return ok(Json.parse(jsonFile));
        }
        return ok();
    }

    public static Result returnBasicStats() throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        List<JsonNode> basicStatsAll = new ArrayList<>();

        /**
         * Getting jsonNode of BasicStats for each file
         * is belonging to user
         */

        for (UserFile file : localAccount.userfiles) {
            if (file.rendered && !file.rendering) {
                File basicStatsCache = new File(file.fileDirPath + "/basicStats.cache");
                if (basicStatsCache.exists()) {
                    FileInputStream jsonFile = new FileInputStream(basicStatsCache);
                    JsonNode basicStatsNode = Json.parse(jsonFile);
                    basicStatsAll.add(basicStatsNode.get(0));
                }
            }
        }
        return ok(Json.toJson(basicStatsAll));

    }

    public static Result returnDiversity() throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        List<JsonNode> diversityAll = new ArrayList<>();

        /**
         * Getting jsonNode of diversity for each file
         * is belonging to user
         */

        for (UserFile file : localAccount.userfiles) {
            if (file.rendered && !file.rendering) {
                File diversityCache = new File(file.fileDirPath + "/diversity.cache");
                if (diversityCache.exists()) {
                    FileInputStream jsonFile = new FileInputStream(diversityCache);
                    JsonNode basicStatsNode = Json.parse(jsonFile);
                    diversityAll.add(basicStatsNode);
                }
            }
        }
        return ok(Json.toJson(diversityAll));

    }



}
