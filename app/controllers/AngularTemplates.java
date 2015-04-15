package controllers;

import models.LocalUser;
import play.mvc.*;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

@SecureSocial.SecuredAction
public class AngularTemplates extends Controller {

    public static Result accountInformation() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(views.html.account.accountInformation.render(localUser));
    }

}
