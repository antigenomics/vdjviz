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
import utils.ComputationUtil;
import views.html.userpage;

import java.io.*;
import java.util.*;

@Security.Authenticated(Secured.class)
public class Computation extends Controller{

    public static Result returnVdjUsageData(Account account, UserFile file) throws FileNotFoundException {
        if (!account.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        if (file.histogramData) {
            File jsonFile = new File(file.file_dir_path + "/" + "vdjUsage.json");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.vdjUsageData(file);
            flash("error", "Error");
            return ok(userpage.render(account));
        }
    }


    public static Result returnSpectrotypeHistogram(Account account, UserFile file) throws JsonProcessingException, FileNotFoundException {
        if (!account.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        if (file.histogramData) {
            File jsonFile = new File(file.file_dir_path + "/" + "histogram.json");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.spectrotypeHistogram(file);
            flash("error", "Error");
            return ok(userpage.render(account));
        }
    }
}