package controllers;

import com.avaje.ebean.Ebean;
import models.Account;
import models.LocalUser;
import models.UserFile;
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
import utils.LogUtil;
import views.html.account;
import views.html.addfile;
import views.html.fileinformation;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@SecureSocial.SecuredAction
public class AccountPage extends Controller {


    public static Result index() {

        /**
         * Identifying User using the SecureSocial API
         * and render the account page
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return ok(account.render(localUser.account));
    }

    private static Form<UserFile> fileForm = Form.form(UserFile.class);


    public static Result newFile() {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        /**
         * Render addfile.scala.html file
         */

        if (account != null) {
            return ok(addfile.render(fileForm, account));
        }
        return redirect(routes.AccountPage.index());
    }


    public static Result saveNewFile() {

        /**
         * Identifying user using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return ok(addfile.render(fileForm, account));
        }

        /**
         * Getting the file from request body
         */

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");

        /**
         * Getting fileName
         * If User do not enter the name of file
         * it will be fills automatically using current file name
         */

        String fileName = null;
        if (boundForm.get().fileName.equals("")) {
            fileName = FilenameUtils.removeExtension(file.getFilename());
        } else {
            fileName = boundForm.get().fileName;
        }
        /**
         *  Getting uploadPath
         *  Configure uploadPath in /conf/application.conf
         *  ---
         *  Debug only : uploadPath = /tmp/
         */

        String uploadPath = Play.application().configuration().getString("uploadPath");

        /**
         * Verification of the existence the account and the file
         */

        if (account != null) {
            if (file != null) {
                try {

                    /**
                     * Creation the UserFile class
                     */

                    File uploadedFile = file.getFile();
                    String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                    File fileDir = (new File(uploadPath + account.userName + "/" + unique_name + "/"));
                    fileDir.mkdir();
                    uploadedFile.renameTo(new File(uploadPath + account.userName + "/" + unique_name + "/file"));
                    UserFile newFile = new UserFile(account, fileName,
                            unique_name, boundForm.get().softwareTypeName,
                            uploadPath + account.userName + "/" + unique_name + "/file",
                            fileDir.getAbsolutePath());

                    /**
                     * Database updating with relationships
                     * UserFile <-> Account
                     */

                    account.userfiles.add(newFile);
                    Ebean.update(account);
                    Ebean.save(newFile);

                    /**
                     * Creating Sample cache files
                     *  - Vdj relationships
                     *  - Histogram
                     *  - Annotation table
                     *
                     *  On success redirect to account page
                     *  On error cause exception
                     */

                    ComputationUtil.createSampleCache(newFile);
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


    public static Result deleteFile(UserFile file) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        /**
         *  Getting uploadPath
         *  Configure uploadPath in /conf/application.conf
         *  ---
         *  Debug only : uploadPath = /tmp/
         */

        String uploadPath = Play.application().configuration().getString("uploadPath");

        /**
         * Getting file directory
         * and try to delete it
         */

        String fileDirectoryName =  uploadPath + account.userName + "/" + file.uniqueName;

        File f = new File(fileDirectoryName + "/file");
        File histogram = new File(fileDirectoryName + "/histogram.cache");
        File vdjUsage = new File(fileDirectoryName + "/vdjUsage.cache");
        File annotation = new File(fileDirectoryName + "/annotation.cache");
        File fileDir = new File(fileDirectoryName + "/");
        Boolean deleted = false;
        try {
            if (f.delete() && annotation.delete()
                           && histogram.delete()
                           && vdjUsage.delete()
                           && fileDir.delete() ) {
                deleted = true;
            }
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


    public static Result fileInformation(UserFile file) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        /**
         * Verifying access to the file
         * if file belong to User redirect
         * to file information page
         * else redirect to the account page
         */

        if (account.userfiles.contains(file)) {
            return ok(fileinformation.render(account, file));
        } else {
            return redirect(routes.AccountPage.index());
        }
    }

    public static Result renderFileAgain(UserFile file) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        /**
         * Verifying access to the file
         * if file belong to User render SampleCache again
         * and update all cache files
         * else
         * redirect to the account page
         */

        if (account.userfiles.contains(file)) {
            ComputationUtil.createSampleCache(file);
        }
        return redirect(routes.AccountPage.index());
    }
}