package controllers;

import models.LocalUser;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

@SecureSocial.SecuredAction
public class AccountPage extends Controller {

    public static Result index() {

        /**
         * Identifying User using the SecureSocial API
         * and render the account page
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(views.html.account.accountMainPage.render(localUser.account));
    }

}
