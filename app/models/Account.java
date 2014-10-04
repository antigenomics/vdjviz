package models;

import play.data.validation.Constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

import play.libs.F;
import play.mvc.PathBindable;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import static play.data.validation.Constraints.*;

import javax.persistence.*;
import play.db.ebean.Model;


@Entity
public class Account implements PathBindable<Account> {

    @Id
    public Long id;
    @Required
    public String userName;
    @OneToOne
    public LocalUser user;
    public String userDirPath;
    @OneToMany(mappedBy="account")
    public List<UserFile> userfiles;
    public Integer filesCount;

    /**
     * Overriding PathBindable functions
     * Account class using in routes with its userName value
     */

    @Override
    public Account bind (String key, String value) {
        return findByUserName(value);
    }
    @Override
    public String unbind(String key) {
        return this.userName;
    }
    @Override
    public String javascriptUnbind() {
        return this.userName;
    }

    public Account() {}

    public Account(LocalUser user, String userName, String userDirPath) {
        this.user = user;
        this.userName = userName;
        this.userDirPath = userDirPath;
        this.filesCount = 0;
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
