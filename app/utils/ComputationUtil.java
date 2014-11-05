package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.basic.SpectratypeV;
import com.antigenomics.vdjtools.db.*;
import com.antigenomics.vdjtools.diversity.DiversityEstimator;
import com.antigenomics.vdjtools.diversity.DownSampler;
import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.intersection.IntersectionType;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import play.Logger;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class ComputationUtil {

    //TODO !!!!
    public static void vjUsageData(SampleCollection sampleCollection, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {

        /**
         * SegmentUsage creating
         */

        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);

        /**
         * Table class contains information about one relationship
         */

        class Table {
            public String vSegment;
            public String jSegment;
            public Double relationNum;

            public Table(String vSegment, String jSegment, Double relationNum) {
                this.vSegment = vSegment;
                this.jSegment = jSegment;
                this.relationNum = relationNum;
            }
        }

        /**
         * Initializing Table list
         */

        List<Table> data = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();
        for (int i = 0; i < jVector.length; i++) {
            for (int j = 0; j < vVector.length; j++) {
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                data.add(new Table(vVector[j], jVector[i], vjMatrix[i][j]));
            }
        }

        /**
         * Optimization data
         * sort descending
         * and take first "optimization value" elements
         */

        Integer optimization_value = 35;
        List<Table> opt_data = new ArrayList<>();
        Collections.sort(data, new Comparator<Table>() {
            public int compare(Table c1, Table c2) {
                if (c1.relationNum > c2.relationNum) return -1;
                if (c1.relationNum < c2.relationNum) return 1;
                return 0;
            }
        });
        if (optimization_value > data.size()) {
            optimization_value = data.size();
        }
        for (int i = 0; i < optimization_value; i++) {
            opt_data.add(data.get(i));
        }

        /**
         * Creating cache files
         */

        File vdjJsonFile = new File(file.fileDirPath + "/vjUsage.cache");

        PrintWriter jsonWriter = new PrintWriter(vdjJsonFile.getAbsoluteFile());
        JsonNode jsonData = Json.toJson(opt_data);
        jsonWriter.write(Json.stringify(jsonData));
        jsonWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 20);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void spectrotype(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {

        /**
         * Getting the spectratype
         */

        Spectratype sp = new Spectratype(false, false);

        /**
         * Getting the list of clonotypes
         * Default top 10
         */

        List<Clonotype> topclones = sp.addAllFancy(sample, 10); //top 10 int

        /**
         * Initializing HistogramData list
         */

        int[] x_coordinates = sp.getLengths();
        double[] y_coordinates = sp.getHistogram();
        int x_min = x_coordinates[0];
        int x_max = x_coordinates[x_coordinates.length - 1];

        List<HashMap<String, Object>> histogramData = new ArrayList<>();
        List<HashMap<String, Object>> commonData = new ArrayList<>();
        for (int i = 0; i < x_coordinates.length; i++) {
            HashMap<String, Object> commonDataItem = new HashMap<>();
            commonDataItem.put("x", x_coordinates[i]);
            commonDataItem.put("y", y_coordinates[i]);
            commonData.add(commonDataItem);
        }
        HashMap<String, Object> commonDataNode = new HashMap<>();
        commonDataNode.put("values", commonData);
        commonDataNode.put("key", "Other");
        histogramData.add(commonDataNode);

        int count = 1;
        for (Clonotype topclone : topclones) {
            HashMap<String, Object> topCloneNode = new HashMap<>();
            List<HashMap<String, Object>> topCloneData = new ArrayList<>();
            int top_clone_x = topclone.getCdr3nt().length();
            for (int i = x_min; i <= x_max; i++) {
                HashMap<String, Object> simpleNode = new HashMap<>();
                if (i != top_clone_x) {
                    simpleNode.put("x", i);
                    simpleNode.put("y", 0);
                } else {
                    simpleNode.put("x", top_clone_x);
                    simpleNode.put("y", topclone.getFreq());
                }
                topCloneData.add(simpleNode);
            }
            topCloneNode.put("values", topCloneData);
            topCloneNode.put("key", count++);
            topCloneNode.put("name", topclone.getCdr3nt());
            topCloneNode.put("v", topclone.getV());
            topCloneNode.put("j", topclone.getJ());
            topCloneNode.put("cdr3aa", topclone.getCdr3aa());
            histogramData.add(topCloneNode);
        }

        File histogramJsonFile = new File(file.fileDirPath + "/spectrotype.cache");
        PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
        JsonNode jsonData = Json.toJson(histogramData);
        jsonWriter.write(Json.stringify(jsonData));
        jsonWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 30);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void spectrotypeV(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {
        //TODO
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);

        List<Object> histogramV = new ArrayList<>();

        for (String key : new HashSet<String>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            List<HashMap<String, Object>> valuesList = new ArrayList<>();
            HashMap<String, Object> histogramVNode = new HashMap<>();
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram();
            for (int i = 0; i < x_coordinates.length; i++) {
                HashMap<String, Object> valuesNode = new HashMap<>();
                valuesNode.put("x", x_coordinates[i]);
                valuesNode.put("y", y_coordinates[i]);
                valuesList.add(valuesNode);
            }
            histogramVNode.put("values", valuesList);
            histogramVNode.put("key", key);
            histogramV.add(histogramVNode);
        }
        File histogramJsonFile = new File(file.fileDirPath + "/spectrotypeV.cache");
        PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
        JsonNode jsonData = Json.toJson(histogramV);
        jsonWriter.write(Json.stringify(jsonData));
        jsonWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 40);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void AnnotationData(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {

        /**
         * Getting CdrDatabase


         /**
         * Initializing AnnotationData list
         * and creating cache file
         */

        CdrDatabase cdrDatabase = new CdrDatabase("trdb");
        File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
        PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
        DatabaseBrowser databaseBrowser = new DatabaseBrowser(false, false, true);

        BrowserResult browserResult = databaseBrowser.query(sample, cdrDatabase);
        List<HashMap<String, Object>> annotationData = new ArrayList<>();
        for (CdrDatabaseMatch cdrDatabaseMatch : browserResult) {
            HashMap<String, Object> annotationDataNode = new HashMap<>();
            HashMap<String, Object> v = new HashMap<>();
            HashMap<String, Object> j = new HashMap<>();
            HashMap<String, Object> cdr3aa = new HashMap<>();
            v.put("v", cdrDatabaseMatch.query.getV());
            v.put("match", cdrDatabaseMatch.vMatch);
            j.put("j", cdrDatabaseMatch.query.getJ());
            j.put("match", cdrDatabaseMatch.jMatch);
            cdr3aa.put("cdr3aa", cdrDatabaseMatch.query.getCdr3aa());
            if (cdrDatabaseMatch.getSubstitutions().size() > 0) {
                cdr3aa.put("pos", cdrDatabaseMatch.getSubstitutions().get(0).pos);
            } else {
                cdr3aa.put("pos", -1);
            }
            cdr3aa.put("vend", cdrDatabaseMatch.query.getVEnd());
            cdr3aa.put("jstart", cdrDatabaseMatch.query.getJStart());
            annotationDataNode.put("query_cdr3aa", cdr3aa);
            annotationDataNode.put("query_V", v);
            annotationDataNode.put("query_J", j);
            annotationDataNode.put("freq", cdrDatabaseMatch.query.getFreq());
            annotationDataNode.put("count", cdrDatabaseMatch.query.getCount());
            annotationDataNode.put("annotations", cdrDatabaseMatch.subject.getAnnotation());
            annotationData.add(annotationDataNode);
        }
        fileWriter.write(Json.stringify(Json.toJson(annotationData)));
        fileWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 60);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void BasicStats(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {
        BasicStats basicStats = new BasicStats(sample);
        String[] header = BasicStats.getHEADER().split("\t");
        List<HashMap<String, String>> basicStatsList = new ArrayList<>();
        HashMap<String, String> basicStatsNode = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsNode.put("Name", file.fileName);
        for (int i = 0; i < header.length; i++) {
            basicStatsNode.put(header[i], basicStatsValues[i]);
        }
        basicStatsList.add(basicStatsNode);
        File annotationCacheFile = new File(file.fileDirPath + "/basicStats.cache");
        PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(basicStatsList)));
        fileWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 80);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void diversityCache(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {
        DiversityEstimator diversityEstimator = new DiversityEstimator(sample, IntersectionType.Strict);
        DownSampler downSampler = diversityEstimator.getDownSampler();
        HashMap<String, Object> diversity = new HashMap<>();
        List<HashMap<String, Object>> values = new ArrayList<>();
        Integer step = Math.round(sample.getCount() / 100);
        double[] x = new double[101], y = new double[101];
        int j = 0;
        for (Integer i = 0; j < 100; i += step, j++) {
            HashMap<String, Object> coordinates = new HashMap<>();
            x[j] = (double) i;
            y[j] = (double) downSampler.reSample(i).getDiversity();
            coordinates.put("x", (int) x[j]);
            coordinates.put("y", (int) y[j]);
            values.add(coordinates);
        }
        x[j] = (int) sample.getCount();
        y[j] = downSampler.reSample((int) sample.getCount()).getDiversity();
        HashMap<String, Object> coorCount = new HashMap<>();
        coorCount.put("x", x[j]);
        coorCount.put("y", y[j]);
        values.add(coorCount);
        LoessInterpolator interpolator = new LoessInterpolator();
        double[] z = interpolator.smooth(x, y);
        for (int i = 0; i <= j; i++) {
            values.get(i).put("y", z[i]);
        }
        diversity.put("values", values);
        diversity.put("key", file.fileName);
        File annotationCacheFile = new File(file.fileDirPath + "/diversity.cache");
        PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(diversity)));
        fileWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 90);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void kernelDensityCache(Sample sample, UserFile file, WebSocket.Out<JsonNode> out) throws Exception {
        FrequencyTable frequencyTable = new FrequencyTable(sample, IntersectionType.Strict);
        List<HashMap<String, Object>> binValues = new ArrayList<>();
        List<HashMap<String, Object>> stdValues = new ArrayList<>();
        double y_max = 0, y_min = 1.0;
        long x_max = 0L, x_min = 1L;
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            HashMap<String, Object> binValue = new HashMap<>();
            HashMap<String, Object> stdValue = new HashMap<>();
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            binValue.put("x", binInfo.getClonotypeSize());
            binValue.put("y", binInfo.getComplementaryCdf());
            x_max = binInfo.getClonotypeSize() > x_max ? binInfo.getClonotypeSize() : x_max;
            y_max = binInfo.getComplementaryCdf() > y_max ? binInfo.getComplementaryCdf() : y_max;
            y_min = binInfo.getComplementaryCdf() < y_min ? binInfo.getComplementaryCdf() : y_min;
            if (binInfo.getClonotypeSize() - binInfo.getCloneStd() >= 1) {
                stdValue.put("x", binInfo.getClonotypeSize() - binInfo.getCloneStd());
                stdValue.put("y", binInfo.getComplementaryCdf());
                stdValues.add(0, stdValue);
            } else {
                HashMap<String, Object> stdValue1 = new HashMap<>();
                stdValue1.put("x", 1);
                stdValue1.put("y", frequencyTable.getBins().get(0).getComplementaryCdf());
                stdValues.add(0, stdValue1);
                stdValue.put("x", 1);
                stdValue.put("y", frequencyTable.getBins().get(1).getComplementaryCdf());
                stdValues.add(0, stdValue);
            }
            binValues.add(binValue);
        }
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            HashMap<String, Object> stdValue = new HashMap<>();
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            stdValue.put("x", binInfo.getClonotypeSize() + binInfo.getCloneStd());
            stdValue.put("y", binInfo.getComplementaryCdf());
            stdValues.add(stdValue);
        }
        List<HashMap<String, Object>> data = new ArrayList<>();
        HashMap<String, Object> binData = new HashMap<>();
        HashMap<String, Object> stdData = new HashMap<>();
        binData.put("values", binValues);
        binData.put("key", "bin");
        data.add(binData);
        stdData.put("values", stdValues);
        stdData.put("key", "std");
        stdData.put("area", true);
        data.add(stdData);
        HashMap<String, Object> jsonData = new HashMap<>();
        jsonData.put("data", data);
        List<Long> xAxisDomain = new ArrayList<>();
        xAxisDomain.add(x_min);
        xAxisDomain.add(x_max);
        List<Double> yAxisDomain = new ArrayList<>();
        yAxisDomain.add(y_min);
        yAxisDomain.add(y_max);
        jsonData.put("yAxisDomain", yAxisDomain);
        jsonData.put("xAxisDomain", xAxisDomain);
        File annotationCacheFile = new File(file.fileDirPath + "/kernelDensity.cache");
        PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(jsonData)));
        fileWriter.close();
        HashMap<String, Object> serverResponse = new HashMap<>();
        HashMap<String, Object> dataResponse = new HashMap<>();
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        dataResponse.put("progress", "progress");
        dataResponse.put("fileName", file.fileName);
        dataResponse.put("result", 100);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
    }

    public static void createSampleCache(UserFile file, WebSocket.Out<JsonNode> out) {

        /**
         * Getting Sample from text file
         */


        Software software = file.softwareType;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        Sample sample = sampleCollection.getAt(0);
        HashMap<String, Object> serverResponse = new HashMap<>();

        /**
         * Creating all cache files
         */

        file.rendered = false;
        file.rendering = true;
        Ebean.update(file);
        serverResponse.put("result", "ok");
        serverResponse.put("action", "render");
        HashMap<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("progress", "start");
        dataResponse.put("fileName", file.fileName);
        serverResponse.put("data", dataResponse);
        out.write(Json.toJson(serverResponse));
        try {
            vjUsageData(sampleCollection, file, out);
            spectrotype(sample, file, out);
            spectrotypeV(sample, file, out);
            AnnotationData(sample, file, out);
            BasicStats(sample, file, out);
            diversityCache(sample, file, out);
            kernelDensityCache(sample, file, out);
            file.rendering = false;
            file.rendered = true;
            serverResponse.put("result", "ok");
            serverResponse.put("action", "render");
            dataResponse.put("progress", "end");
            serverResponse.put("data", dataResponse);
            out.write(Json.toJson(serverResponse));
            Ebean.update(file);
        } catch (Exception e) {
            file.rendering = false;
            file.rendered = false;
            serverResponse.put("result", "error");
            serverResponse.put("action", "render");
            serverResponse.put("message", "Computation error");
            out.write(Json.toJson(serverResponse));
            Ebean.update(file);
            e.printStackTrace();
        }
    }
}