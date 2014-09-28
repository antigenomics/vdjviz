package controllers;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.UserFile;
import play.libs.Json;
import play.libs.Json.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.*;
import java.util.*;

@Security.Authenticated(Secured.class)
public class Computation extends Controller{

    public static Result vdjUsageData(Account account, UserFile file, Boolean optimization) {
        if (!account.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        Software software = file.software_type;
        List<String> sampleFileNames = new ArrayList<>();
        sampleFileNames.add(file.file_path);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);
        class Table {
            public String vSegment;
            public String jSegment;
            public Double relationNum;
            public Table(String vSegment, String jSegment, Double relationNum) {
                this.vSegment = vSegment;
                this.jSegment = jSegment;
                this.relationNum = relationNum;
            }
        }

        List<Table> data = new ArrayList<>();
        String[] vVector = segmentUsage.vUsageHeader();
        String[] jVector = segmentUsage.jUsageHeader();
        for (int i = 0; i < jVector.length; i++) {
            for (int j = 0; j < vVector.length; j++) {
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                data.add(new Table(vVector[j], jVector[i], vjMatrix[i][j]));
            }
        }
        /**
         * Optimization
         **/
        if (optimization) {
            Integer optimization_value = 40;
            List<Table> opt_data = new ArrayList<>();
            Collections.sort(data, new Comparator<Table>() {
                public int compare(Table c1, Table c2) {
                    if (c1.relationNum > c2.relationNum) return -1;
                    if (c1.relationNum < c2.relationNum) return 1;
                    return 0;
                }
            });
            for (int i = 0; i < optimization_value; i++) {
                opt_data.add(data.get(i));
            }
            return ok(Json.toJson(opt_data));
        }
        return ok(Json.toJson(data));
    }


    public static Result returnSpectrotypeHistogram(Account account, UserFile file) throws JsonProcessingException, FileNotFoundException {
        if (!account.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        File jsonFile = new File(file.file_dir_path + "/" + "histogram.json");
        FileInputStream fis = new FileInputStream(jsonFile);
        JsonNode jsonData = Json.parse(fis);
        return ok(jsonData);
    }
}