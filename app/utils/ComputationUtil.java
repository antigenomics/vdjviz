package utils;

import com.antigenomics.vdjdb.core.db.CdrDatabase;
import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.db.CdrMatch;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.basic.SpectratypeV;
import com.antigenomics.vdjtools.db.*;
import com.antigenomics.vdjtools.diversity.*;
import com.antigenomics.vdjtools.intersection.IntersectionType;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.milaboratory.core.tree.TreeSearchParameters;
import graph.QuantileStatsChart.Quantile;
import graph.QuantileStatsChart.QuantileStatsChart;
import graph.QuantileStatsChart.QuantileStatsChartCreator;
import graph.RarefactionChart.RarefactionLine;
import graph.RarefactionChart.RarefactionChart;
import graph.SpectratypeChart.SpectratypeBar;
import graph.SpectratypeChart.SpectratypeChart;
import graph.SpectratypeChart.SpectratypeChartCreator;
import graph.SpectratypeVChart.SpectratypeVBar;
import graph.SpectratypeVChart.SpectratypeVChart;
import models.Account;
import play.Logger;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;
import utils.ArrayUtils.Data;
import utils.CacheType.CacheType;
import utils.RarefactionColor.RarefactionColor;
import graph.SpectratypeVChart.VColor;

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

        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sample.getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);
        List<String> labels = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();

        double[][] matrix = new double[vVector.length + jVector.length][];
        for (int i = 0; i < vVector.length + jVector.length; i++) {
            matrix[i] = new double[vVector.length + jVector.length];
        }
        for (int i = 0; i < jVector.length; i++) {
            for (int j = 0; j < vVector.length; j++) {
                matrix[i][j + jVector.length] = vjMatrix[i][j];
                matrix[j + jVector.length][i] = vjMatrix[i][j];
            }
        }

        Collections.addAll(labels, jVector);
        Collections.addAll(labels, vVector);
        HashMap<String, Object> data = new HashMap<>();
        data.put("matrix", matrix);
        data.put("labels", labels);
        saveCache("vjUsage", data);
        serverResponse.changeValue("progress", 20);
        out.write(Json.toJson(serverResponse.getData()));

    }

    private void spectratype() throws Exception {
        SpectratypeChartCreator spectratypeChartCreator = new SpectratypeChartCreator(file, account, sample);
        spectratypeChartCreator.create().saveCache();
        serverResponse.changeValue("progress", 30);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void spectratypeV() throws Exception {
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);
        SpectratypeVChart spectratypeVChart = new SpectratypeVChart();
        for (String key : new HashSet<>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            SpectratypeVBar spectratypeVBar = new SpectratypeVBar(key, new VColor(key).getHexVColor());
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram();
            for (int i = 0; i < x_coordinates.length; i++) {
                spectratypeVBar.addPoint((double) x_coordinates[i], y_coordinates[i]);
            }
            spectratypeVChart.addBar(spectratypeVBar);
        }
        saveCache(CacheType.spectratypeV.getCacheFileName(), spectratypeVChart.getChart());
        serverResponse.changeValue("progress", 40);
        out.write(Json.toJson(serverResponse.getData()));
    }


    //TODO
    private void quantileStats() throws Exception {
        QuantileStatsChartCreator quantileStatsChartCreator = new QuantileStatsChartCreator(file, account, sample);
        quantileStatsChartCreator.create().saveCache();
        serverResponse.changeValue("progress", 100);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void annotation() throws Exception {

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
            v.addData(new Object[]{cdrDatabaseMatch.getQuery().getV(), cdrDatabaseMatch.getvMatch()});
            j.addData(new Object[]{cdrDatabaseMatch.getQuery().getJ(), cdrDatabaseMatch.getjMatch()});
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
        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void basicStats() throws Exception {
        BasicStats basicStats = new BasicStats(sample);
        String[] header = BasicStats.getHEADER().split("\t");
        HashMap<String, String> basicStatsCache = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsCache.put("Name", file.getFileName());
        for (int i = 0; i < header.length; i++) {
            basicStatsCache.put(header[i], basicStatsValues[i]);
        }
        saveCache(CacheType.summary.getCacheFileName(), basicStatsCache);
        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }


    private void rarefaction() throws Exception {
        FrequencyTable frequencyTable = new FrequencyTable(sample, IntersectionType.Strict);
        Rarefaction rarefaction = new Rarefaction(frequencyTable);
        ArrayList<Rarefaction.RarefactionPoint> rarefactionPoints = rarefaction.build(sample.getCount());
        RarefactionLine line = new RarefactionLine(file.getFileName(), RarefactionColor.getColor(rarefaction.hashCode()), false,  false);
        RarefactionLine areaLine = new RarefactionLine(file.getFileName() + "_area", "#dcdcdc", true, true);
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            line.addPoint(rarefactionPoint.x, rarefactionPoint.mean);
        }
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            areaLine.addPoint(rarefactionPoint.x, rarefactionPoint.ciL);
        }
        for (int i = rarefactionPoints.size() - 1; i >=0; --i) {
            areaLine.addPoint(rarefactionPoints.get(i).x, rarefactionPoints.get(i).ciU);
        }
        RarefactionChart rarefactionChart = new RarefactionChart(frequencyTable.getCache(), line, areaLine);
        saveCache(CacheType.rarefaction.getCacheFileName(), rarefactionChart);
        serverResponse.changeValue("progress", 90);
        out.write(Json.toJson(serverResponse.getData()));
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
        file.changeRenderingState(true);
        Ebean.update(file);
        serverResponse.addData(new Object[]{"ok", "render", "start", file.getFileName()});
        out.write(Json.toJson(serverResponse.getData()));
        vjUsageData();
        spectratype();
        spectratypeV();
        annotation();
        basicStats();
        rarefaction();
        quantileStats();
        file.changeRenderingState(false);
        file.changeRenderedState(true);
        serverResponse.changeValue("progress", "end");
        out.write(Json.toJson(serverResponse.getData()));
        Ebean.update(file);
    }
}
