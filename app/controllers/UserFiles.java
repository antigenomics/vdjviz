package controllers;

import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import controllers.*;
import models.User;
import models.UserFile;
import models.Table;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.account;
import views.html.addfile;
import views.html.visualisation;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Catch
public class UserFiles extends Controller {

    public static Result index() {
        return Results.redirect(routes.Users.list());
    }

    private static Form<UserFile> fileForm = Form.form(UserFile.class);

    public static Result add(User user) {
        if (user != null) {
            return ok(addfile.render(fileForm, user));
        }
        return redirect(routes.Users.account(user));
    }

    public static Result visualisation(User user, String data) {
        return TODO;
    }

    public static Result vdjtoolUsageMatrix(User user, UserFile file, boolean optimization) {

        /**
         * VdjTools usage
         */
        Software software = Software.MiTcr;
        List<String> sampleFileNames = new ArrayList<String>();
        sampleFileNames.add(file.file_path);
        SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
        SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
        segmentUsage.vUsageHeader();
        segmentUsage.jUsageHeader();
        String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
        double[] vVector = segmentUsage.vUsageVector(sampleId);
        double[] jVector = segmentUsage.jUsageVector(sampleId);
        double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);

        /**
         * Parsing Table vjMatrix
         */
        List<Table> data = new ArrayList<>();
        String[] vUsage = segmentUsage.vUsageHeader();
        String[] jUsage = segmentUsage.jUsageHeader();
        double summary = 0;
        for (int i = 0; i < jUsage.length; i++) {
            for (int j = 0; j < vUsage.length; j++) {
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                summary += vjMatrix[i][j];
                data.add(new Table(vUsage[j], jUsage[i], vjMatrix[i][j]));
            }
        }

        /**
         * Optimization
         */
        if (optimization) {
            List<Table> opt_data = new ArrayList<>();
            double opt_constant = 0.007;
            for (Table item : data) {
                if (item.n / summary > opt_constant) {
                    opt_data.add(item);
                }
            }
            return ok(Json.toJson(opt_data));
        }
        return ok(Json.toJson(data));
    }

    public static Result save(User user) {
        Form<UserFile> boundForm = fileForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return ok(addfile.render(fileForm, user));
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart input_file = body.getFile("file");
        String upload_path = Play.application().configuration().getString("file_upload_path");
        if (user != null) {
            if (input_file != null) {
                String fileName = input_file.getFilename();
                File upload_file = input_file.getFile();
                upload_file.renameTo(new File(upload_path + user.login_name + "/" + fileName));
                Logger.info(upload_path + user.login_name + "/" + fileName);
                UserFile file_information = boundForm.get();
                file_information.user = user;
                file_information.file_path = upload_path + user.login_name + "/" + fileName;
                user.userfiles.add(file_information);
                Ebean.save(user);
                Ebean.save(file_information);
                flash("success_file_added", String.format("Successfully added file %s to user %s", file_information, user));
            } else {
                flash("error", "Please correct the form below.");
                return ok(addfile.render(fileForm, user));
            }
        }
        return Results.redirect(routes.Users.account(user));
    }


    //TODO remake this method!
    //TODO method is unsafe
    public static Result delete(Long file_id, User user) {
        if (file_id != null && user != null) {
            UserFile file = UserFile.findById(file_id);
            if (user.userfiles.contains(file)) {
                File del = new File(file.file_path);
                del.delete();
                user.userfiles.remove(file);
            }
            Ebean.delete(file);
            Ebean.update(user);
        }
        return redirect(routes.Users.account(user));
    }
}