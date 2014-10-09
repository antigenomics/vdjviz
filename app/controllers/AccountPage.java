package controllers;

import com.antigenomics.vdjtools.Software;
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
import utils.LogUtil;
import views.html.account;
import views.html.addfile;
import views.html.fileinformation;
import org.apache.commons.io.FilenameUtils;
import views.html.fileupdate;

import java.io.File;
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

        /**
         * Use this to log
         */
        //TODO
        Logger.of("user."+localUser.email).info("user page request");

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

        if (account == null) {
            flash("error", "Account does not exist");
            return redirect(routes.Application.index());
        }

        /**
         * Checking files count
         */

        if (account.filesCount >= 10) {
            flash("info", "You have exceeded the limit of the number of files");
            return Results.redirect(routes.AccountPage.index());
        }

        /**
         * Getting boundForm from request
         */

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

        List<UserFile> allFiles = UserFile.findByAccount(account);
        for (UserFile userFile: allFiles) {
            if (userFile.fileName.equals(fileName)) {
                flash("error", "You should use unique names for your files");
                return ok(addfile.render(fileForm, account));
            }
        }

        /**
         * Verification of the existence the account and the file
         */

        if (file != null) {
            try {

                /**
                 * Creation the UserFile class
                 */

                File uploadedFile = file.getFile();
                String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                File fileDir = (new File(account.userDirPath + "/" + unique_name + "/"));

                /**
                 * Trying to create file's directory
                 * if failed redirect to the account
                 */

                if (!fileDir.exists()) {
                    Boolean created = fileDir.mkdir();
                    if (!created) {
                        flash("error","Error while adding file");
                        return Results.redirect(routes.AccountPage.index());
                    }
                }

                /**
                 * Checking
                 */

                Boolean uploaded = uploadedFile.renameTo(new File(account.userDirPath + "/" + unique_name + "/file"));
                if (!uploaded) {
                    flash("error", "Error while adding file");
                    return Results.redirect(routes.AccountPage.index());
                }

                UserFile newFile = new UserFile(account, fileName,
                        unique_name, boundForm.get().softwareTypeName,
                        account.userDirPath + "/" + unique_name + "/file",
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

                account.filesCount++;
                Ebean.update(account);
                try {
                    ComputationUtil.createSampleCache(newFile);
                } catch (Exception e) {
                    flash("error", "Error rendering file, maybe you had choose the wrong software type, update software type and try render again");
                    return Results.redirect(routes.AccountPage.index());
                }
                flash("success", "Successfully added");
            } catch (Exception e) {
                flash("error", "Error while adding file");
                return Results.redirect(routes.AccountPage.index());
            }
        } else {
            flash("error", "Please correct the form below.");
            return ok(addfile.render(fileForm, account));
        }
        return Results.redirect(routes.AccountPage.index());
    }


    public static Result deleteFile(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

        /**
         * Getting file directory
         * and try to delete it
         */

        String fileDirectoryName =  account.userDirPath + "/" + file.uniqueName;

        File f = new File(fileDirectoryName + "/file");
        File histogram = new File(fileDirectoryName + "/histogram.cache");
        File vdjUsage = new File(fileDirectoryName + "/vdjUsage.cache");
        File annotation = new File(fileDirectoryName + "/annotation.cache");
        File basicStats = new File(fileDirectoryName + "/basicStats.cache");
        File fileDir = new File(fileDirectoryName + "/");
        Boolean deleted = false;
        try {
            f.delete();
            annotation.delete();
            basicStats.delete();
            histogram.delete();
            vdjUsage.delete();
            if (fileDir.delete()) {
                deleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deleted) {
            account.userfiles.remove(file);
            Ebean.delete(file);
            account.filesCount--;
            Ebean.update(account);
            return Results.redirect(routes.AccountPage.index());
        } else {
            flash("error", "Error deleting file");
            return Results.redirect(routes.AccountPage.index());
        }
    }


    public static Result fileInformation(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;


        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

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

    public static Result fileUpdatePage(String fileName) {
        /**
         * Identifying User using the SecureSocial API
         */
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);
        return ok(fileupdate.render(Form.form(UserFile.class).fill(file), account, fileName));
    }

    //TODO
    public static Result fileUpdate(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

        if (account == null) {
            flash("error", "Account does not exist");
            return redirect(routes.Application.index());
        }

        if (account.userfiles.contains(file)) {
            Form<UserFile> boundForm = fileForm.bindFromRequest();
            if (boundForm.hasErrors()) {
                flash("error", "Please correct the form below.");
                return ok(addfile.render(fileForm, account));
            }

        } else {
            return ok("access restricted");
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart newFile = body.getFile("file");
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        file.fileName = boundForm.get().fileName;
        String oldSoftwareType = file.softwareTypeName;
        file.softwareTypeName = boundForm.get().softwareTypeName;
        file.softwareType = Software.byName(boundForm.get().softwareTypeName);
        Ebean.update(file);
        if (newFile != null) {
            File nf = new File(file.fileDirPath + "/file");
            nf.delete();
            Boolean uploaded = newFile.getFile().renameTo(new File(account.userDirPath + "/" + file.uniqueName + "/file"));
            if (!uploaded) {
                flash("error", "Error while adding file");
                return Results.redirect(routes.AccountPage.index());
            }
            ComputationUtil.createSampleCache(file);
        } else if (!file.softwareTypeName.equals(oldSoftwareType)) {
            ComputationUtil.createSampleCache(file);
        }
        return redirect(routes.AccountPage.fileInformation(file.fileName));
    }

    public static Result renderFileAgain(String fileName) {

        /**
         * Identifying User using the SecureSocial API
         */

        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;

        UserFile file = UserFile.fyndByNameAndAccount(account, fileName);

        /**
         * Verifying access to the file
         * if file belong to User render SampleCache again
         * and update all cache files
         * else
         * redirect to the account page
         */

        if (account !=null && account.userfiles.contains(file)) {
            ComputationUtil.createSampleCache(file);
        }
        return redirect(routes.AccountPage.index());
    }

    public static Result test() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        Account account = localUser.account;
        List<UserFile> all = UserFile.findByAccount(account);
        return ok(String.valueOf(all));
    }
}