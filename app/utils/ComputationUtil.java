package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.libs.Json;
import play.mvc.Result;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComputationUtil {

    public static void vdjUsageData(UserFile file) {
        Software software = file.software_type;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.file_path);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
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
        Integer optimization_value = 30;
        List<Table> opt_data = new ArrayList<>();
        Collections.sort(data, new Comparator<Table>() {
            public int compare(Table c1, Table c2) {
                if (c1.relationNum > c2.relationNum) return -1;
                if (c1.relationNum < c2.relationNum) return 1;
                return 0;
            }
        });
        for (int i = 0; i < optimization_value; i++) {
            opt_data.add(data.get(i));
        }
        File histogramJsonFile = new File(file.file_dir_path + "/" + "vdjUsage.json");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(opt_data);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            file.vdjUsageData = true;
            Ebean.update(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void spectrotypeHistogram(UserFile file) {
        Software software = file.software_type;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.file_path);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        Sample sample = sampleCollection.getAt(0); //sample collection get at (0)
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
        File histogramJsonFile = new File(file.file_dir_path + "/" + "histogram.json");
        try {
            PrintWriter jsonWriter = new PrintWriter(histogramJsonFile.getAbsoluteFile());
            JsonNode jsonData = Json.toJson(histogramData);
            jsonWriter.write(Json.stringify(jsonData));
            jsonWriter.close();
            file.histogramData = true;
            Ebean.update(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}