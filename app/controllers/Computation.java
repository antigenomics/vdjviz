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
import models.LocalUser;
import models.UserFile;
import play.libs.Json;
import play.libs.Json.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.ComputationUtil;
import views.html.account;

import java.io.*;
import java.util.*;


public class Computation extends Controller{


    @SecureSocial.SecuredAction
    public static Result returnVdjUsageData(UserFile file) throws FileNotFoundException {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        if (file.histogramData) {
            File jsonFile = new File(file.fileDirPath + "/" + "vdjUsage.json");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.vdjUsageData(file);
            flash("error", "Error");
            return ok(account.render(localAccount));
        }
    }

    @SecureSocial.SecuredAction
    public static Result returnSpectrotypeHistogram(UserFile file) throws JsonProcessingException, FileNotFoundException {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }
        if (file.histogramData) {
            File jsonFile = new File(file.fileDirPath + "/" + "histogram.json");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.spectrotypeHistogram(file);
            flash("error", "Error");
            return ok(account.render(localAccount));
        }
    }
}