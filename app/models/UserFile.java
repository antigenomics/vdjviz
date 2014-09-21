package models;


import play.mvc.PathBindable;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import play.data.validation.Constraints.Required;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.soap.Text;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserFile extends Model implements PathBindable<UserFile> {

    @Id
    public Long id;
    @ManyToOne
    public User user;
    public String test_string;
    public String file_path;
    //TODO

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