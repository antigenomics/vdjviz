package controllers;

import models.LocalUser;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
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

    public static Result contacts() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = null;
        if (user != null) {
            localUser = LocalUser.find.byId(user.identityId().userId());
        }
        if (localUser != null) {
            return ok(contacts.render(localUser.account.userName));
        } else {
            return ok(contacts.render(null));
        }
    }

    public static Result about() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = null;
        if (user != null) {
            localUser = LocalUser.find.byId(user.identityId().userId());
        }
        if (localUser != null) {
            return ok(about.render(localUser.account.userName));
        } else {
            return ok(about.render(null));
        }
    }

}
