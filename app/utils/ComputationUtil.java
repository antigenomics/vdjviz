package utils;

import com.antigenomics.vdjdb.core.db.CdrDatabase;
import com.antigenomics.vdjtools.db.CdrMatch;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.db.*;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.milaboratory.core.tree.TreeSearchParameters;
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
    private SampleCollection sampleCollection;
    private UserFile file;
    private WebSocket.Out<JsonNode> out;
    private Data serverResponse;
    private ProgressResponse progressResponse;

    private class ProgressResponse {
        public String result;
        public String action;
        public String progress;
        public String fileName;
        private WebSocket.Out<JsonNode> out;

        public ProgressResponse(String fileName, WebSocket.Out<JsonNode> out) {
            this.fileName = fileName;
            this.out = out;
            this.result = "ok";
            this.action = "render";
            this.progress = "start";
        }

        public void sendMessage(String progress) {
            this.progress = progress;
            sendMessage();
        }

        public void sendMessage() {
            out.write(Json.toJson(this));
        }

    }

    public ComputationUtil(UserFile file, WebSocket.Out<JsonNode> out) {
        Software software = file.getSoftwareType();
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.getPath());
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        this.sample = sampleCollection.getAt(0);
        this.sampleCollection = sampleCollection;
        this.file = file;
        this.account = file.getAccount();
        this.out = out;
        this.serverResponse = new Data(new String[]{"result", "action", "progress", "fileName"});
        this.progressResponse = new ProgressResponse(file.getFileName(), out);
    }

    public Sample getSample() {
        return this.sample;
    }

    public SampleCollection getSampleCollection() {
        return this.sampleCollection;
    }

    public UserFile getFile() {
        return this.file;
    }

    public WebSocket.Out<JsonNode> getWebSocketOut() {
        return this.out;
    }

    public Data getServerResponse() {
        return this.serverResponse;
    }

    private void vjUsageData() throws Exception {
        VJUsageChartCreator vjUsageChartCreator = new VJUsageChartCreator(file, account, sampleCollection);
        vjUsageChartCreator.create().saveCache();
        progressResponse.sendMessage("20");

    }

    private void spectratype() throws Exception {
        SpectratypeChartCreator spectratypeChartCreator = new SpectratypeChartCreator(file, account, sample);
        spectratypeChartCreator.create().saveCache();
        progressResponse.sendMessage("30");
    }

    private void spectratypeV() throws Exception {
        SpectratypeVChartCreator spectratypeVChartCreator = new SpectratypeVChartCreator(file, account, sample);
        spectratypeVChartCreator.create().saveCache();
        progressResponse.sendMessage("40");
    }

    private void quantileStats() throws Exception {
        QuantileStatsChartCreator quantileStatsChartCreator = new QuantileStatsChartCreator(file, account, sample);
        quantileStatsChartCreator.create().saveCache();
        progressResponse.sendMessage("100");
    }

    private void annotation() throws Exception {
        /*
        CdrDatabase cdrDatabase = new CdrDatabase();
        DatabaseBrowser databaseBrowser = new DatabaseBrowser(false, false, new TreeSearchParameters(1, 0, 0, 1));
        BrowserResult browserResult = databaseBrowser.query(sample, cdrDatabase);
        Data data = new Data(new String[]{"data", "header"});
        List<HashMap<String, Object>> annotationData = new ArrayList<>();
        String[] header = cdrDatabase.annotationHeader;
        for (CdrMatch cdrDatabaseMatch : browserResult) {
            Data annotationDataNode = new Data(new String[]{"query_cdr3aa", "query_V", "query_J", "freq", "count"});
            Data v = new Data(new String[]{"v", "match"});
            Data j = new Data(new String[]{"j", "match"});
            Data cdr3aa = new Data(new String[]{"cdr3aa", "pos", "vend", "jstart", "dstart", "dend"});
            v.addData(new Object[]{cdrDatabaseMatch.getQuery().getV(), cdrDatabaseMatch.isvMatch()});
            j.addData(new Object[]{cdrDatabaseMatch.getQuery().getJ(), cdrDatabaseMatch.isjMatch()});
            cdr3aa.addData(new Object[]{cdrDatabaseMatch.getQuery().getCdr3aa(),
                    cdrDatabaseMatch.getAlignment().getAbsoluteMutations().firsMutationPosition(),
                    cdrDatabaseMatch.getQuery().getVEnd(),
                    cdrDatabaseMatch.getQuery().getJStart(),
                    cdrDatabaseMatch.getQuery().getDStart(),
                    cdrDatabaseMatch.getQuery().getDEnd()});
            annotationDataNode.addData(new Object[]{cdr3aa.getData(), v.getData(), j.getData(), cdrDatabaseMatch.getQuery().getFreq(), cdrDatabaseMatch.getQuery().getCount()});
            List<String> annotations = cdrDatabaseMatch.getSubject().getAnnotation();
            for (int i = 0; i < header.length; i++) {
                annotationDataNode.changeValue(header[i], annotations.get(i));
            }
            annotationData.add(annotationDataNode.getData());
        }
        data.addData(new Object[]{annotationData, header});
        saveCache(CacheType.annotation.getCacheFileName(), data.getData());
        */
        AnnotationTable annotationTable = new AnnotationTable(account, file, sample);
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
        progressResponse.sendMessage("start");
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
