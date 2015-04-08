package graph.SpectratypeVChart;

import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.basic.SpectratypeV;
import com.antigenomics.vdjtools.sample.Sample;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;

public class SpectratypeVChartCreator {
    private UserFile file;
    private SpectratypeVChart spectratypeVChart;
    private Integer defaultTop;
    private String cacheName;
    private SpectratypeV spectratypeV;
    private boolean created;

    public SpectratypeVChartCreator(UserFile file, Sample sample) {
        this.file = file;
        this.spectratypeVChart = new SpectratypeVChart();
        this.spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        this.defaultTop = 12;
        this.cacheName = CacheType.spectratypeV.getCacheFileName();
        this.created = false;
    }

    public SpectratypeVChart getSpectratypeVChart() {
        return spectratypeVChart;
    }

    public SpectratypeVChartCreator create() {
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(defaultTop);
        for (String key : new HashSet<>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            SpectratypeVBar spectratypeVBar = new SpectratypeVBar(key, VColor.getColor(key));
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram(false);
            for (int i = 0; i < x_coordinates.length; i++) {
                spectratypeVBar.addPoint((double) x_coordinates[i], y_coordinates[i]);
            }
            spectratypeVChart.addBar(spectratypeVBar);
        }
        created = true;
        return this;
    }

    public void saveCache() throws Exception {
        if (created) {
            try {
                File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
                fileWriter.write(Json.stringify(Json.toJson(spectratypeVChart.getChart())));
                fileWriter.close();
            } catch (FileNotFoundException fnfe) {
                Logger.of("user." + file.getAccount().getUserName()).error("User " + file.getAccount().getUserName() +
                        ": save cache error [" + file.getFileName() + "," + cacheName + "]");
                fnfe.printStackTrace();
            }
        } else {
            throw new Exception("You should create graph");
        }
    }

}
