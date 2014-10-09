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
    public static Result returnVdjUsageData(String fileName) throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * vdjUsage data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.vdjUsageData) {
            File jsonFile = new File(file.fileDirPath + "/vdjUsage.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.createSampleCache(file);
            flash("error", "Error");
            return ok(account.render(localAccount));
        }
    }

    @SecureSocial.SecuredAction
    public static Result returnSpectrotypeHistogram(String fileName) throws JsonProcessingException, FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.histogramData) {
            File jsonFile = new File(file.fileDirPath + "/histogram.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        } else {
            ComputationUtil.createSampleCache(file);
            flash("error", "Error");
            return ok(account.render(localAccount));
        }
    }

    @SecureSocial.SecuredAction
    public static Result returnAnnotationData(String fileName) throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Verifying access to the file
         */

        if (!localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file.annotationData) {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            FileInputStream jsonFile = new FileInputStream(annotationCacheFile);
            return ok(Json.parse(jsonFile));
        } else {
            ComputationUtil.createSampleCache(file);
            flash("error", "Error");
            return ok(account.render(localAccount));
        }
    }

    @SecureSocial.SecuredAction
    public static Result returnBasicStats() throws FileNotFoundException {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;

        List<JsonNode> basicStatsAll = new ArrayList<>();

        for (UserFile file: localAccount.userfiles) {
            File basicStatsCache = new File(file.fileDirPath + "/basicStats.cache");
            FileInputStream jsonFile = new FileInputStream(basicStatsCache);
            JsonNode basicStatsNode = Json.parse(jsonFile);
            basicStatsAll.add(basicStatsNode.get(0));
        }
        return ok(Json.toJson(basicStatsAll));

    }
}