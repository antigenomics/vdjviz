package graph.QuantileStatsChart;

import com.antigenomics.vdjtools.diversity.QuantileStats;
import com.antigenomics.vdjtools.sample.Clonotype;
import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;
import utils.server.LogAggregator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Objects;

public class QuantileStatsChartCreator {
    private boolean created;
    private QuantileStatsChart quantileStatsChart;
    private Sample sample;
    private UserFile file;
    private String cacheName;
    private QuantileStats quantileStats;
    //TODO
    private HashMap<String, Object> data;

    public QuantileStatsChartCreator(UserFile file, Sample sample) {
        this.quantileStats = new QuantileStats(sample, 5);
        this.quantileStatsChart = new QuantileStatsChart();
        this.data = new HashMap<>();
        this.sample = sample;
        this.created = false;
        this.file = file;
        this.cacheName = CacheType.quantileStats.getCacheFileName();
    }

    public QuantileStatsChartCreator create() {
        quantileStatsChart.addQuantile(new Quantile("Singleton", quantileStats.getSingletonFreq()));
        quantileStatsChart.addQuantile(new Quantile("Doubleton", quantileStats.getDoubletonFreq()));
        QuantileStatsChart highOrder = new QuantileStatsChart();

        Integer topCount = 0;
        Integer topMax = 10;
        Double sumFreq = 0.0;

        for (int i = 0; i < quantileStats.getNumberOfQuantiles(); i++) {
            Double frequency = quantileStats.getQuantileFrequency(i);
            Quantile quantile = new Quantile("Q" + (i + 1), frequency);
            if (i == 0) {
                while (topCount < topMax) {
                    Clonotype clonotype = sample.getAt(topCount);
                    if (sumFreq + clonotype.getFreq() > frequency) {
                        sumFreq = 0.0;
                        break;
                    }
                    sumFreq += clonotype.getFreq();
                    quantile.addChildren(clonotype.getCdr3aa(), clonotype.getFreq(), true);
                    topCount++;
                    if (Objects.equals(topCount, topMax)) {
                        quantile.addChildren("Other", frequency - sumFreq);
                    }
                }
                quantile.inverse();
            }
            highOrder.addQuantile(quantile);
        }
        highOrder.inverse();
        quantileStatsChart.addQuantile(new Quantile("HighOrder", highOrder.getChart()));

        data.put("name", "data");
        data.put("children", quantileStatsChart.getChart());
        created = true;
        return this;
    }

    public HashMap<String, Object> getData() throws Exception {
        if (created)
            return data;
        throw new Exception("You should create the graph");
    }

    public void saveCache() throws Exception {
        if (created) {
            try {
                File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
                fileWriter.write(Json.stringify(Json.toJson(data)));
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
