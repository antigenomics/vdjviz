package utils;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.BasicStats;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.basic.SpectratypeV;
import com.antigenomics.vdjtools.db.*;
import com.antigenomics.vdjtools.diversity.DiversityEstimator;
import com.antigenomics.vdjtools.diversity.DownSampler;
import com.antigenomics.vdjtools.diversity.FrequencyTable;
import com.antigenomics.vdjtools.intersection.IntersectionType;
import com.antigenomics.vdjtools.pwm.CdrPwmGrid;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;
import utils.ArrayUtils.Data;
import utils.ArrayUtils.xyValues;
import utils.VColor.VColor;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class ComputationUtil {

    private Sample sample;
    private SampleCollection sampleCollection;
    private UserFile file;
    private WebSocket.Out<JsonNode> out;
    private Data serverResponse;

    public ComputationUtil(UserFile file, WebSocket.Out<JsonNode> out) {
        Software software = file.softwareType;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        this.sample = sampleCollection.getAt(0);
        this.sampleCollection = sampleCollection;
        this.file = file;
        this.out = out;
        this.serverResponse = new Data(new String[]{"result", "action", "progress", "fileName"});
    }

    public Sample getSample() {
        return this.sample;
    }

    public SampleCollection getSampleCollection() {
        return this.sampleCollection;
    }

    public UserFile getFile() {
        return this.file;
    }

    public WebSocket.Out<JsonNode> getWebSocketOut() {
        return this.out;
    }

    public Data getServerResponse() {
        return this.serverResponse;
    }

    private void vjUsageData() throws Exception {

        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sample.getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);
        List<String> labels = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();

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
        HashMap<String, Object> data = new HashMap<>();
        data.put("matrix", matrix);
        data.put("labels", labels);
        saveCache("vjUsage", data);
        serverResponse.changeValue("progress", 20);
        out.write(Json.toJson(serverResponse.getData()));

    }

    private void spectrotype() throws Exception {

        Spectratype sp = new Spectratype(false, false);
        List<Clonotype> topclones = sp.addAllFancy(sample, 10); //top 10 int
        int[] x_coordinates = sp.getLengths();
        double[] y_coordinates = sp.getHistogram();
        int x_min = x_coordinates[0];
        int x_max = x_coordinates[x_coordinates.length - 1];

        List<HashMap<String, Object>> data = new ArrayList<>();
        xyValues commonValues = new xyValues();
        for (int i = 0; i < x_coordinates.length; i++) {
            commonValues.addValue(x_coordinates[i], y_coordinates[i]);
        }
        Data commonData = new Data(new String[]{"values", "key", "color"});
        commonData.addData(new Object[]{commonValues.getValues(), "Other", "#DCDCDC"});
        data.add(commonData.getData());
        int count = 1;
        for (Clonotype topclone : topclones) {
            Data topCloneNode = new Data(new String[]{"values", "key", "name", "v", "j", "cdr3aa"});
            xyValues values = new xyValues();
            int top_clone_x = topclone.getCdr3nt().length();
            for (int i = x_min; i <= x_max; i++) {
                values.addValue(i, i != top_clone_x ? 0 : topclone.getFreq());
            }

            topCloneNode.addData(new Object[]{
                    values.getValues(),
                    count++,
                    topclone.getCdr3nt(),
                    topclone.getV(),
                    topclone.getJ(),
                    topclone.getCdr3aa(),
            });
            data.add(topCloneNode.getData());
        }
        saveCache("spectrotype", data);
        serverResponse.changeValue("progress", 30);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void spectrotypeV() throws Exception {
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);
        List<Object> data = new ArrayList<>();
        for (String key : new HashSet<>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            xyValues values = new xyValues();
            Data node = new Data(new String[]{"values", "key", "color"});
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram();
            for (int i = 0; i < x_coordinates.length; i++) {
                values.addValue(x_coordinates[i], y_coordinates[i]);
            }
            VColor vColor = new VColor(key);
            node.addData(new Object[]{values.getValues(), key, vColor.getHexVColor()});
            if (Objects.equals(key, "other")) {
                data.add(0, node.getData());
            } else {
                data.add(node.getData());
            }
        }
        saveCache("spectrotypeV", data);
        serverResponse.changeValue("progress", 40);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void clonotypeSizeClassifying() throws Exception {
        double rare = 0, small = 0, medium = 0, large = 0, hyperexpanded = 0;
        for (Clonotype clonotype: sample) {
            double freq = clonotype.getFreq();
            if (clonotype.getCount() == 1) {
                rare += freq;
            } else  {

                if (freq < 0.001) small += freq;
                    else if (freq >= 0.001 && freq < 0.01) medium += freq;
                        else if (freq >= 0.01 && freq < 1) large += freq;
                            else if (freq >= 1) hyperexpanded += freq;
            }
        }
        List<HashMap<String, Object>> data = new ArrayList<>();
        HashMap<String, Object> rareData = new HashMap<>();
        rareData.put("label", "Rare");
        rareData.put("value", rare);
        HashMap<String, Object> mediumData = new HashMap<>();
        mediumData.put("label", "Medium");
        mediumData.put("value", medium);
        HashMap<String, Object> smallData = new HashMap<>();
        smallData.put("label", "Small");
        smallData.put("value", small);
        HashMap<String, Object> largeData = new HashMap<>();
        largeData.put("label", "Large");
        largeData.put("value", large);
        HashMap<String, Object> hyperexpandedData = new HashMap<>();
        hyperexpandedData.put("label", "HyperExpanded");
        hyperexpandedData.put("value", hyperexpanded);
        data.add(rareData);
        data.add(smallData);
        data.add(mediumData);
        data.add(largeData);
        data.add(hyperexpandedData);
        saveCache("sizeClassifying", data);
        serverResponse.changeValue("progress", 100);
        out.write(Json.toJson(serverResponse.getData()));

    }

    private void seqLogo() throws Exception {
        CdrPwmGrid cdrPwmGrid = new CdrPwmGrid();
        cdrPwmGrid.update(sample);
    }

    private void annotation() throws Exception {

        CdrDatabase cdrDatabase = new CdrDatabase();
        DatabaseBrowser databaseBrowser = new DatabaseBrowser(false, false, true);

        BrowserResult browserResult = databaseBrowser.query(sample, cdrDatabase);
        Data data = new Data(new String[]{"data", "header"});
        List<HashMap<String, Object>> annotationData = new ArrayList<>();
        String[] header = cdrDatabase.annotationHeader;
        for (CdrDatabaseMatch cdrDatabaseMatch : browserResult) {
            Data annotationDataNode = new Data(new String[]{"query_cdr3aa", "query_V", "query_J", "freq", "count"});
            Data v = new Data(new String[]{"v", "match"});
            Data j = new Data(new String[]{"j", "match"});
            Data cdr3aa = new Data(new String[]{"cdr3aa", "pos", "vend", "jstart"});
            v.addData(new Object[]{cdrDatabaseMatch.query.getV(), cdrDatabaseMatch.vMatch});
            j.addData(new Object[]{cdrDatabaseMatch.query.getJ(), cdrDatabaseMatch.jMatch});
            cdr3aa.addData(new Object[]{cdrDatabaseMatch.query.getCdr3aa(),
                                        cdrDatabaseMatch.getSubstitutions().size() > 0 ? cdrDatabaseMatch.getSubstitutions().get(0).pos : -1,
                                        cdrDatabaseMatch.query.getVEnd(),
                                        cdrDatabaseMatch.query.getJStart()});
            annotationDataNode.addData(new Object[]{cdr3aa.getData(), v.getData(), j.getData(), cdrDatabaseMatch.query.getFreq(), cdrDatabaseMatch.query.getCount()});
            String[] annotations = cdrDatabaseMatch.subject.getAnnotation();
            for (int i = 0; i < header.length; i++) {
                annotationDataNode.changeValue(header[i], annotations[i]);
            }
            annotationData.add(annotationDataNode.getData());
        }
        data.addData(new Object[]{annotationData, header});
        saveCache("annotation", data.getData());
        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }

   private void basicStats() throws Exception {
        BasicStats basicStats = new BasicStats(sample);
        String[] header = BasicStats.getHEADER().split("\t");
        HashMap<String, String> basicStatsCache = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsCache.put("Name", file.fileName);
        for (int i = 0; i < header.length; i++) {
            basicStatsCache.put(header[i], basicStatsValues[i]);
        }
        saveCache("basicStats", basicStatsCache);
        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void diversity() throws Exception {
        DiversityEstimator diversityEstimator = new DiversityEstimator(sample, IntersectionType.Strict);
        DownSampler downSampler = diversityEstimator.getDownSampler();
        Data data = new Data(new String[]{"values", "key"});
        Integer step = Math.round(sample.getCount() / 100);
        double[] x = new double[101], y = new double[101];
        int j = 0;
        for (Integer i = 0; j < 100; i += step, j++) {
            x[j] = (double) i;
            y[j] = (double) downSampler.reSample(i).getDiversity();
        }
        x[j] = (int) sample.getCount();
        y[j] = downSampler.reSample((int) sample.getCount()).getDiversity();
        LoessInterpolator interpolator = new LoessInterpolator();
        double[] z = interpolator.smooth(x, y);
        xyValues values = new xyValues();
        for (int i = 0; i <= j; i++) {
            values.addValue(x[i], z[i]);
        }
        data.addData(new Object[]{values.getValues(), file.fileName});
        saveCache("diversity", data.getData());
        serverResponse.changeValue("progress", 90);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void kernelDensity() throws Exception {

        FrequencyTable frequencyTable = new FrequencyTable(sample, IntersectionType.Strict);
        xyValues binValues = new xyValues();
        xyValues stdValues = new xyValues();
        int count = 0;
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            binValues.addValue(binInfo.getClonotypeFreq(), binInfo.getComplementaryCdf());
            if (count >= 1) {
                stdValues.addValueToPosition(0, binInfo.getClonotypeFreq() - binInfo.getClonotypeFreqStd(), binInfo.getComplementaryCdf());
            } else {
                stdValues.addValueToPosition(0, frequencyTable.getBins().get(1).getClonotypeFreq() - frequencyTable.getBins().get(1).getClonotypeFreqStd(), binInfo.getComplementaryCdf());
            }
            count++;
        }
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            stdValues.addValue(binInfo.getClonotypeFreq() + binInfo.getClonotypeFreqStd(), binInfo.getComplementaryCdf());
        }
        List<HashMap<String, Object>> data = new ArrayList<>();
        Data binData = new Data(new String[]{"values", "key"});
        Data stdData = new Data(new String[]{"values", "key", "area"});
        binData.addData(new Object[]{binValues.getValues(), "bin"});
        stdData.addData(new Object[]{stdValues.getValues(), "std", true});
        data.add(binData.getData());
        data.add(stdData.getData());

        File cache = new File(file.fileDirPath + "/kernelDensity.cache");
        PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(data)));
        fileWriter.close();

        serverResponse.changeValue("progress", 100);
        out.write(Json.toJson(serverResponse.getData()));
    }

    private void saveCache(String cacheName, Object data) throws Exception {
        File cache = new File(file.fileDirPath + "/" + cacheName + ".cache");
        PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(data)));
        fileWriter.close();
    }

    public void createSampleCache() throws Exception {
        file.rendering = true;
        Ebean.update(file);
        serverResponse.addData(new Object[]{"ok", "render", "start", file.fileName});
        out.write(Json.toJson(serverResponse.getData()));
        vjUsageData();
        spectrotype();
        spectrotypeV();
        annotation();
        basicStats();
        diversity();
        //seqLogo(sample, file, out, serverResponse);
        clonotypeSizeClassifying();
        //kernelDensity(sample, file, out, serverResponse);
        file.rendering = false;
        file.rendered = true;
        serverResponse.changeValue("progress", "end");
        out.write(Json.toJson(serverResponse.getData()));
        Ebean.update(file);
    }
}
