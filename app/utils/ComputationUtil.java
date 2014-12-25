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
import models.Account;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import play.Logger;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;
import utils.ArrayUtils.Data;
import utils.ArrayUtils.xyValues;
import utils.CacheType.CacheType;
import utils.RarefactionColor.RarefactionColor;
import utils.VColor.VColor;

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

        Spectratype sp = new Spectratype(false, false);
        List<Clonotype> topclones = sp.addAllFancy(sample, 10); //top 10 int
        int[] x_coordinates = sp.getLengths();
        double[] y_coordinates = sp.getHistogram();
        int x_min = x_coordinates[0];
        int x_max = x_coordinates[x_coordinates.length - 1];

        List<HashMap<String, Object>> data = new ArrayList<>();
        xyValues commonValues = new xyValues();
        for (int i = 0; i < x_coordinates.length; i++) {
            commonValues.addValue((double) x_coordinates[i], y_coordinates[i]);
        }
        Data commonData = new Data(new String[]{"values", "key", "color"});
        commonData.addData(new Object[]{commonValues.getValues(), "Other", "#DCDCDC"});
        int count = 1;
        for (Clonotype topclone : topclones) {
            Data topCloneNode = new Data(new String[]{"values", "key", "name", "v", "j", "cdr3aa"});
            xyValues values = new xyValues();
            int top_clone_x = topclone.getCdr3nt().length();
            for (int i = x_min; i <= x_max; i++) {
                values.addValue((double) i, i != top_clone_x ? 0 : topclone.getFreq());
            }

            topCloneNode.addData(new Object[]{
                    values.getValues(),
                    count++,
                    topclone.getCdr3nt(),
                    topclone.getV(),
                    topclone.getJ(),
                    topclone.getCdr3aa(),
            });
            data.add(topCloneNode.getData());
        }
        data.add(commonData.getData());
        Collections.sort(data, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> s1, HashMap<String, Object> s2) {
                if (s1.get("key").equals("Other")) return -1;
                if (s2.get("key").equals("Other")) return 1;
                if (xyValues.getSumY(s1.get("values")) > xyValues.getSumY(s2.get("values"))) return 1;
                if (xyValues.getSumY(s1.get("values")) < xyValues.getSumY(s2.get("values"))) return -1;
                return 0;
            }
        });
        String[] colors = new String[]{"#a50026", "#d73027", "#f46d43", "#fdae61", "#fee090", "#ffffbf", "#74add1", "#abd9e9", "#e0f3f8", "#bababa", "#DCDCDC"};
        int index = 10;
        for (HashMap<String, Object> map : data) {
            map.put("color", colors[index--]);
        }
        saveCache(CacheType.spectratype.getCacheFileName(), data);
        serverResponse.changeValue("progress", 30);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void spectratypeV() throws Exception {
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);
        List<Object> data = new ArrayList<>();
        for (String key : new HashSet<>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            xyValues values = new xyValues();
            Data node = new Data(new String[]{"values", "key", "color"});
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram();
            for (int i = 0; i < x_coordinates.length; i++) {
                values.addValue((double) x_coordinates[i], y_coordinates[i]);
            }
            VColor vColor = new VColor(key);
            node.addData(new Object[]{values.getValues(), key, vColor.getHexVColor()});
            if (Objects.equals(key, "other")) {
                data.add(0, node.getData());
            } else {
                data.add(node.getData());
            }
        }
        saveCache(CacheType.spectratypeV.getCacheFileName(), data);
        serverResponse.changeValue("progress", 40);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void quantileStats() throws Exception {
        QuantileStats quantileStats = new QuantileStats(sample, 5);
        HashMap<String, Object> data = new HashMap<>();
        List<HashMap<String, Object>> mainChildrens = new ArrayList<>();
        Data singleton = new Data(new String[]{"name", "size"});
        singleton.addData(new Object[]{"Singleton", quantileStats.getSingletonFreq()});
        Data doubleton = new Data(new String[]{"name", "size"});
        doubleton.addData(new Object[]{"Doubleton", quantileStats.getDoubletonFreq()});
        mainChildrens.add(singleton.getData());
        mainChildrens.add(doubleton.getData());

        Data highOrder = new Data(new String[]{"name", "children"});
        List<HashMap<String, Object>> highOrderChildrens = new ArrayList<>();
        for (int i = 0; i < quantileStats.getNumberOfQuantiles(); i++) {
            Data highOrderChild = new Data(new String[]{"name", "size"});
            highOrderChild.addData(new Object[]{"Q" + (i + 1), quantileStats.getQuantileFrequency(i)});
            highOrderChildrens.add(highOrderChild.getData());
        }

        highOrder.addData(new Object[]{"HighOrder", highOrderChildrens});
        mainChildrens.add(highOrder.getData());

        data.put("name", "data");
        data.put("children", mainChildrens);

        saveCache(CacheType.quantileStats.getCacheFileName(), data);
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
        //max
        ArrayList<Rarefaction.RarefactionPoint> rarefactionPoints = rarefaction.build(sample.getCount());
        HashMap<String, Object> cacheData = new HashMap<>();
        cacheData.put("freqTableCache", frequencyTable.getCache());
        Data mainLine = new Data(new String[]{"values", "key", "area", "color", "hideTooltip"});
        xyValues mainLineXYValues = new xyValues();
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            mainLineXYValues.addValue(rarefactionPoint.x, rarefactionPoint.mean);
        }

        Data areaLine = new Data(new String[]{"values", "key", "area", "color", "hideTooltip"});
        xyValues areaLineXYValues = new xyValues();
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            areaLineXYValues.addValue(rarefactionPoint.x, rarefactionPoint.ciL);
        }
        for (int i = rarefactionPoints.size() - 1; i >=0; --i) {
            areaLineXYValues.addValue(rarefactionPoints.get(i).x, rarefactionPoints.get(i).ciU);
        }
        areaLine.addData(new Object[]{areaLineXYValues.getValues(), file.getFileName() + "_area", true, "#dcdcdc", true});
        mainLine.addData(new Object[]{mainLineXYValues.getValues(), file.getFileName(), false, RarefactionColor.getColor(rarefaction.hashCode()), false});
        cacheData.put("line", mainLine.getData());
        cacheData.put("areaLine", areaLine.getData());
        saveCache(CacheType.rarefaction.getCacheFileName(), cacheData);
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
