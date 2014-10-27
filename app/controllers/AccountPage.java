package controllers;

import com.antigenomics.vdjtools.Software;
import com.avaje.ebean.Ebean;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.CommonUtil;
import utils.ComputationUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static Result test() {
        return ok(views.html.test.render());
    }

}
