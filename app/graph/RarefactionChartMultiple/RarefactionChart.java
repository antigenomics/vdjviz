package graph.RarefactionChartMultiple;


import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.diversity.Rarefaction;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RarefactionChart {
    private List<RarefactionLine> lines;

    private Account account;
    private boolean created;
    private String cacheName;

    public RarefactionChart(Account account) {
        this.account = account;
        this.created = false;
        this.cacheName = CacheType.rarefaction.getCacheFileName();
        this.lines = new ArrayList<>();
    }

    private void addLine(RarefactionLine line) {
        lines.add(line);
    }

    public List<RarefactionLine> getLines() throws Exception {
        if (created)
            return lines;
        throw new Exception("You should create graph");
    }

    public JsonNode create(Boolean needToCreateNew) throws Exception {

        if (needToCreateNew) {
            Long maxCount = UserFile.getMaxSampleCount();
            RarefactionColor rarefactionColor = new RarefactionColor();
            for (UserFile userFile : account.getRenderedUserFiles()) {
                Software software = userFile.getSoftwareType();
                List<String> sampleFileNames = new ArrayList<>();
                sampleFileNames.add(userFile.getPath());
                SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
                Sample sample = sampleCollection.getAt(0);
                FrequencyTable frequencyTable = new FrequencyTable(sample);
                Rarefaction rarefaction = new Rarefaction(frequencyTable);
                ArrayList<Rarefaction.RarefactionPoint> values = rarefaction.build(0, maxCount, 40);

                String color = rarefactionColor.getNext();
                RarefactionLine line = new RarefactionLine(userFile.getFileName(), color, false, false);
                RarefactionLine additionalLine = new RarefactionLine(userFile.getFileName() + "_rarefaction_add_line", color, false, true, true, userFile.getSampleCount());
                RarefactionLine areaLine = new RarefactionLine(userFile.getFileName() + "_area", "#dcdcdc", true, true);

                for (Rarefaction.RarefactionPoint value : values) {
                    areaLine.addPoint(value.x, value.ciL);
                    switch (value.diversityType) {
                        case Interpolated:
                            line.addPoint(value.x, value.mean);
                            break;
                        case Extrapolated:
                            additionalLine.addPoint(value.x, value.mean);
                            break;
                        default:
                            line.addPoint(value.x, value.mean);
                            additionalLine.addPoint(value.x, value.mean);
                            break;
                    }
                }

                for (int i = values.size() - 1; i >= 0; --i) {
                    areaLine.addPoint(values.get(i).x, values.get(i).ciU);
                }
                addLine(areaLine);
                addLine(line);
                addLine(additionalLine);
            }
            created = true;
            saveCache();
            return Json.toJson(lines);
        } else {
            File jsonFile = new File(account.getDirectoryPath() + "/" + cacheName + ".cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return jsonData.findValue("lines");
        }
    }

    private void saveCache() {
        try {
            File cache = new File(account.getDirectoryPath() + "/" + cacheName + ".cache");
            PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
            fileWriter.write(Json.stringify(Json.toJson(this)));
            fileWriter.close();
        } catch (FileNotFoundException fnfe) {
            Logger.of("user." + account.getUserName()).error("User " + account.getUserName() +
                    ": save cache error [" + cacheName + "]");
            fnfe.printStackTrace();
        }
    }

}
