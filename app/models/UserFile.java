package models;


import com.antigenomics.vdjtools.Software;
import play.data.validation.Constraints;
import play.mvc.PathBindable;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class UserFile extends Model implements PathBindable<UserFile> {

    @Id
    public Long id;
    @ManyToOne
    public Account account;
    @Constraints.Required
    public String fileName;
    public String uniqueName;
    //TODO
    public Software softwareType;
    @Constraints.Required
    public String softwareTypeName;
    public String filePath;
    public String fileDirPath;
    public Boolean histogramData;
    public Boolean vdjUsageData;

    public UserFile() {}

    public UserFile(Account account, String fileName,
                    String uniqueName, String softwareTypeName,
                    String filePath, String fileDirPath) {
        this.account = account;
        this.fileName = fileName;
        this.uniqueName = uniqueName;
        this.softwareType = Software.byName(softwareTypeName);
        this.softwareTypeName = softwareTypeName;
        this.filePath = filePath;
        this.fileDirPath = fileDirPath;
        this.histogramData = false;
        this.vdjUsageData = false;

    }

    public static UserFile findById(Long id) {
        return find().where().eq("id", id).findUnique();
    }

    public static UserFile findByUniqueName(String s) { return find().where().eq("uniqueName", s).findUnique(); }

    public static UserFile findbyFileName(String s) {return find().where().eq("fileName", s).findUnique(); }

    public static Model.Finder<Long, UserFile> find() {
        return new Model.Finder<>(Long.class, UserFile.class);
    }

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