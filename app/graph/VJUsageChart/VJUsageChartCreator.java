package graph.VJUsageChart;

import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;
import utils.server.LogAggregator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VJUsageChartCreator {
    private UserFile file;
    private Sample sample;
    private VJUsageChart vjUsageChart;
    private String cacheName = CacheType.vjUsage.getCacheFileName();
    private boolean created;


    public VJUsageChartCreator(UserFile file, Sample sample) {
        this.file = file;
        this.sample = sample;
        this.created = false;
    }

    public VJUsageChartCreator create() {
        Sample sampleArray[] = new Sample[1];
        sampleArray[0] = sample;
        SegmentUsage segmentUsage = new SegmentUsage(sampleArray, false);
        List<String> labels = new ArrayList<>();
        String sampleId = sample.getSampleMetadata().getSampleId();
        if (segmentUsage.vjUsageMatrix(sampleId).length == 0) {
            throw new RuntimeException("Empty segment usage data");
        }
        MatrixMath matrixMath = new MatrixMath(segmentUsage.vjUsageMatrix(sampleId),
                                                segmentUsage.jUsageHeader(),
                                                segmentUsage.vUsageHeader(),
                                                segmentUsage.jUsageVector(sampleId),
                                                segmentUsage.vUsageVector(sampleId));
        matrixMath.sort().transpose();
        double[][] vjMatrix = matrixMath.getMatrix();
        String[] jVector = matrixMath.getRowsNames();
        String[] vVector = matrixMath.getColumnsNames();

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
        vjUsageChart = new VJUsageChart(matrix, labels);
        created = true;
        return this;
    }

    public void saveCache() throws Exception {
        if (created) {
            try {
                File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
                fileWriter.write(Json.stringify(Json.toJson(vjUsageChart)));
                fileWriter.close();
            } catch (FileNotFoundException fnfe) {
                LogAggregator.logServerError("Error while saving cache file[" + file.getFileName() + "," + cacheName + "]", file.getAccount());
                fnfe.printStackTrace();
            }
        } else {
            throw new Exception("You should create graph");
        }
    }

}
