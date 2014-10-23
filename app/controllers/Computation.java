package controllers;


import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
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

        /**
         * Getting jsonNode of BasicStats for each file
         * is belonging to user
         */

        for (UserFile file : localAccount.userfiles) {
            if (file.rendered && !file.rendering) {
                File basicStatsCache = new File(file.fileDirPath + "/basicStats.cache");
                if (basicStatsCache.exists()) {
                    FileInputStream jsonFile = new FileInputStream(basicStatsCache);
                    JsonNode basicStatsNode = Json.parse(jsonFile);
                    basicStatsAll.add(basicStatsNode.get(0));
                }
            }
        }
        return ok(Json.toJson(basicStatsAll));

    }

    @SecureSocial.SecuredAction
    public static Result returnDiversity() throws FileNotFoundException {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        List<JsonNode> diversityAll = new ArrayList<>();

        /**
         * Getting jsonNode of diversity for each file
         * is belonging to user
         */

        for (UserFile file : localAccount.userfiles) {
            if (file.rendered && !file.rendering) {
                File diversityCache = new File(file.fileDirPath + "/diversity.cache");
                if (diversityCache.exists()) {
                    FileInputStream jsonFile = new FileInputStream(diversityCache);
                    JsonNode basicStatsNode = Json.parse(jsonFile);
                    diversityAll.add(basicStatsNode);
                }
            }
        }
        return ok(Json.toJson(diversityAll));

    }

    public static WebSocket<String> computationProgressBar(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        LocalUser localUser = LocalUser.find.byId(SecureSocial.currentUser().identityId().userId());
        final Account localAccount = localUser.account;
        final UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);

        /**
         * Creating websocket between server and user
         * which initiates rendering file and informs user about progress
         */

        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                out.write("0%");
                try {
                    file.renderCount++;
                    file.rendering = true;
                    Ebean.update(file);
                    ComputationUtil.createSampleCache(file, out);
                } catch (Exception e) {
                    out.write("ComputationError");
                    file.rendering = false;
                    file.rendered = false;
                    Ebean.update(file);
                    Logger.of("user." + localAccount.userName).info("Error render file: User " + localAccount.userName +
                            " can not render file named " + file.fileName);
                    e.printStackTrace();
                }
                out.close();
                Logger.of("user." + localAccount.userName).info("Render file: User " + localAccount.userName +
                                                                " successfully rendered file named " + file.fileName);
            }
        };
    }

    @SecureSocial.SecuredAction
    public static Result computationPage(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account localAccount = localUser.account;
        UserFile file = UserFile.fyndByNameAndAccount(localAccount, fileName);
        if (file == null || file.rendering) {
            flash("error", "You have no file named " + fileName);
            return Results.redirect(routes.AccountPage.index());
        }
        return ok(views.html.computation.computationProgressPage.render(localAccount, fileName));
    }
}