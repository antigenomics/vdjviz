package controllers;

import models.LocalUser;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import views.html.*;


public class Application extends Controller {

    @SecureSocial.UserAwareAction
    public static Result index() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(index.render(LocalUser.find.byId(user.identityId().userId()).account.userName));
        }
        return ok(index.render(null));
    }

    @SecureSocial.UserAwareAction
    public static Result contacts() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.commonPages.contacts.render(LocalUser.find.byId(user.identityId().userId()).account.userName));
        }
        return ok(views.html.commonPages.contacts.render(null));
    }


    @SecureSocial.UserAwareAction
    public static Result about() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = null;
        if (user != null) {
            return ok(views.html.commonPages.about.render(LocalUser.find.byId(user.identityId().userId()).account.userName));
        }
        return ok(views.html.commonPages.about.render(null));
    }

    public static Result noScriptPage() {
        return ok(views.html.commonPages.noScriptPage.render());
    }

    public static Result test() {
        return ok(views.html.test.render());
    }

}
