package models;


import com.antigenomics.vdjtools.Software;
import com.avaje.ebean.Ebean;
import play.Logger;
import play.data.validation.Constraints;
import play.mvc.PathBindable;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class UserFile extends Model implements PathBindable<UserFile> {

    @Id
    public Long id;
    @ManyToOne
    public Account account;
    public String fileName;
    public String uniqueName;
    public Software softwareType;
    @Constraints.Required
    public String softwareTypeName;
    public String filePath;
    public String fileDirPath;
    public String fileExtension;
    public Boolean rendered;
    public Boolean rendering;

    public UserFile() {}

    public UserFile(Account account, String fileName,
                    String uniqueName, String softwareTypeName,
                    String filePath, String fileDirPath, String fileExtension) {
        this.account = account;
        this.fileName = fileName;
        this.uniqueName = uniqueName;
        this.softwareType = Software.byName(softwareTypeName);
        this.softwareTypeName = softwareTypeName;
        this.filePath = filePath;
        this.fileDirPath = fileDirPath;
        this.rendered = false;
        this.rendering = false;
        this.fileExtension = fileExtension;

    }

    public static UserFile findById(Long id) {
        return find().where().eq("id", id).findUnique();
    }


    public static List<UserFile> findByAccount(Account account) {
        return find().where().eq("account", account).findList();
    }

    public static UserFile fyndByNameAndAccount(Account account, String fileName) {
        return find().where().eq("account", account).eq("fileName", fileName).findUnique();
    }

    public static Model.Finder<Long, UserFile> find() {
        return new Model.Finder<>(Long.class, UserFile.class);
    }

    public static void deleteFile(UserFile file) {
        File fileDir = new File(file.fileDirPath);
        File[] files = fileDir.listFiles();
        if (files == null) {
            if (fileDir.delete()) {
                Ebean.delete(file);
            }
            return;
        }
        for (File cache : files) {
            try {
                Files.deleteIfExists(cache.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fileDir.delete()) {
            Ebean.delete(file);
        }
    }

    /**
     * Overriding PathBindable functions
     * UseFile class using in routes with its id value
     */

    @Override
    public UserFile bind(String key, String value) {
        return findById(Long.parseLong(value));
    }

    @Override
    public String unbind(String key) {
        return this.id.toString();
    }

    @Override
    public String javascriptUnbind() {
        return this.id.toString();
    }
}