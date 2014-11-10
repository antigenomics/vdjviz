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
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import play.mvc.WebSocket;
import models.UserFile;
import play.libs.Json;
import utils.ArrayUtils.Data;
import utils.ArrayUtils.xyValues;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class ComputationUtil {

    public static void vjUsageData(SampleCollection sampleCollection, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {

        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);

        List<List<Object>> data = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();
        for (int i = 0; i < jVector.length; i++) {
            for (int j = 0; j < vVector.length; j++) {
                List<Object> dataNode = new ArrayList<>();
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                dataNode.add(vVector[j]);
                dataNode.add(jVector[i]);
                dataNode.add(vjMatrix[i][j]);
                data.add(dataNode);
            }
        }

        /**
         * Optimization data
         * sort descending
         * and take first "optimization value" elements
         */

        Integer optimization_value = 35;
        List<List<Object>> opt_data = new ArrayList<>();
        Collections.sort(data, new Comparator<List<Object>>() {
            public int compare(List<Object> c1, List<Object> c2) {
                if ((double) c1.get(2) > (double) c2.get(2)) return -1;
                if ((double) c1.get(2) < (double) c2.get(2)) return 1;
                return 0;
            }
        });
        if (optimization_value > data.size()) {
            optimization_value = data.size();
        }
        for (int i = 0; i < optimization_value; i++) {
            opt_data.add(data.get(i));
        }


        File cache = new File(file.fileDirPath + "/vjUsage.cache");

        PrintWriter jsonWriter = new PrintWriter(cache.getAbsoluteFile());
        jsonWriter.write(Json.stringify(Json.toJson(opt_data)));
        jsonWriter.close();

        serverResponse.changeValue("progress", 20);
        out.write(Json.toJson(serverResponse.getData()));

    }

    public static void spectrotype(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {

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
        Data commonData = new Data(new String[]{"values", "key"});
        commonData.addData(new Object[]{commonValues.getValues(), "Other"});
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
                    topclone.getCdr3aa()});
            data.add(topCloneNode.getData());
        }

        File cache = new File(file.fileDirPath + "/spectrotype.cache");
        PrintWriter jsonWriter = new PrintWriter(cache.getAbsoluteFile());
        jsonWriter.write(Json.stringify(Json.toJson(data)));
        jsonWriter.close();

        serverResponse.changeValue("progress", 30);
        out.write(Json.toJson(serverResponse.getData()));
    }

    public static void spectrotypeV(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {
        SpectratypeV spectratypeV = new SpectratypeV(false, false);
        spectratypeV.addAll(sample);
        int default_top = 12;
        Map<String, Spectratype> collapsedSpectratypes = spectratypeV.collapse(default_top);

        List<Object> data = new ArrayList<>();

        for (String key : new HashSet<>(collapsedSpectratypes.keySet())) {
            Spectratype spectratype = collapsedSpectratypes.get(key);
            xyValues values = new xyValues();
            Data node = new Data(new String[]{"values", "key"});
            int x_coordinates[] = spectratype.getLengths();
            double y_coordinates[] = spectratype.getHistogram();
            for (int i = 0; i < x_coordinates.length; i++) {
                values.addValue(x_coordinates[i], y_coordinates[i]);
            }
            node.addData(new Object[]{values.getValues(), key});
            data.add(node.getData());
        }

        File cache = new File(file.fileDirPath + "/spectrotypeV.cache");
        PrintWriter jsonWriter = new PrintWriter(cache.getAbsoluteFile());
        jsonWriter.write(Json.stringify(Json.toJson(data)));
        jsonWriter.close();

        serverResponse.changeValue("progress", 40);
        out.write(Json.toJson(serverResponse.getData()));
    }

    public static void annotation(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {

        CdrDatabase cdrDatabase = new CdrDatabase("trdb");
        DatabaseBrowser databaseBrowser = new DatabaseBrowser(false, false, true);

        BrowserResult browserResult = databaseBrowser.query(sample, cdrDatabase);
        Data data = new Data(new String[]{"data", "header"});
        List<HashMap<String, Object>> annotationData = new ArrayList<>();
        String[] header = cdrDatabase.header;
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

        File cache = new File(file.fileDirPath + "/annotation.cache");
        PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(data.getData())));
        fileWriter.close();

        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }

    public static void basicStats(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {
        BasicStats basicStats = new BasicStats(sample);
        String[] header = BasicStats.getHEADER().split("\t");
        HashMap<String, String> basicStatsCache = new HashMap<>();
        String[] basicStatsValues = basicStats.toString().split("\t");
        basicStatsCache.put("Name", file.fileName);
        for (int i = 0; i < header.length; i++) {
            basicStatsCache.put(header[i], basicStatsValues[i]);
        }

        File cache = new File(file.fileDirPath + "/basicStats.cache");
        PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(basicStatsCache)));
        fileWriter.close();

        serverResponse.changeValue("progress", 60);
        out.write(Json.toJson(serverResponse.getData()));
    }

    public static void diversity(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {
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

        File cache = new File(file.fileDirPath + "/diversity.cache");
        PrintWriter fileWriter = new PrintWriter(cache.getAbsoluteFile());
        fileWriter.write(Json.stringify(Json.toJson(data.getData())));
        fileWriter.close();

        serverResponse.changeValue("progress", 90);
        out.write(Json.toJson(serverResponse.getData()));
    }

    public static void kernelDensity(Sample sample, UserFile file, WebSocket.Out<JsonNode> out, Data serverResponse) throws Exception {

        FrequencyTable frequencyTable = new FrequencyTable(sample, IntersectionType.Strict);
        xyValues binValues = new xyValues();
        xyValues stdValues = new xyValues();
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            binValues.addValue(binInfo.getClonotypeSize(), binInfo.getComplementaryCdf());

            if (binInfo.getClonotypeSize() - binInfo.getCloneStd() >= 1) {
                stdValues.addValueToPosition(0, binInfo.getClonotypeSize() - binInfo.getCloneStd(), binInfo.getComplementaryCdf());
            } else {
                stdValues.addValueToPosition(0, 1, frequencyTable.getBins().get(0).getComplementaryCdf());
                stdValues.addValueToPosition(0, 1, frequencyTable.getBins().get(1).getComplementaryCdf());
            }
        }
        for (FrequencyTable.BinInfo binInfo : frequencyTable.getBins()) {
            if (binInfo.getComplementaryCdf() == 0) {
                break;
            }
            stdValues.addValue(binInfo.getClonotypeSize() + binInfo.getCloneStd(), binInfo.getComplementaryCdf());
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

    public static void createSampleCache(UserFile file, WebSocket.Out<JsonNode> out) {

        /**
         * Getting Sample from text file
         */


        Software software = file.softwareType;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.filePath);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        Sample sample = sampleCollection.getAt(0);
        Data serverResponse = new Data(new String[]{"result", "action", "progress", "fileName"});

        /**
         * Creating all cache files
         */

        file.rendering = true;
        Ebean.update(file);
        serverResponse.addData(new Object[]{"ok", "render", "start", file.fileName});
        out.write(Json.toJson(serverResponse.getData()));
        try {
            vjUsageData(sampleCollection, file, out, serverResponse);
            spectrotype(sample, file, out, serverResponse);
            spectrotypeV(sample, file, out, serverResponse);
            annotation(sample, file, out, serverResponse);
            basicStats(sample, file, out, serverResponse);
            diversity(sample, file, out, serverResponse);
            kernelDensity(sample, file, out, serverResponse);
            file.rendering = false;
            file.rendered = true;
            serverResponse.changeValue("progress", "end");
            out.write(Json.toJson(serverResponse.getData()));
            Ebean.update(file);
        } catch (Exception e) {
            serverResponse.changeValue("result", "error");
            serverResponse.changeValue("progress", "end");
            serverResponse.changeValue("message", "Computation error");
            out.write(Json.toJson(serverResponse.getData()));
            e.printStackTrace();
        }
    }
}