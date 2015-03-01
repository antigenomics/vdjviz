package controllers;

import models.IPAddress;
import models.LocalUser;
import play.Logger;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import views.html.*;


public class Application extends Controller {

    public static Boolean checkIP() {
        String ip = request().remoteAddress();
        IPAddress ipAddress = IPAddress.findByIp(ip);
        if (ipAddress == null) {
            ipAddress = new IPAddress(ip, 1L, false);
            ipAddress.save();
            return true;
        } else {
            if (ipAddress.isBanned()) {
                return false;
            } else {
                ipAddress.count();
                return true;
            }
        }
    }

    @SecureSocial.UserAwareAction
    public static Result index() {
        if (!checkIP()) {
            return badRequest();
        }
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(index.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(index.render(null));
    }

    @SecureSocial.UserAwareAction
    public static Result contacts() {
        if (!checkIP()) {
            return badRequest();
        }
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.commonPages.contacts.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(views.html.commonPages.contacts.render(null));
    }


    @SecureSocial.UserAwareAction
    public static Result about() {
        if (!checkIP()) {
            return badRequest();
        }
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.commonPages.about.render(LocalUser.find.byId(user.identityId().userId()).getAccountUserName()));
        }
        return ok(views.html.commonPages.about.render(null));
    }

    public static Result badBrowser() {
        if (!checkIP()) {
            return badRequest();
        }
        return ok(views.html.commonPages.badBrowserPage.render());
    }

}
