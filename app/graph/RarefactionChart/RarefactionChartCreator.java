package graph.RarefactionChart;

import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.diversity.Rarefaction;
import com.antigenomics.vdjtools.intersection.IntersectionType;
import com.antigenomics.vdjtools.sample.Sample;
import models.Account;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RarefactionChartCreator {
    private UserFile file;
    private Account account;
    private Sample sample;
    private RarefactionChart rarefactionChart;
    private FrequencyTable frequencyTable;
    private Rarefaction rarefaction;
    private boolean created;
    private String cacheName;

    public RarefactionChartCreator(UserFile file, Account account, Sample sample) {
        this.file = file;
        this.account = account;
        this.sample = sample;
        this.created = false;
        this.frequencyTable = new FrequencyTable(sample, IntersectionType.Strict);
        this.rarefaction = new Rarefaction(frequencyTable);
        this.cacheName = CacheType.rarefaction.getCacheFileName();
    }

    public RarefactionChartCreator create() {

        ArrayList<Rarefaction.RarefactionPoint> rarefactionPoints = rarefaction.build(sample.getCount());
        RarefactionLine line = new RarefactionLine(file.getFileName(), RarefactionColor.getColor(rarefaction.hashCode()), false,  false);
        RarefactionLine areaLine = new RarefactionLine(file.getFileName() + "_area", "#dcdcdc", true, true);
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            line.addPoint(rarefactionPoint.x, rarefactionPoint.mean);
        }
        for (Rarefaction.RarefactionPoint rarefactionPoint : rarefactionPoints) {
            areaLine.addPoint(rarefactionPoint.x, rarefactionPoint.ciL);
        }
        for (int i = rarefactionPoints.size() - 1; i >=0; --i) {
            areaLine.addPoint(rarefactionPoints.get(i).x, rarefactionPoints.get(i).ciU);
        }
        rarefactionChart = new RarefactionChart(frequencyTable.getCache(), line, areaLine);
        created = true;
        return this;
    }

    public void saveCache() throws Exception {
        if (created) {
            try {
                File cache = new File(file.getDirectoryPath() + "/" + cacheName + ".cache");
                PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
                fileWriter.write(Json.stringify(Json.toJson(rarefactionChart)));
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
