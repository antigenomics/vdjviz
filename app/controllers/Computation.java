package controllers;


import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.libs.Comet;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.ComputationUtil;

import java.io.*;
import java.util.*;


public class Computation extends Controller {

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

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/vdjUsage.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
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

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/histogram.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
    }

    @SecureSocial.SecuredAction
    public static Result returnSpectrotypeVHistogram(String fileName) throws JsonProcessingException, FileNotFoundException {

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

        if (file.rendered) {
            File jsonFile = new File(file.fileDirPath + "/histogramV.cache");
            FileInputStream fis = new FileInputStream(jsonFile);
            JsonNode jsonData = Json.parse(fis);
            return ok(jsonData);
        }
        return ok();
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

        if (file != null && !localAccount.userfiles.contains(file)) {
            return ok("You have no access to this operation");
        }

        /**
         * Verification of the existence
         * Histogram data cache file
         * if exists return jsonData
         * else render SampleCache files again
         */

        if (file != null && file.rendered) {
            File annotationCacheFile = new File(file.fileDirPath + "/annotation.cache");
            FileInputStream jsonFile = new FileInputStream(annotationCacheFile);
            return ok(Json.parse(jsonFile));
        }
        return ok();
    }

    @SecureSocial.SecuredAction
    public static Result returnBasicStats() throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        List<JsonNode> basicStatsAll = new ArrayList<>();
        for (UserFile file : localAccount.userfiles) {
            if (file.rendered) {
                File basicStatsCache = new File(file.fileDirPath + "/basicStats.cache");
                FileInputStream jsonFile = new FileInputStream(basicStatsCache);
                JsonNode basicStatsNode = Json.parse(jsonFile);
                basicStatsAll.add(basicStatsNode.get(0));
            }
        }
        return ok(Json.toJson(basicStatsAll));

    }

    @SecureSocial.SecuredAction
    public static Result returnDiversity() throws FileNotFoundException {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        List<JsonNode> diversityAll = new ArrayList<>();
        for (UserFile file : localAccount.userfiles) {
            if (file.rendered) {
                File diversityCache = new File(file.fileDirPath + "/diversity.cache");
                FileInputStream jsonFile = new FileInputStream(diversityCache);
                JsonNode basicStatsNode = Json.parse(jsonFile);
                diversityAll.add(basicStatsNode);
            }
        }
        return ok(Json.toJson(diversityAll));

    }

    public static WebSocket<String> computationProgressBar(String fileName) {
        LocalUser localUser = LocalUser.find.byId(SecureSocial.currentUser().identityId().userId());
        Account localAccount = localUser.account;
        final UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                out.write("0%");
                try {
                    ComputationUtil.createSampleCache(file, out);
                } catch (Exception e) {
                    out.write("ComputationError");
                    e.printStackTrace();
                }
                out.close();
            }
        };
    }

    @SecureSocial.SecuredAction
    public static Result computationPage(String fileName) {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);
        if (file.rendering) {
            return Results.redirect(routes.AccountPage.index());
        }
        return ok(views.html.computation.computationProgressPage.render(localAccount, fileName));
    }
}