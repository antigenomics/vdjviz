package graph.QuantileStatsChart;

import com.antigenomics.vdjtools.diversity.QuantileStats;
import com.antigenomics.vdjtools.sample.Sample;
import models.Account;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

public class QuantileStatsChartCreator {
    private boolean created;
    private QuantileStatsChart quantileStatsChart;
    private UserFile file;
    private Account account;
    private String cacheName;
    private QuantileStats quantileStats;
    //TODO
    private HashMap<String, Object> data;

    public QuantileStatsChartCreator(UserFile file, Account account, Sample sample) {
        this.quantileStats = new QuantileStats(sample, 5);
        this.quantileStatsChart = new QuantileStatsChart();
        this.data = new HashMap<>();
        this.created = false;
        this.file = file;
        this.cacheName = CacheType.quantileStats.getCacheFileName();
    }

    public QuantileStatsChartCreator create() {
        quantileStatsChart.addQuantile(new Quantile("Singleton", quantileStats.getSingletonFreq()));
        quantileStatsChart.addQuantile(new Quantile("Doubleton", quantileStats.getDoubletonFreq()));
        QuantileStatsChart highOrder = new QuantileStatsChart();
        for (int i = 0; i < quantileStats.getNumberOfQuantiles(); i++) {
            highOrder.addQuantile(new Quantile("Q" + (i + 1), quantileStats.getQuantileFrequency(i)));
        }
        quantileStatsChart.addQuantile(new Quantile("HighOrder", highOrder.getChart()));

        data.put("name", "data");
        data.put("children", quantileStatsChart.getChart());
        created = true;
        return this;
    }

    public HashMap<String, Object> getData() {
        if (created)
            return data;
        return null;
    }

    public void saveCache() throws Exception {
        if (created) {
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
        } else {
            throw new Exception("You should create graph");
        }
    }


}
