package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
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
                    Logger.of("user." + localAccount.userName).info("Render file: User " + localAccount.userName +
                            " successfully rendered file named " + file.fileName);
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
            }
        };
    }

}