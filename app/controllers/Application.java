package controllers;

import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.db.*;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import views.html.*;

import java.util.*;

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
            return ok(views.html.commonPages.contacts.render(localUser.account.userName));
        } else {
            return ok(views.html.commonPages.contacts.render(null));
        }
    }

    public static Result about() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = null;
        if (user != null) {
            localUser = LocalUser.find.byId(user.identityId().userId());
        }
        if (localUser != null) {
            return ok(views.html.commonPages.about.render(localUser.account.userName));
        } else {
            return ok(views.html.commonPages.about.render(null));
        }
    }
}
