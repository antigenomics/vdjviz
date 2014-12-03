package controllers;


import models.Account;
import models.LocalUser;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

@SecureSocial.SecuredAction
public class AngularTemplates extends Controller {

    public static Result mainVisualisationContent() {
        return ok(views.html.account.mainVisualisationContent.render());
    }

    public static Result filesSidebar() {
        return ok(views.html.account.filesSidebar.render());
    }

    public static Result accountInformation() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(views.html.account.accountInformation.render(localUser));
    }

    public static Result diversityContent() {
        return ok(views.html.account.diversityContent.render());
    }

    public static Result summaryContent() {
        return ok(views.html.account.summaryContent.render());
    }

    public static Result fileUpload() {
        return ok(views.html.account.fileUpload.render());
    }

    public static Result comparingContent() {
        return ok(views.html.account.comparingContent.render());
    }

}
