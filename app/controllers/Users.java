package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.io.Files;
import models.UserFile;
import play.data.Form;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Results;
import views.html.details;
import views.html.list;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Catch
public class Users extends Controller {

    public static Result index() {
        return Results.redirect(Users.list());
    }

    public static Result list() {
        List<UserFile> files = UserFile.findAll();
        return ok(list.render(files));
    }

    private static final Form<UserFile> fileForm = Form.form(UserFile.class);

    public static Result newFile() {
        return ok(details.render(fileForm));
    }

    public static Result details(UserFile file) {
        if (file != null) {
            Form<UserFile> filledForm = fileForm.fill(file);
            return ok(details.render(filledForm));
        } else {
            flash("not found", String.format("Not found"));
            return Results.redirect(Users.list());
        }
    }

    public static Result save() {
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(details.render(boundForm));
        }
        UserFile file = boundForm.get();

        MultipartFormData body = request().body().asMultipartFormData();
        MultipartFormData.FilePart part = body.getFile("input_file");
        if (part != null) {
            File input_file = part.getFile();
            try {
                file.file = Files.toByteArray(input_file);
            } catch (IOException e) {
                return internalServerError("Error reading file upload");
            }
        }
        Ebean.save(file);
        flash("success", String.format("Successfully added file %s", file));
        return Results.redirect(Users.list());
    }

    public static Result delete(String ean) {
        final UserFile file = UserFile.findByEan(ean);
        if (file == null) {
            return notFound(String.format("File %s does not exists", ean));
        }
        UserFile.remove(file);
        flash("remove", String.format("Successful removed file %s", file));
        return Results.redirect(Users.list());
    }

    public static Result get_file(String ean) {
        final UserFile file = UserFile.findByEan(ean);
        if (file == null) {
            return notFound();
        }
        return ok(file.file);
    }

}


