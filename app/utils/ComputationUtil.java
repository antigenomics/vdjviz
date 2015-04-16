package utils;

import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import graph.AnnotationTable.AnnotationTable;
import graph.QuantileStatsChart.QuantileStatsChartCreator;
import graph.SpectratypeChart.SpectratypeChartCreator;
import graph.SpectratypeVChart.SpectratypeVChartCreator;
import graph.VJUsageChart.VJUsageChartCreator;
import models.Account;
import play.Logger;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;
import utils.ArrayUtils.Data;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class ComputationUtil {

    private Sample sample;
    private Account account;
    private UserFile file;
    private WebSocket.Out<JsonNode> out;
    private ProgressResponse progressResponse;

    private class ProgressResponse {
        public String result;
        public String action;
        public String message;
        public String fileName;
        private WebSocket.Out<JsonNode> out;

        public ProgressResponse(String fileName, WebSocket.Out<JsonNode> out) {
            this.fileName = fileName;
            this.out = out;
            this.result = "ok";
            this.action = "render";
            this.message = "start";
        }

        public void sendMessage(String message) {
            this.message = message;
            sendMessage();
        }

        public void sendMessage() {
            out.write(Json.toJson(this));
        }

    }

    public ComputationUtil(UserFile file, Sample sample, WebSocket.Out<JsonNode> out) {
        this.progressResponse = new ProgressResponse(file.getFileName(), out);
        this.sample = sample;
        this.file = file;
        this.account = file.getAccount();
        this.out = out;
    }

    public Sample getSample() {
        return this.sample;
    }

    public UserFile getFile() {
        return this.file;
    }

    public WebSocket.Out<JsonNode> getWebSocketOut() {
        return this.out;
    }

    private void vjUsageData() throws Exception {
        VJUsageChartCreator vjUsageChartCreator = new VJUsageChartCreator(file, sample);
        vjUsageChartCreator.create().saveCache();
        progressResponse.sendMessage("20");

    }

    private void spectratype() throws Exception {
        SpectratypeChartCreator spectratypeChartCreator = new SpectratypeChartCreator(file, sample);
        spectratypeChartCreator.create().saveCache();
        progressResponse.sendMessage("30");
    }

    private void spectratypeV() throws Exception {
        SpectratypeVChartCreator spectratypeVChartCreator = new SpectratypeVChartCreator(file, sample);
        spectratypeVChartCreator.create().saveCache();
        progressResponse.sendMessage("40");
    }

    private void quantileStats() throws Exception {
        QuantileStatsChartCreator quantileStatsChartCreator = new QuantileStatsChartCreator(file, sample);
        quantileStatsChartCreator.create().saveCache();
        progressResponse.sendMessage("100");
    }

    private void annotation() throws Exception {
        AnnotationTable annotationTable = new AnnotationTable(file, sample);
        annotationTable.create().saveCache();
        progressResponse.sendMessage("60");
    }

    private void basicStats() throws Exception {
        BasicStats basicStats = new BasicStats(sample);
        String[] header = BasicStats.HEADER.split("\t");
        HashMap<String, String> basicStatsCache = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsCache.put("Name", file.getFileName());
        for (int i = 0; i < header.length; i++) {
            basicStatsCache.put(header[i], basicStatsValues[i]);
        }
        saveCache(CacheType.summary.getCacheFileName(), basicStatsCache);
        progressResponse.sendMessage("80");
    }

    private void saveCache(String cacheName, Object data) {
        try {
            File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
            PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
            fileWriter.write(Json.stringify(Json.toJson(data)));
            fileWriter.close();
        } catch (FileNotFoundException fnfe) {
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    ": save cache error [" + file.getFileName() + "," + cacheName + "]");
            fnfe.printStackTrace();
        }
    }

    public void createSampleCache() throws Exception {
        vjUsageData();
        spectratype();
        spectratypeV();
        annotation();
        basicStats();
        quantileStats();
        file.rendered();
        Ebean.update(file);
        progressResponse.sendMessage("end");
    }
}
