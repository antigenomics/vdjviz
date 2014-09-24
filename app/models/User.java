package models;

import play.mvc.PathBindable;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Constraint;
import java.util.List;

@Entity
public class User implements PathBindable<User> {
    @Id
    public Long id;
    @Constraints.Required
    public String email;
    @Constraints.Required
    public String password;
    public String sessionhash;
    @OneToOne
    public Account account;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User authenticate(String email, String password) {
        User user = User.findByEmail(email);
        if (user != null && user.password.equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public static Model.Finder<Long, User> find() {
        return new Model.Finder<Long, User>(Long.class, User.class);
    }

    public static User findByEmail(String email) {
        return find().where().eq("email", email).findUnique();
    }

    public static List<User> findAll() {
        return find().all();
    }

    @Override
    public User bind(String key, String value) {
        return User.findByEmail(value);
    }

    @Override
    public String unbind(String key) {
        return this.email;
    }

    @Override
    public String javascriptUnbind() {
        return this.email;
    }
}
