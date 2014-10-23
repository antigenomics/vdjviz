package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.basic.SpectratypeV;
import com.antigenomics.vdjtools.db.CdrDatabase;
import com.antigenomics.vdjtools.db.SampleAnnotation;
import com.antigenomics.vdjtools.diversity.Diversity;
import com.antigenomics.vdjtools.diversity.DiversityEstimator;
import com.antigenomics.vdjtools.diversity.DownSampler;
import com.antigenomics.vdjtools.intersection.IntersectionType;
import com.antigenomics.vdjtools.intersection.IntersectionUtil;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

public class ComputationUtil {

    public static Boolean vdjUsageData(SampleCollection sampleCollection, UserFile file, WebSocket.Out out) {

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

        File vdjJsonFile = new File(file.fileDirPath + "/vdjUsage.cache");
        try {
            PrintWriter jsonWriter = new PrintWriter(vdjJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(opt_data);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            out.write("20");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean spectrotypeHistogram(Sample sample, UserFile file, WebSocket.Out out) {

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
        commonDataNode.put("key", "Common");
        histogramData.add(commonDataNode);

        int count = 1;
        for (Clonotype topclone: topclones) {
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

        File histogramJsonFile = new File(file.fileDirPath + "/histogram.cache");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(histogramData);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            out.write("30");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean spectrotypeVHistogram(Sample sample, UserFile file, WebSocket.Out out) {
        //TODO
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);

        List<Object> histogramV = new ArrayList<>();

        for (String key: new HashSet<String>(collapsedSpectratypes.keySet())) {
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
        File histogramJsonFile = new File(file.fileDirPath + "/histogramV.cache");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(histogramV);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            out.write("40");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean AnnotationData(Sample sample, UserFile file, WebSocket.Out out) {

        /**
         * Getting CdrDatabase
         */

        /*
        CdrDatabase cdrDatabase = new CdrDatabase("trdb");
        SampleAnnotation sampleAnnotation = new SampleAnnotation(sample, false);
        HashMap<String, Double> cdrToFrequency = sampleAnnotation.getEntryFrequencies(cdrDatabase);

        /**
         * Initializing AnnotationData list
         * and creating cache file
         */
        /*
        String[] header = cdrDatabase.header;
        HashMap<String, String> header1 = new HashMap<>();
        HashMap<String, String> header2 = new HashMap<>();
        List<HashMap<String , String>> headerJsonData = new ArrayList<>();
        header1.put("data", "Frequency");
        header2.put("data", "Name");
        headerJsonData.add(header1);
        headerJsonData.add(header2);
        for (int i = 1; i < header.length; i++) {
            HashMap<String, String> header_i = new HashMap<>();
            header_i.put("data", header[i]);
            headerJsonData.add(header_i);
        }
        HashMap<String, List<HashMap<String, String>>> data = new HashMap<>();
        data.put("header", headerJsonData);


        List<HashMap<String, String>> annotationData = new ArrayList<>();
        try {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
            for (Map.Entry<String, Double> entry : cdrToFrequency.entrySet()) {
                if (entry.getValue() == 0) {
                    continue;
                }
                HashMap<String, String> dataNode = new HashMap<>();
                //dataNode.put("Frequency", entry.getValue().toString());
                dataNode.put("Frequency", String.format(Locale.US, "%.2e", entry.getValue()));
                dataNode.put("Name", entry.getKey());
                for (int i = 0; i < header.length - 1; i++) {
                    dataNode.put(header[i + 1], cdrDatabase.getAnnotationEntries(entry.getKey()).get(0)[i]);
                }
                annotationData.add(dataNode);
            }
            data.put("data", annotationData);
            fileWriter.write(Json.stringify(Json.toJson(data)));
            fileWriter.close();
            out.write("60");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        */
        return true;
    }

    public static Boolean BasicStats(Sample sample, UserFile file, WebSocket.Out out) {
        BasicStats basicStats = new BasicStats(sample);
        String[] header =  BasicStats.getHEADER().split("\t");
        List<HashMap<String, String>> basicStatsList = new ArrayList<>();
        HashMap<String, String> basicStatsNode = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsNode.put("Name", file.fileName);
        for (int i = 0; i < header.length; i++) {
            basicStatsNode.put(header[i], basicStatsValues[i]);
        }

        basicStatsList.add(basicStatsNode);
        try {
            File annotationCacheFile = new File(file.fileDirPath + "/basicStats.cache");
            PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
            fileWriter.write(Json.stringify(Json.toJson(basicStatsList)));
            fileWriter.close();
            out.write("80");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static Boolean diversityCache(Sample sample, UserFile file, WebSocket.Out out) {
        DiversityEstimator diversityEstimator = new DiversityEstimator(sample, new IntersectionUtil(IntersectionType.AminoAcid));
        DownSampler downSampler = diversityEstimator.getDownSampler();
        HashMap<String, Object> diversity = new HashMap<>();
        List<HashMap<String, Integer>> values = new ArrayList<>();

        Integer step = Math.round(sample.getCount() / 100);
        Double stepProgress = 0.2;
        Double progress = 79.8;
        for (Integer i = 0; i < sample.getCount(); i += step) {
            HashMap<String, Integer> coordinates = new HashMap<>();
            coordinates.put("x", i);
            //TODO
            coordinates.put("y", (int) downSampler.reSample(i).getDiversity());
            values.add(coordinates);
            progress += stepProgress;
            out.write(String.format(Locale.US, "%.2f", progress));
        }
        HashMap<String, Integer> coorCount = new HashMap<>();
        coorCount.put("x", (int) sample.getCount());
        //TODO
        coorCount.put("y", (int) downSampler.reSample((int) sample.getCount()).getDiversity());
        values.add(coorCount);
        diversity.put("values", values);
        diversity.put("key", file.fileName);
        try {
            File annotationCacheFile = new File(file.fileDirPath + "/diversity.cache");
            PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
            fileWriter.write(Json.stringify(Json.toJson(diversity)));
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createSampleCache(UserFile file, play.mvc.WebSocket.Out out) {

        /**
         * Getting Sample from text file
         */


        Software software = file.softwareType;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        Sample sample = sampleCollection.getAt(0);

        /**
         * Creating all cache files
         */

        file.rendered = false;
        file.rendering = true;
        Ebean.update(file);
        out.write("start");
        try {
            if (vdjUsageData(sampleCollection, file, out)
                && AnnotationData(sample, file, out)
                && spectrotypeHistogram(sample, file, out)
                && spectrotypeVHistogram(sample, file, out)
                && BasicStats(sample, file, out)
                && diversityCache(sample, file, out)) {
                    file.rendering = false;
                    file.rendered = true;
                    out.write("ComputationDone");
            } else {
                file.rendered = false;
                file.rendering = false;
                out.write("ComputationError");
            }
            Ebean.update(file);
        } catch (Exception e) {
            file.rendering = false;
            file.rendered = false;
            out.write("ComputationError");
            Ebean.update(file);
            e.printStackTrace();
        }
    }
}