package controllers;

import com.avaje.ebean.Ebean;
import models.Account;
import models.LocalUser;
import models.UserFile;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import utils.CommonUtil;
import utils.ComputationUtil;
import views.html.account;
import views.html.addfile;
import views.html.fileinformation;

import java.io.File;

public class AccountPage extends Controller {
    @SecureSocial.SecuredAction
    public static Result index() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(account.render(localUser.account));
    }

    private static Form<UserFile> fileForm = Form.form(UserFile.class);

    @SecureSocial.SecuredAction
    public static Result newFile() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        if (account != null) {
            return ok(addfile.render(fileForm, account));
        }
        return redirect(routes.AccountPage.index());
    }

    @SecureSocial.SecuredAction
    public static Result saveNewFile() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        Logger.debug(String.valueOf(boundForm.errors()));
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return ok(addfile.render(fileForm, account));
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        String uploadPath = Play.application().configuration().getString("uploadPath");
        if (account != null) {
            if (file != null) {
                try {
                    File uploadedFile = file.getFile();
                    String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                    File fileDir = (new File(uploadPath + account.userName + "/" + unique_name + "/"));
                    fileDir.mkdir();
                    uploadedFile.renameTo(new File(uploadPath + account.userName + "/" + unique_name + "/file"));
                    UserFile newFile = new UserFile(account, boundForm.get().fileName,
                            unique_name, boundForm.get().softwareTypeName,
                            uploadPath + account.userName + "/" + unique_name + "/file",
                            fileDir.getAbsolutePath());
                    account.userfiles.add(newFile);
                    Ebean.update(account);
                    Ebean.save(newFile);
                    //TODO asynchronous
                    ComputationUtil.spectrotypeHistogram(newFile);
                    ComputationUtil.vdjUsageData(newFile);
                    flash("success", "Successfully added");
                } catch (Exception e) {
                    flash("error", "Error while adding file");
                    return Results.redirect(routes.AccountPage.index());
                }
            } else {
                flash("error", "Please correct the form below.");
                return ok(addfile.render(fileForm, account));
            }
        }
        return Results.redirect(routes.AccountPage.index());
    }

    @SecureSocial.SecuredAction
    public static Result deleteFile(UserFile file) {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        String uploadPath = Play.application().configuration().getString("uploadPath");
        File f = new File(uploadPath + account.userName + "/" + file.uniqueName + "/file");
        File histogram = new File(uploadPath + account.userName + "/" + file.uniqueName + "/histogram.json");
        File vdjUdage = new File(uploadPath + account.userName + "/" + file.uniqueName + "/vdjUsage.json");
        File fileDir = new File(uploadPath + account.userName + "/" + file.uniqueName + "/");
        Boolean deleted = false;
        try {
            f.delete();
            histogram.delete();
            vdjUdage.delete();
            fileDir.delete();
            deleted = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deleted) {
            account.userfiles.remove(file);
            Ebean.delete(file);
            return Results.redirect(routes.AccountPage.index());
        } else {
            flash("error", "Error deleting file");
            return Results.redirect(routes.AccountPage.index());
        }
    }

    @SecureSocial.SecuredAction
    public static Result fileInformation(UserFile file) {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        if (account.userfiles.contains(file)) {
            return ok(fileinformation.render(account, file));
        } else {
            return redirect(routes.AccountPage.index());
        }
    }

}