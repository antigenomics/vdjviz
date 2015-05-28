package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.AnnotationTable.AnnotationTable;
import graph.AnnotationTable.AnnotationTableShared;
import graph.RarefactionChartMultiple.RarefactionChart;
import graph.RarefactionChartMultiple.RarefactionChartShared;
import graph.SearchClonotypes.SearchClonotypes;
import models.Account;
import models.SharedFile;
import models.SharedGroup;

import models.UserFile;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CacheType.CacheType;
import utils.server.CacheServerResponse;
import utils.server.ServerResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ShareAPI extends Controller {

    public static Result sharedAccount(String link) {
        SharedGroup byLink = SharedGroup.findByLink(link);
        if (byLink == null)
            return ok(views.html.commonPages.notFound.render("share/" + link));
        return ok(views.html.account.accountMainPage.render(true, link));
    }

    public static F.Promise<Result> info(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                return ok(Json.toJson(new SharedGroup.GroupInformation(sharedGroup)));
            }
        });
    }

    public static F.Promise<Result> data(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));

                JsonNode request = request().body().asJson();
                if (!request.findValue("action").asText().equals("data")
                        || request.findValue("fileName") == null
                        || request.findValue("type") == null) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid Action")));
                }

                String fileName = request.findValue("fileName").asText();
                String type = request.findValue("type").asText();
                try {
                    CacheType cacheType = CacheType.findByType(type);
                    if (cacheType.getSingle()) {
                        SharedFile sharedFile = sharedGroup.findSharedFileByName(fileName);
                        if (sharedFile == null)
                            return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                        return cache(sharedFile, cacheType.getCacheFileName(), sharedGroup.getAccount());
                    } else {
                        switch (type) {
                            case "summary":
                                return basicStats(sharedGroup);
                            case "rarefaction":
                                Boolean needToCreateNew = request.findValue("new").asBoolean();
                                return rarefaction(sharedGroup, needToCreateNew);
                            default:
                                throw new IllegalArgumentException("Unknown type " + type);
                        }
                    }
                } catch (IllegalArgumentException | FileNotFoundException e) {
                    e.printStackTrace();
                    Logger.of("user." + sharedGroup.getAccount().getUserName()).error("User " + sharedGroup.getAccount().getUserName() +
                            ": error while requesting shared " + type + " data");
                    return badRequest(Json.toJson(new ServerResponse("error", "Unknown type " + type)));
                }
            }
        });
    }

    private static Result cache(SharedFile file, String cacheName, Account account) {
        //Return cache file transformed into json format
        if (file == null) {
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    " have no requested file");
            return badRequest(Json.toJson(new CacheServerResponse("error", "You have no requested file", null)));
        }
        try {
            File jsonFile = new File(file.getFileDirPath() + "/" + cacheName + ".cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(Json.toJson(new CacheServerResponse("success", jsonData)));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    ": cache file does not exists [" + file.getFileName() + "," + cacheName + "]");
            return ok(Json.toJson(new CacheServerResponse("error", "Cache file does not exist", null)));
        }
    }

    private static Result basicStats(SharedGroup sharedGroup) throws FileNotFoundException {
        //Return basicStats cache for all files in json format

        List<JsonNode> basicStatsData = new ArrayList<>();
        for (SharedFile file : sharedGroup.getFiles()) {
            File basicStatsCache = new File(file.getFileDirPath() + "/basicStats.cache");
            FileInputStream cacheFile = new FileInputStream(basicStatsCache);
            JsonNode basicStatsNode = Json.parse(cacheFile);
            basicStatsData.add(basicStatsNode);
        }
        return ok(Json.toJson(new CacheServerResponse("success", basicStatsData)));
    }

    private static Result rarefaction(SharedGroup sharedGroup, Boolean needToCreateNew) throws Exception {
        RarefactionChartShared rarefactionChartShared = new RarefactionChartShared(sharedGroup);
        return ok(Json.toJson(new CacheServerResponse("success", rarefactionChartShared.create(needToCreateNew))));
    }

    public static F.Promise<Result> annotationData(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));

                JsonNode request = request().body().asJson();
                if (!request.findValue("action").asText().equals("data")
                        || request.findValue("fileName") == null
                        || request.findValue("shift") == null) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid Action")));
                }

                String fileName = request.findValue("fileName").asText();
                SharedFile sharedFile = sharedGroup.findSharedFileByName(fileName);

                if (sharedFile != null) {
                    AnnotationTableShared annotationTable = new AnnotationTableShared(sharedFile, request.findValue("shift").asInt());
                    annotationTable.create();
                    return ok(Json.toJson(annotationTable.getData()));
                } else {
                    return badRequest(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                }
            }
        });
    }

    public static F.Promise<Result> searchClonotypes(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {

                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                JsonNode request = request().body().asJson();

                AccountAPI.SearchClonotypesRequest searchClonotypesRequest;

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    searchClonotypesRequest = objectMapper.convertValue(request, AccountAPI.SearchClonotypesRequest.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                }

                String fileName = searchClonotypesRequest.fileName;
                String sequence = searchClonotypesRequest.sequence;
                boolean aminoAcid = searchClonotypesRequest.aminoAcid;

                if (aminoAcid && !sequence.toUpperCase().matches("[FLSYCWPHQRIMTNKVADEGX\\*\\_\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
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
                                return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
                    }
                }

                if (!aminoAcid && !sequence.toUpperCase().matches("[ATGCN\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
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
                                return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
                    }
                }

                SharedFile sharedFile = sharedGroup.findSharedFileByName(fileName);
                if (sharedFile == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));

                return ok(Json.toJson(SearchClonotypes.searchSingleSample(sharedFile, searchClonotypesRequest)));
            }
        });
    }

    public static F.Promise<Result> multipleSearchClonotypes(final String link) {
        return F.Promise.promise(new F.Function0<Result>() {
            @Override
            public Result apply() throws Throwable {
                SharedGroup sharedGroup = SharedGroup.findByLink(link);
                if (sharedGroup == null)
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                JsonNode request = request().body().asJson();

                AccountAPI.SearchClonotypesRequest searchClonotypesRequest;
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    searchClonotypesRequest = objectMapper.convertValue(request, AccountAPI.SearchClonotypesRequest.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return badRequest(Json.toJson(new ServerResponse("error", "Invalid request")));
                }
                String[] selectedFiles = searchClonotypesRequest.selectedFiles;
                String sequence = searchClonotypesRequest.sequence;
                boolean aminoAcid = searchClonotypesRequest.aminoAcid;

                if (aminoAcid && !sequence.toUpperCase().matches("[FLSYCWPHQRIMTNKVADEGX\\*\\_\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
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
                                return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid aminoacid sequence")));
                    }
                }

                if (!aminoAcid && !sequence.toUpperCase().matches("[ATGCN\\[\\]]+")) {
                    return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
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
                                return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
                        }
                    }
                    if (leftCount != rightCount) {
                        return badRequest(Json.toJson(new ServerResponse("error", "Sequence does not matches for valid nucleotide sequence")));
                    }
                }

                List<SharedFile> files = new ArrayList<>();
                for (String fileName : selectedFiles) {
                    SharedFile sharedFileByName = sharedGroup.findSharedFileByName(fileName);
                    if (sharedFileByName == null)
                        return badRequest(Json.toJson(new ServerResponse("error", "You have no file named " + fileName)));
                    files.add(sharedFileByName);
                }

                return ok(Json.toJson(SearchClonotypes.searchMultipleSampleShared(files, searchClonotypesRequest)));
            }
        });
    }
}
