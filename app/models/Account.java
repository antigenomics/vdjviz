package models;

import java.util.*;
import play.mvc.PathBindable;
import static play.data.validation.Constraints.*;
import javax.persistence.*;
import play.db.ebean.Model;


@Entity
public class Account extends Model {

    @Id
    private Long id;
    @Required
    private String userName;
    @OneToOne
    private LocalUser user;
    private String userDirPath;
    @OneToMany(mappedBy="account")
    private List<UserFile> userfiles;

    public Account(LocalUser user, String userName, String userDirPath) {
        this.user = user;
        this.userName = userName;
        this.userDirPath = userDirPath;
        this.userfiles = new ArrayList<>();
    }

    public List<UserFile> getUserfiles() {
        return userfiles;
    }

    public String getUserName() {
        return userName;
    }

    public LocalUser getUser() {
        return user;
    }

    public String getDirectoryPath() {
        return userDirPath;
    }

    public Integer getFilesCount() {
        return getUserfiles().size();
    }

    public Long getId() {
        return id;
    }

    public String toString() {
        return String.format("%s", userName);
    }

    public static List<Account> findAll() {
        return find().all();
    }

    public static Account findByUserName(String userName) {
        return find().where().eq("userName", userName).findUnique();
    }

    public static Account findById(Long database_id) {
        return find().where().eq("id", database_id).findUnique();
    }

    public static Model.Finder<Long, Account> find() {
        return new Model.Finder<>(Long.class, Account.class);
    }
}
