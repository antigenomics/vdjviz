package controllers;

import models.Account;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;

@SecureSocial.SecuredAction
public class AngularTemplates extends Controller {

    public static Result accountInformation() {
        Account account = AccountAPI.getCurrentAccount();
        return ok(views.html.account.accountInformation.render(Account.getAccountInformation(account)));
    }

}
