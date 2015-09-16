package graph.RarefactionChartMultiple;


import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.diversity.Rarefaction;
import com.antigenomics.vdjtools.io.SampleFileConnection;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.metadata.MetadataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.SharedFile;
import models.SharedGroup;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import utils.CacheType.CacheType;
import utils.server.LogAggregator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class RarefactionChartShared {
    private List<RarefactionLine> lines;
    private String md5;

    private SharedGroup sharedGroup;
    private boolean created;
    private String cacheName;

    public RarefactionChartShared(SharedGroup sharedGroup) {
        this.sharedGroup = sharedGroup;
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

    public String getMd5() {
        return md5;
    }

    public JsonNode create(Boolean needToCreateNew) throws Exception {

        if (needToCreateNew) {
            Long maxCount = sharedGroup.getMaxSampleCount();
            RarefactionColor rarefactionColor = new RarefactionColor();
            for (SharedFile file: sharedGroup.getFiles()) {
                SampleFileConnection sampleFileConnection= new SampleFileConnection(file.getFilePath(), file.getSoftwareType(), MetadataUtil.createSampleMetadata(MetadataUtil.fileName2id(file.getFileName())), true, false);
                Sample sample = sampleFileConnection.getSample();
                FrequencyTable frequencyTable = new FrequencyTable(sample);
                Rarefaction rarefaction = new Rarefaction(frequencyTable);
                ArrayList<Rarefaction.RarefactionPoint> values = rarefaction.build(0, maxCount, 80);

                String color = rarefactionColor.getNext();
                RarefactionLine line = new RarefactionLine(file.getFileName(), color, false, false);
                RarefactionLine additionalLine = new RarefactionLine(file.getFileName() + "_rarefaction_add_line", color, false, true, true, file.getSampleCount());
                RarefactionLine areaLine = new RarefactionLine(file.getFileName() + "_area", "#dcdcdc", true, true);

                for (Rarefaction.RarefactionPoint value : values) {
                    areaLine.addPoint(value.getX(), value.getCiL());
                    switch (value.getRichnessType()) {
                        case Interpolated:
                            line.addPoint(value.getX(), value.getMean());
                            break;
                        case Extrapolated:
                            additionalLine.addPoint(value.getX(), value.getMean());
                            break;
                        default:
                            line.addPoint(value.getX(), value.getMean());
                            additionalLine.addPoint(value.getX(), value.getMean());
                            break;
                    }
                }

                for (int i = values.size() - 1; i >= 0; --i) {
                    areaLine.addPoint(values.get(i).getX(), values.get(i).getCiU());
                }
                addLine(areaLine);
                addLine(line);
                addLine(additionalLine);
            }
            created = true;

            saveMD5();
            saveCache();
            return Json.toJson(lines);
        } else {
            File jsonFile = new File(sharedGroup.getCachePath() + "/" + cacheName + ".cache");
            if (!jsonFile.exists()) {
                return create(true);
            }
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            if (!jsonData.findValue("md5").asText().equals(getMD5()) || !jsonData.has("lines")) {
                return create(true);
            }
            if (!jsonData.has("lines")) {
                return create(true);
            }
            return jsonData.findValue("lines");
        }
    }

    private String getMD5() throws Exception {
        List<String> fileNames = sharedGroup.getFileNames();
        List<Long> fileSizes = sharedGroup.getFileSizes();
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
        }
        for (Long fileSize : fileSizes) {
            stringBuilder.append(fileSize.toString());
        }
        String namesString = stringBuilder.toString();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(namesString.getBytes(), 0, namesString.length());
        return new BigInteger(1, messageDigest.digest()).toString(16);
    }

    private void saveMD5() throws Exception {
        List<String> fileNames = sharedGroup.getFileNames();
        List<Long> fileSizes = sharedGroup.getFileSizes();
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
        }
        for (Long fileSize : fileSizes) {
            stringBuilder.append(fileSize.toString());
        }
        String namesString = stringBuilder.toString();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(namesString.getBytes(), 0, namesString.length());
        md5 = new BigInteger(1, messageDigest.digest()).toString(16);
    }

    private void saveCache() {
        try {
            File cache = new File(sharedGroup.getCachePath() + "/" + cacheName + ".cache");
            PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
            fileWriter.write(Json.stringify(Json.toJson(this)));
            fileWriter.close();
        } catch (FileNotFoundException fnfe) {
            LogAggregator.logServerError("Error while saving cache file for shared rarefaction", sharedGroup.getAccount());
            fnfe.printStackTrace();
        }
    }

}
