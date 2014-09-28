package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.libs.Json;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ComputationUtil {

    public static void spectrotypeHistogram(Account account, UserFile file) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}