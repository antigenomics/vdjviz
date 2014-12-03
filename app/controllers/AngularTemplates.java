package controllers;


import play.mvc.*;
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
        return ok(views.html.account.accountInformation.render());
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
