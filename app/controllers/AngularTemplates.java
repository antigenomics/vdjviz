package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;

@SecureSocial.SecuredAction
public class AngularTemplates extends Controller {

    public static Result accountInformation() {
        return ok(views.html.account.accountInformation.render());
    }

}
