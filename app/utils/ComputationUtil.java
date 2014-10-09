package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.db.CdrDatabase;
import com.antigenomics.vdjtools.db.SampleAnnotation;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class ComputationUtil {

    public static void vdjUsageData(SampleCollection sampleCollection, UserFile file) {

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
            file.vdjUsageData = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void spectrotypeHistogram(Sample sample, UserFile file) {

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
         * HistogramData class contains the coordinates
         * of histogram's columns
         */

        class HistogramData {
            public Integer xCoordinate;
            public Double yCoordinate;
            public Boolean clonotype;
            public String clonotypeName;
            public HistogramData(Integer xCoordinate, Double yCoordinate, Boolean clonotype, String clonotypeName) {
                this.xCoordinate = xCoordinate;
                this.yCoordinate = yCoordinate;
                this.clonotype = clonotype;
                this.clonotypeName = clonotypeName;
            }
        }

        /**
         * Initializing HistogramData list
         */

        ArrayList<HistogramData> histogramData = new ArrayList<>();
        int[] x_coordinates = sp.getLengths();
        double[] y_coordinates = sp.getHistogram();
        for (int i = 0; i < x_coordinates.length; i++) {
            histogramData.add(new HistogramData(x_coordinates[i], y_coordinates[i], false, ""));
        }
        for (Clonotype topclone: topclones) {
            histogramData.add(new HistogramData(topclone.getCdr3nt().length(), topclone.getFreq(), true, topclone.getCdr3nt()));
        }

        /**
         * Sort ascending
         * The greatest clonotypes will be higher
         */

        Collections.sort(histogramData, new Comparator<HistogramData>() {
            public int compare(HistogramData c1, HistogramData c2) {
                if (c1.yCoordinate > c2.yCoordinate) return 1;
                if (c1.yCoordinate < c2.yCoordinate) return -1;
                return 0;
            }
        });

        /**
         * Creating cache files
         */

        File histogramJsonFile = new File(file.fileDirPath + "/histogram.cache");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(histogramData);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            file.histogramData = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void AnnotationData(Sample sample, UserFile file) {

        /**
         * Getting CdrDatabase
         */

        CdrDatabase cdrDatabase = new CdrDatabase("trdb");
        SampleAnnotation sampleAnnotation = new SampleAnnotation(sample);
        HashMap<String, Double> cdrToFrequency = sampleAnnotation.getEntryFrequencies(cdrDatabase);

        /**
         * Initializing AnnotationData list
         * and creating cache file
         */

        String[] header = cdrDatabase.header;
        HashMap<String, String> headerNode = new HashMap<>();
        headerNode.put("1", "Frequency");
        headerNode.put("2", "Name");
        for (int i = 1; i < header.length; i++) {
            headerNode.put(String.valueOf(i + 2), header[i]);
        }

        List<HashMap<String, String>> data = new ArrayList<>();
        data.add(headerNode);
        try {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
            for (Map.Entry<String, Double> entry : cdrToFrequency.entrySet()) {
                if (entry.getValue() == 0) {
                    continue;
                }

                HashMap<String, String> dataNode = new HashMap<>();
                dataNode.put("Frequency", entry.getValue().toString());
                dataNode.put("Name", entry.getKey());
                for (int i = 0; i < header.length - 1; i++) {
                    dataNode.put(header[i + 1], cdrDatabase.getAnnotationEntries(entry.getKey()).get(0)[i]);
                }
                data.add(dataNode);
            }
            fileWriter.write(Json.stringify(Json.toJson(data)));
            fileWriter.close();
            file.annotationData = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BasicStats(Sample sample, UserFile file) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createSampleCache(UserFile file) {

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

        try {
            AnnotationData(sample, file);
            spectrotypeHistogram(sample, file);
            vdjUsageData(sampleCollection, file);
            BasicStats(sample, file);
            Ebean.update(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}