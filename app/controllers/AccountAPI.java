package controllers;

import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.AnnotationTable.AnnotationTable;
import graph.RarefactionChartMultiple.RarefactionChart;
import graph.SearchClonotypes.SearchClonotypes;
import models.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.CacheType.CacheType;
import utils.CommonUtil;
import utils.ComputationUtil;
import utils.server.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SecureSocial.SecuredAction
public class AccountAPI extends Controller {

    public static Account getCurrentAccount() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return localUser.account;
    }

    public static Result account() {
        Account account = getCurrentAccount();
        Account.AccountInformation accountInformation = Account.getAccountInformation(account);
        return ok(views.html.account.accountMainPage.render(false, null, accountInformation));
    }

    public static Result log() {
        Account account = getCurrentAccount();
        JsonNode request = request().body().asJson();
        if (!request.has("message")) {
            LogAggregator.logServerError("User: " + account.getUserName() + ": error while client logging", account);
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid Action")));
        }
        LogAggregator.logClientError(request.get("message").asText(), account);
        return ok();
    }

    public static F.Promise<Result> upload() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();


                if (account == null) {
                    return ok(Json.toJson(new ServerResponse("error", "Unknown error")));
                }

                //Checking files count;
                if (account.isMaxFilesCountExceeded()) {
                    LogAggregator.logServerError("Exceeded the limit of the number of files", account);
                    return ok(Json.toJson(new ServerResponse("error", "You have exceeded the allowed number of samples. Remove some files to continue")));

                }

                //Getting the file from request body
                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart file = body.getFile("files[]");

                if (file == null) {
                    return ok(Json.toJson(new ServerResponse("error", "You should upload the file first")));
                }
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
                for (UserFile userFile : allFiles) {
                    if (userFile.getFileName().equals(fileName)) {
                        return ok(Json.toJson(new ServerResponse("error", "You should use unique names for your files")));
                    }
                }

                try {

                    File accountDir = new File(account.getDirectoryPath());
                    if (!accountDir.exists()) {
                        Boolean accountDirCreated = accountDir.mkdir();
                        if (!accountDirCreated) {
                            LogAggregator.logServerError("Error creating main directory", account);
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
                            LogAggregator.logServerError("Error creating file directory", account);
                            return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
                        }
                    }

                    String fileExtension = body.asFormUrlEncoded().get("fileExtension")[0].equals("") ? "txt" : body.asFormUrlEncoded().get("fileExtension")[0];
                    Boolean uploaded = uploadedFile.renameTo(new File(account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension));
                    if (!uploaded) {
                        LogAggregator.logServerError("Error while uploading new file", account);
                        return ok(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
                    }

                    final UserFile newFile = new UserFile(account, fileName,
                            unique_name, body.asFormUrlEncoded().get("softwareTypeName")[0],
                            account.getDirectoryPath() + "/" + unique_name + "/" + fileName + "." + fileExtension,
                            fileDir.getAbsolutePath(), fileExtension);


                    //Updating database UserFile <-> Account
                    Ebean.save(newFile);
                    return ok(Json.toJson(new ServerResponse("success", "Successfully uploaded")));
                } catch (Exception e) {
                    e.printStackTrace();
                    LogAggregator.logServerError("Error while uploading new file", account);
                    return ok(Json.toJson(new ServerResponse("error", "Error while uploading new file")));
                }
            }
        });
    }

    public static F.Promise<Result> delete() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();

                JsonNode request = request().body().asJson();

                File fileDir;
                File[] files;

                switch (request.findValue("action").asText()) {
                    //Delete one file
                    case "delete":
                        String fileName = request.findValue("fileName").asText();
                        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
                        if (file == null) {
                            LogAggregator.logServerError("User have no sample named " + fileName, account);
                            return forbidden(Json.toJson(new ServerResponse("error", "You have no sample named " + fileName)));
                        }
                        fileDir = new File(file.getDirectoryPath());
                        files = fileDir.listFiles();
                        if (files == null) {
                            return ok(Json.toJson(new ServerResponse("error", "Sample does not exist")));
                        }
                        try {
                            UserFile.deleteFile(file);
                            return ok(Json.toJson(new ServerResponse("ok", "Successfully deleted")));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogAggregator.logServerError("Error while deleting sample " + fileName, account);
                            return ok(Json.toJson(new ServerResponse("error", "Error while deleting sample " + fileName)));
                        }
                        //Delete all files
                    case "deleteAll":
                        try {
                            account.cleanAllFiles();
                            return ok(Json.toJson(new ServerResponse("ok", "Successfully deleted")));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogAggregator.logServerError("Error while deleting all samples", account);
                            return ok(Json.toJson(new ServerResponse("error", "Error while deleting files")));
                        }
                    default:
                        return badRequest(Json.toJson(new ServerResponse("error", "Invalid action")));
                }
            }
        });
    }

    public static F.Promise<Result> files() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();
                return ok(Json.toJson(Account.getFilesInformation(account)));
            }
        });
    }

    public static F.Promise<Result> accountInformation() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                return ok(Json.toJson(new CacheServerResponse("ok", Account.getAccountInformation(getCurrentAccount()))));
            }
        });
    }

    public static F.Promise<Result> data() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();

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
                    LogAggregator.logServerError("Error while requesting " + type + " data", account);
                    return badRequest(Json.toJson(new ServerResponse("error", "Unknown type " + type)));
                }
            }
        });
    }

    public static class ShareFilesRequest {
        public String[] selectedFiles;
        public String description;
    }

    public static F.Promise<Result> shareFiles() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                if (!Configuration.isSharingEnabled()) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Sharing feature is disabled")));
                }
                Account account = getCurrentAccount();
                JsonNode request = request().body().asJson();
                ShareFilesRequest shareFilesRequest;
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    shareFilesRequest = objectMapper.convertValue(request, ShareFilesRequest.class);
                } catch (Exception e) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                }
                if (account.isMaxSharedGroupsCountExceeded())
                    return badRequest(Json.toJson(new ServerResponse("error", "You have exceeded the allowed number of shared samples")));

                List<UserFile> files = new ArrayList<>();
                for (String selectedFile : shareFilesRequest.selectedFiles) {
                    UserFile userFile = UserFile.fyndByNameAndAccount(account, selectedFile);
                    if (userFile == null) {
                        LogAggregator.logServerError("[Sharing]User have no sample named " + selectedFile, account);
                        return badRequest(Json.toJson(new ServerResponse("error", "You have no sample named " + selectedFile)));
                    }
                    files.add(userFile);
                }


                String groupUniqueName = CommonUtil.RandomStringGenerator.generateRandomString(10, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC);
                String cachePath = account.getDirectoryPath() + "/" + groupUniqueName + "/";
                File groupFolder = new File(cachePath);
                if (!groupFolder.exists()) {
                    if (!groupFolder.mkdir()) {
                        LogAggregator.logServerError("Error while creating directory for sharing group", account);
                        return badRequest(Json.toJson(new ServerResponse("error", "Server is unavailable")));
                    }
                }
                String link = CommonUtil.RandomStringGenerator.generateRandomString(40, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                while (SharedGroup.findByLink(link) != null) {
                    link = CommonUtil.RandomStringGenerator.generateRandomString(40, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                }
                List<SharedFile> sharedFiles = new ArrayList<>();
                SharedGroup sharedGroup = new SharedGroup(account, groupUniqueName, cachePath, link, sharedFiles, shareFilesRequest.description);
                sharedGroup.save();
                for (UserFile file : files) {
                    String sharedFileUniqueName = CommonUtil.RandomStringGenerator.generateRandomString(10, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC);
                    String fileDirPath = sharedGroup.getCachePath() + "/" + sharedFileUniqueName + "/";
                    File fileDir = new File(fileDirPath);
                    if (!fileDir.exists()) {
                        if (!fileDir.mkdir()) {
                            sharedGroup.deleteGroup();
                            LogAggregator.logServerError("Error while creating directory for file " + file.getFileName() + " in sharing group", account);
                            return badRequest(Json.toJson(new ServerResponse("error", "Server is currently unavailable")));
                        }
                    }
                    FileUtils.copyDirectory(new File(file.getDirectoryPath()), new File(fileDirPath));
                    SharedFile sharedFile = new SharedFile(file, sharedGroup, sharedFileUniqueName, fileDirPath + file.getFileName() + "." + file.getFileExtension(), fileDirPath);
                    sharedFile.save();
                    sharedGroup.addFile(sharedFile);
                }
                sharedGroup.update();
                return ok(Json.toJson(sharedGroup.getGroupInformation()));
            }
        });
    }

    public static F.Promise<Result> deleteShared() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();
                JsonNode request = request().body().asJson();
                if (!request.has("link"))
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                String link = request.get("link").asText();
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null || !Objects.equals(sharedGroup.getAccount(), account))
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));

                sharedGroup.deleteGroup();
                return ok(Json.toJson(new ServerResponse("ok", "Successfully deleted")));
            }
        });
    }

    public static F.Promise<Result> annotationData() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();

                JsonNode request = request().body().asJson();
                if (!request.findValue("action").asText().equals("data")
                        || request.findValue("fileName") == null
                        || request.findValue("shift") == null) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid Action")));
                }

                String fileName = request.findValue("fileName").asText();
                UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

                if (file != null) {
                    AnnotationTable annotationTable = new AnnotationTable(file, request.findValue("shift").asInt());
                    annotationTable.create();
                    return ok(Json.toJson(annotationTable.getData()));
                } else {
                    return badRequest(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                }
            }
        });
    }

    public static class SearchClonotypesRequest {
        public String sequence;
        public boolean aminoAcid;
        public boolean nonFunctional;
        public String[] vFilter;
        public String[] jFilter;
        public String[] dFilter;
        //Length filter parameters
        public int length;
        public String lengthType;
        //For one sample
        public String fileName;
        //For multiple samples
        public String[] selectedFiles;
    }

    public static F.Promise<Result> searchClonotypes() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {

                Account account = getCurrentAccount();
                JsonNode request = request().body().asJson();

                SearchClonotypesRequest searchClonotypesRequest;

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    searchClonotypesRequest = objectMapper.convertValue(request, SearchClonotypesRequest.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                }

                String fileName = searchClonotypesRequest.fileName;
                String sequence = searchClonotypesRequest.sequence;
                boolean aminoAcid = searchClonotypesRequest.aminoAcid;

                if (aminoAcid && !sequence.toUpperCase().matches("[FLSYCWPHQRIMTNKVADEGX\\*\\_\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                } else {
                    int leftCount = 0;
                    int rightCount = 0;
                    for (char c : sequence.toCharArray()) {
                        if (c == '[') {
                            leftCount++;
                            continue;
                        }
                        if (c == ']') {
                            rightCount++;
                            if (rightCount > leftCount)
                                return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                    }
                }

                if (!aminoAcid && !sequence.toUpperCase().matches("[ATGCN\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid nucleotide sequence")));
                } else {
                    int leftCount = 0;
                    int rightCount = 0;
                    for (char c : sequence.toCharArray()) {
                        if (c == '[') {
                            leftCount++;
                            continue;
                        }
                        if (c == ']') {
                            rightCount++;
                            if (rightCount > leftCount)
                                return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid nucleotide sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid nucleotide sequence")));
                    }
                }

                UserFile userFile = UserFile.fyndByNameAndAccount(account, fileName);
                if (userFile == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                try {
                    SearchClonotypes.SingleSampleSearchResults singleSampleSearchResults = SearchClonotypes.searchSingleSample(userFile, searchClonotypesRequest);
                    return ok(Json.toJson(singleSampleSearchResults));
                } catch (Exception e) {
                    return badRequest(Json.toJson(new ServerResponse("error", e.getMessage())));
                }
            }
        });
    }

    public static F.Promise<Result> multipleSearchClonotypes() {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                Account account = getCurrentAccount();
                JsonNode request = request().body().asJson();

                SearchClonotypesRequest searchClonotypesRequest;
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    searchClonotypesRequest = objectMapper.convertValue(request, SearchClonotypesRequest.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                }
                String[] selectedFiles = searchClonotypesRequest.selectedFiles;
                String sequence = searchClonotypesRequest.sequence;
                boolean aminoAcid = searchClonotypesRequest.aminoAcid;

                if (aminoAcid && !sequence.toUpperCase().matches("[FLSYCWPHQRIMTNKVADEGX\\*\\_\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                } else {
                    int leftCount = 0;
                    int rightCount = 0;
                    for (char c : sequence.toCharArray()) {
                        if (c == '[') {
                            leftCount++;
                            continue;
                        }
                        if (c == ']') {
                            rightCount++;
                            if (rightCount > leftCount)
                                return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                    }
                }

                if (!aminoAcid && !sequence.toUpperCase().matches("[ATGCN\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid amino acid sequence")));
                } else {
                    int leftCount = 0;
                    int rightCount = 0;
                    for (char c : sequence.toCharArray()) {
                        if (c == '[') {
                            leftCount++;
                            continue;
                        }
                        if (c == ']') {
                            rightCount++;
                            if (rightCount > leftCount)
                                return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid nucleotide sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "The pattern is not a valid nucleotide sequence")));
                    }
                }

                List<UserFile> files = new ArrayList<>();
                for (String fileName : selectedFiles) {
                    UserFile userFile = UserFile.fyndByNameAndAccount(account, fileName);
                    if (userFile == null)
                        return badRequest(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                    files.add(userFile);
                }

                try {
                    List<SearchClonotypes.SingleSampleSearchResults> multipleSampleSearchResults = SearchClonotypes.searchMultipleSample(files, searchClonotypesRequest);
                    return ok(Json.toJson(multipleSampleSearchResults));
                } catch (Exception e) {
                    return badRequest(Json.toJson(new ServerResponse("error", e.getMessage())));
                }
            }
        });
    }

    private static Result cache(UserFile file, String cacheName, Account account) {
        //Return cache file transformed into json format
        if (file == null) {
            LogAggregator.logServerError("User have no requested file", account);
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
                LogAggregator.logServerError("Cache file does not exists [" + file.getFileName() + "," + cacheName + "]", account);
                return ok(Json.toJson(new CacheServerResponse("error", "Cache file does not exist", null)));
            }
        } else {
            LogAggregator.logServerError("File: " + file.getFileName() + " did not rendered", account);
            return ok(Json.toJson(new CacheServerResponse("error", "The file has not been rendered yet")));
        }
    }

    private static Result basicStats(Account account) throws FileNotFoundException {
        //Return basicStats cache for all files in json format

        if (account.getFilesCount() == 0) {
            return badRequest(Json.toJson(new ServerResponse("error", "You have no files :(")));
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

        if (account.getFilesCount() == 0) {
            return badRequest(Json.toJson(new ServerResponse("error", "You have no files :(")));
        }

        RarefactionChart rarefactionChart = new RarefactionChart(account);
        return ok(Json.toJson(new CacheServerResponse("success", rarefactionChart.create(needToCreateNew))));
    }

    public static class TagRequest {
        public String description;
        public String color;
        public String tagName;
        public long id;

        public TagRequest() {}
    }

    public static Result createTag() {
        Account account = getCurrentAccount();
        JsonNode request = request().body().asJson();

        TagRequest tagRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            tagRequest = objectMapper.convertValue(request, TagRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        }
        Tag tag = new Tag(account, tagRequest.description, tagRequest.color, tagRequest.tagName);
        tag.save();
        return ok(Json.toJson(new Tag.TagInformation(tag)));
    }

    public static Result editTag() {
        Account account = getCurrentAccount();
        JsonNode request = request().body().asJson();

        TagRequest tagRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            tagRequest = objectMapper.convertValue(request, TagRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        }
        Tag tag = Tag.findById(tagRequest.id, account);
        if (tag == null) return badRequest(Json.toJson(new ServerResponse("error", "Invalid tag")));
        tag.updateTag(tagRequest);
        return ok(Json.toJson(new Tag.TagInformation(tag)));
    }

    public static Result deleteTag() {
        Account account = getCurrentAccount();
        JsonNode request = request().body().asJson();
        if (!request.has("id"))
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        Long id = request.get("id").asLong();
        Tag byId = Tag.findById(id, account);
        if (byId == null)
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        byId.deleteTag();
        return ok(Json.toJson(new ServerResponse("success", "Successfully deleted")));
    }

    public static class TagFilesRequest {
        public Long id;
        public String[] selectedFiles;
    }

    public static Result tagFiles() {
        Account account = getCurrentAccount();
        JsonNode request = request().body().asJson();

        TagFilesRequest tagFilesRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            tagFilesRequest = objectMapper.convertValue(request, TagFilesRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        }
        if (tagFilesRequest.selectedFiles == null || tagFilesRequest.id == null)
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        Tag tag = Tag.findById(tagFilesRequest.id, account);
        if (tag == null)
            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
        tag.tagFiles(tagFilesRequest.selectedFiles);
        return ok(Json.toJson(tagFilesRequest));
    }

    public static WebSocket<JsonNode> render() {
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
                                out.write(Json.toJson(new WSResponse("ok", "render", fileName, "start")));
                                try {

                                    SampleFileConnection sampleFileConnection;
                                    Sample sample;

                                    try {
                                        sampleFileConnection  = new SampleFileConnection(file.getPath(), file.getSoftwareType(), MetadataUtil.createSampleMetadata(MetadataUtil.fileName2id(file.getFileName())), true, false);
                                        sample = sampleFileConnection.getSample();
                                        file.setSampleCount(sample.getDiversity(), sample.getCount());

                                        if (account.getMaxClonotypesCount() > 0) {
                                            if (file.getClonotypesCount() > account.getMaxClonotypesCount()) {
                                                UserFile.deleteFile(file);
                                                out.write(Json.toJson(new WSResponse("error", "render", fileName, "Number of clonotypes in a sample should be less than " + account.getMaxClonotypesCount())));
                                                out.close();
                                                return;
                                            }
                                        }

                                    } catch (Exception e) {
                                        UserFile.deleteFile(file);
                                        out.write(Json.toJson(new WSResponse("error", "render", fileName, "Wrong file format, unable to parse")));
                                        out.close();
                                        return;
                                    }

                                    //Trying to render cache files for sample
                                    ComputationUtil computationUtil = new ComputationUtil(file, sample, out);
                                    computationUtil.createSampleCache();
                                    return;
                                } catch (Exception e) {
                                    //On exception delete file and inform user about fail
                                    LogAggregator.logServerError("Error while rendering file " + file.getFileName(), account);
                                    out.write(Json.toJson(new WSResponse("error", "render", fileName, "Error while rendering")));
                                    UserFile.deleteFile(file);
                                    e.printStackTrace();
                                    out.close();
                                    return;
                                }
                            default:
                                LogAggregator.logServerError("Render: unknown type " + event.findValue("action").asText(), account);
                                out.write(Json.toJson(new WSResponse("error", "render", "", "Unknown action")));
                                out.close();
                        }
                    }
                });
            }
        };
    }

    public static WebSocket<JsonNode> wsTest() {
        LocalUser localUser = LocalUser.find.byId(SecureSocial.currentUser().identityId().userId());
        final Account account = localUser.account;
        return new WebSocket<JsonNode>() {
            @Override
            public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
                out.close();
            }
        };
    }
}
