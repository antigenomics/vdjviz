package models;


import com.antigenomics.vdjtools.Software;
import play.data.validation.Constraints;
import play.mvc.PathBindable;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Constraint;

@Entity
public class UserFile extends Model implements PathBindable<UserFile> {

    @Id
    public Long id;
    @ManyToOne
    public Account account;
    @Constraints.Required
    public String file_name;
    public String unique_name;
    public Software software_type;
    @Constraints.Required
    public String software_type_name;
    public String file_path;
    public String file_dir_path;
    public Boolean histogramData;

    public UserFile() {}

    public UserFile(Account account, String file_name,
                    String unique_name, String software_type_name,
                    String file_path, String file_dir_path) {
        this.account = account;
        this.file_name = file_name;
        this.unique_name = unique_name;
        this.software_type = Software.byName(software_type_name);
        this.software_type_name = software_type_name;
        this.file_path = file_path;
        this.file_dir_path = file_dir_path;
        this.histogramData = false;

    }

    public static UserFile findById(Long id) {
        return find().where().eq("id", id).findUnique();
    }

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