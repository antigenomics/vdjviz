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
            return ok(index.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(index.render(null));
    }

    @SecureSocial.UserAwareAction
    public static Result contacts() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.commonPages.contacts.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(views.html.commonPages.contacts.render(null));
    }


    @SecureSocial.UserAwareAction
    public static Result about() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.commonPages.about.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(views.html.commonPages.about.render(null));
    }

    public static Result badBrowser() {
        return ok(views.html.commonPages.badBrowserPage.render());
    }

    public static Result notFound404(String path) {
        return ok(views.html.commonPages.notFound.render(path));
    }
}
