package controllers;

import com.avaje.ebean.Ebean;
import models.Account;
import models.User;
import play.api.mvc.EssentialFilter;
import play.data.Form;
import play.filters.csrf.CSRFFilter;
import static play.data.Form.form;
import play.mvc.*;
import utils.CommonUtil;
import views.html.*;

public class Application extends Controller {
    public static Result index(String username) {
        if (username != null) {
            return ok(index.render(username));
        }
        String sessionusername = session().get("username");
        return ok(index.render(sessionusername));
    }

    public static class Login {
        public String email;
        public String password;
    }

    public static Result login() {
        return ok(login.render(form(Login.class)));
    }

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index(null));
    }

    public static Result authenticate() throws Exception {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        String email = loginForm.get().email;
        String password = loginForm.get().password;
        User user = User.authenticate(email, password);
        if (user == null) {
            flash("error", "Invalid Password or Email");
            return ok(login.render(loginForm));
        }
        try {
            String sessionhash = CommonUtil.RandomStringGenerator.generateRandomString(20, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC);
            session().clear();
            user.sessionhash = sessionhash;
            Ebean.update(user);
            session("email", email);
            session("sessionhash", sessionhash);
            session("username", user.account.user_name);
        } catch (Exception e) {
            return forbidden("unexpected error");
        }

        return redirect(routes.UserAccount.userpage(user.account));
    }

    public static Result redirectToAccount() {
        String email = session().get("email");
        String sessionhash = session().get("sessionhash");
        User user = User.findByEmail(email);
        if (user != null && user.sessionhash.equals(sessionhash)) {
            return redirect(routes.UserAccount.userpage(user.account));
        } else {
            return redirect(routes.Application.index(null));
        }
    }

    public static Result sessionclear() {
        session().clear();
        return ok(index.render(null));
    }

}