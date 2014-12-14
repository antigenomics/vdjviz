package controllers;

import models.LocalUser;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import views.html.index;

public class DatabaseSearch extends Controller {

    @SecureSocial.UserAwareAction
    public static Result databaseSearchMainPage() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        if (user != null) {
            return ok(views.html.databaseSearch.databaseSearchMainPage.render(LocalUser.find.byId(user.identityId().userId()).account.userName));
        }
        return ok(views.html.databaseSearch.databaseSearchMainPage.render(null));
    }

}
