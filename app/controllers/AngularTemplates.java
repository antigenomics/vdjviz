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

public class AngularTemplates extends Controller {

    public static Result mainVisualisationContent() {
        return ok(views.html.account.mainVisualisationContent.render());
    }

    public static Result filesSidebar() {
        return ok(views.html.account.filesSidebar.render());
    }

    public static Result accountPage() {
        return ok(views.html.account.accountPage.render());
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

}
