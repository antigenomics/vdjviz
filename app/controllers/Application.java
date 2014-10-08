package controllers;

import models.LocalUser;
import play.*;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.ComputationUtil;
import utils.LogUtil;
import views.html.*;

public class Application extends Controller {

    @SecureSocial.UserAwareAction
    public static Result index() {

        /**
         * Start page
         * Identifying User using the SecureSocial API
         * if user does not exist render navbar without user information
         * else render navbar with account button and user information
         */

        //Logger.of(Application.class).info("Index request");

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = null;
        if (user != null) {
            localUser = LocalUser.find.byId(user.identityId().userId());
        }
        if (localUser != null) {
            return ok(index.render(localUser.account.userName));
        } else {
            return ok(index.render(null));
        }
    }

}
