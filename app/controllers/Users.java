package controllers;

import com.avaje.ebean.Ebean;
import models.User;
import models.UserFile;
import play.Play;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;
import views.html.details;
import views.html.login;
import views.html.account;

import java.io.File;
import java.util.List;

@Catch
public class Users extends Controller {

    public static Result index() {
        return Results.redirect(routes.Users.list());
    }

    public static Result list() {
        List<User> users = User.findAll();
        return ok(login.render(users));
    }

    private static final Form<User> userForm = Form.form(User.class);

    public static Result newUser() {
        return ok(details.render(userForm));
    }

    public static Result details(User user) {
        if (user != null) {
            Form<User> filledForm = userForm.fill(user);
            return ok(details.render(filledForm));
        } else {
            flash("not found", String.format("Not found"));
            return Results.redirect(routes.Users.list());
        }
    }

    public static Result account(User user) {
        if (user != null) {
            return ok(account.render(user));
        } else {
            flash("not found", String.format("Not found"));
            return Results.redirect(routes.Users.list());
        }
    }

    public static Result save() {
        Form<User> boundForm = userForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(details.render(boundForm));
        }
        User user = boundForm.get();
        //TODO fake?
        User fake = User.findById(user.id);
        if (fake == null) {
            Ebean.save(user);
            String upload_path = Play.application().configuration().getString("file_upload_path");
            File temp_dir = new File(upload_path + user.login_name + "/");
            temp_dir.mkdir();
        } else {
            Ebean.update(user);
        }
        flash("success", String.format("Successfully added user %s", user));
        return Results.redirect(routes.Users.list());
    }

    public static Result delete(String ean) {
        final User user = User.findByLogin(ean);
        if (user == null) {
            return notFound(String.format("User %s does not exists", ean));
        }
        for (UserFile file: user.userfiles) {
            UserFiles.delete(file.id, user);
            Ebean.delete(file);
        }
        Ebean.delete(user);
        flash("remove", String.format("Successful removed user %s", user));
        return Results.redirect(routes.Users.list());
    }
}


