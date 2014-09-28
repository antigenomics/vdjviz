package controllers;

import com.antigenomics.vdjtools.Software;
import com.avaje.ebean.Ebean;
import models.Account;
import models.UserFile;
import play.Play;
import play.data.Form;
import play.mvc.*;
import utils.CommonUtil;
import utils.ComputationUtil;
import views.html.userpage;
import views.html.addfile;

import java.io.File;


@Security.Authenticated(Secured.class)
public class UserAccount extends Controller {
    public static Result userpage(Account account) {
        return ok(userpage.render(account));
    }

    private static Form<UserFile> fileForm = Form.form(UserFile.class);

    public static Result newFile(Account account) {
        if (account != null) {
            return ok(addfile.render(fileForm, account));
        }
        return redirect(routes.Application.index(null));
    }

    public static Result saveNewFile(Account account) {
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return ok(addfile.render(fileForm, account));
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        String upload_path = Play.application().configuration().getString("file_upload_path");
        if (account != null) {
            if (file != null) {
                try {
                    File upload_file = file.getFile();
                    String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                    File file_dir = (new File(upload_path + account.user_name + "/" + unique_name + "/"));
                    file_dir.mkdir();
                    upload_file.renameTo(new File(upload_path + account.user_name + "/" + unique_name + "/" + unique_name+ ".file"));
                    UserFile newFile = new UserFile(account, boundForm.get().file_name,
                                                    unique_name, boundForm.get().software_type_name,
                                                    upload_path + account.user_name + "/" + unique_name + "/" + unique_name + ".file",
                                                    file_dir.getAbsolutePath());
                    account.userfiles.add(newFile);
                    Ebean.update(account);
                    Ebean.save(newFile);
                    ComputationUtil.spectrotypeHistogram(account, newFile);
                    flash("success", "Successfully added");
                } catch (Exception e) {
                    flash("error", "Error while adding file");
                    return Results.redirect(routes.Application.redirectToAccount());
                }
            } else {
                flash("error", "Please correct the form below.");
                return ok(addfile.render(fileForm, account));
            }
        }
        return Results.redirect(routes.Application.redirectToAccount());
    }

    public static Result deleteFile(Account account, UserFile file) {
        String upload_path = Play.application().configuration().getString("file_upload_path");
        File f = new File(upload_path + account.user_name + "/" + file.unique_name + ".file");
        if (f.delete()) {
            account.userfiles.remove(file);
            Ebean.delete(file);
            return Results.redirect(routes.Application.redirectToAccount());
        } else {
            flash("error", "Error deleting file");
            return Results.redirect(routes.Application.redirectToAccount());
        }
    }
}