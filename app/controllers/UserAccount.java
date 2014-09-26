package controllers;

import com.antigenomics.vdjtools.Clonotype;
import com.antigenomics.vdjtools.Software;
import com.antigenomics.vdjtools.basic.SegmentUsage;
import com.antigenomics.vdjtools.basic.Spectratype;
import com.antigenomics.vdjtools.sample.Sample;
import com.antigenomics.vdjtools.sample.SampleCollection;
import com.avaje.ebean.Ebean;
import models.Account;
import models.User;
import models.UserFile;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import utils.CommonUtil;
import views.html.userpage;
import views.html.addfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
        Http.MultipartFormData.FilePart input_file = body.getFile("file");
        String upload_path = Play.application().configuration().getString("file_upload_path");
        if (account != null) {
            if (input_file != null) {
                try {
                    File upload_file = input_file.getFile();
                    String unique_name = CommonUtil.RandomStringGenerator.generateRandomString(30, CommonUtil.RandomStringGenerator.Mode.ALPHA);
                    upload_file.renameTo(new File(upload_path + account.user_name + "/" + unique_name + ".file"));
                    UserFile file_information = boundForm.get();
                    file_information.software_type_name = boundForm.get().software_type_name;
                    //TODO software type
                    file_information.software_type = Software.byName(boundForm.get().software_type_name);
                    file_information.account = account;
                    file_information.file_path = upload_path + account.user_name + "/" + unique_name + ".file";
                    file_information.unique_name = unique_name;
                    account.userfiles.add(file_information);
                    Ebean.update(account);
                    Ebean.save(file_information);
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
        return redirect(routes.Application.redirectToAccount());
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

    public static Result vdjtoolUsageMatrix(Account account, UserFile file, boolean optimization) {

         /**
         * VdjTools usage
         **/
         Software software = file.software_type;
         List<String> sampleFileNames = new ArrayList<String>();
         sampleFileNames.add(file.file_path);
         SampleCollection sampleCollection = new SampleCollection(sampleFileNames, software, false);
         SegmentUsage segmentUsage = new SegmentUsage(sampleCollection, false);
         segmentUsage.vUsageHeader();
         segmentUsage.jUsageHeader();
         String sampleId = sampleCollection.getAt(0).getSampleMetadata().getSampleId();
         double[][] vjMatrix = segmentUsage.vjUsageMatrix(sampleId);

         /**
         * Parsing Table vjMatrix
         **/

         class Table {
            public String vSegment;
            public String jSegment;
            public Double relationNum;
            public Table() {}
            public Table(String vSegment, String jSegment, Double relationNum) {
                this.vSegment = vSegment;
                this.jSegment = jSegment;
                this.relationNum = relationNum;
            }
         }

         List<Table> data = new ArrayList<>();
         String[] vVector = segmentUsage.vUsageHeader();
         String[] jVector = segmentUsage.jUsageHeader();
         double summary = 0;
         for (int i = 0; i < jVector.length; i++) {
             for (int j = 0; j < vVector.length; j++) {
                vjMatrix[i][j] = Math.round(vjMatrix[i][j] * 100000);
                summary += vjMatrix[i][j];
                data.add(new Table(vVector[j], jVector[i], vjMatrix[i][j]));
            }
         }

         /**
         * Optimization
         **/

         if (optimization) {
            List<Table> opt_data = new ArrayList<>();
            double opt_constant = 0.005;
                for (Table item : data) {
                    if (item.relationNum / summary > opt_constant) {
                        opt_data.add(item);
                    }
                }
            return ok(Json.toJson(opt_data));
        }
        return ok(Json.toJson(data));
    }

    //TODO spectr histrogram
    public static Result spectr() {
        Sample sample = null; //sample colleaction get at (0)
        sample.sort();
        for (Clonotype c: sample) {
            c.getCdr3nt().length(); // sequences string
            // sort by length
            // top 10

        }
        Spectratype sp = new Spectratype(false, false);
        List<Clonotype> topclones = sp.addAllFancy(sample, 10); //top 10 int
        sp.getLengths(); // coordinates x int
        sp.getHistogram();// coordinates y double
        for (Clonotype topclone: topclones ) {
            topclone.getCdr3nt().length(); // +=x
            topclone.getFreq(); // += y
        }
        return null;
    }
}