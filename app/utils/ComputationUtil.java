package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
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
        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);
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

        List<Table> data = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();
        for (int i = 0; i < jVector.length; i++) {
            for (int j = 0; j < vVector.length; j++) {
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                data.add(new Table(vVector[j], jVector[i], vjMatrix[i][j]));
            }
        }
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
        File histogramJsonFile = new File(file.fileDirPath + "/vdjUsage.cache");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(opt_data);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            file.vdjUsageData = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void spectrotypeHistogram(Sample sample, UserFile file) {
        Spectratype sp = new Spectratype(false, false);
        List<Clonotype> topclones = sp.addAllFancy(sample, 10); //top 10 int
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
        ArrayList<HistogramData> histogramData = new ArrayList<>();
        int[] x_coordinates = sp.getLengths();
        double[] y_coordinates = sp.getHistogram();
        for (int i = 0; i < x_coordinates.length; i++) {
            histogramData.add(new HistogramData(x_coordinates[i], y_coordinates[i], false, ""));
        }
        for (Clonotype topclone: topclones) {
            histogramData.add(new HistogramData(topclone.getCdr3nt().length(), topclone.getFreq(), true, topclone.getCdr3nt()));
        }
        Collections.sort(histogramData, new Comparator<HistogramData>() {
            public int compare(HistogramData c1, HistogramData c2) {
                if (c1.yCoordinate > c2.yCoordinate) return 1;
                if (c1.yCoordinate < c2.yCoordinate) return -1;
                return 0;
            }
        });
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
        CdrDatabase cdrDatabase = new CdrDatabase("trdb");
        SampleAnnotation sampleAnnotation = new SampleAnnotation(sample);
        HashMap<String, Double> cdrToFrequency = sampleAnnotation.getEntryFrequencies(cdrDatabase);
        // cdrToFrequency = (CDR3 amino acid sequence) : (frequency in a given sample)

        class AnnotationData {
            public String key;
            public Double value;
            public String[] entries;
            public AnnotationData(String key, Double value ,String[] entries) {
                this.key = key;
                this.value = value;
                this.entries = entries;
            }
        }
        List<AnnotationData> data = new ArrayList<>();
        try {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            PrintWriter fileWriter = new PrintWriter(annotationCacheFile.getAbsoluteFile());
            for (Map.Entry<String, Double> entry : cdrToFrequency.entrySet()) {
                data.add(new AnnotationData(entry.getKey(),
                                            entry.getValue(),
                                            cdrDatabase.getAnnotationEntries(entry.getKey()).get(0)));
            }
            fileWriter.write(Json.stringify(Json.toJson(data)));
            fileWriter.close();
            file.annotationData = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSampleCache(UserFile file) {
        Software software = file.softwareType;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        Sample sample = sampleCollection.getAt(0); //sample collection get at (0)
        try {
            AnnotationData(sample, file);
            spectrotypeHistogram(sample, file);
            vdjUsageData(sampleCollection, file);
            Ebean.update(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}