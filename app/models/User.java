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
public class User implements PathBindable<User> {
    // Custom Validator for UID
    // Need to refactor ?
    public static class UidValidator extends Constraints.Validator<String> implements ConstraintValidator<UID, String> {
        final static public String message = "error.invalid.login_name";
        public UidValidator() {}
        @Override
        public void initialize(UID constraintAnnotation) {}
        @Override
        public boolean isValid(String value) {
            //TODO paterrn
            //Pattern need to be refactor?
            String pattern = "^[a-z]{8}$";
            return value != null && value.matches(pattern);
        }
        @Override
        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return new F.Tuple<String, Object[]>("error.invalid.login_name", new Object[]{});
        }
    }
    @Constraint(validatedBy = UidValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UID {
        String message() default "error.invalid.login_name";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Id
    public Long id;
    @UID
    public String login_name;
    @Required
    public String user_name;
    public String description;
    @OneToMany(mappedBy="user")
    public List<UserFile> userfiles;

    //It is uses for binding in routes file
    @Override
    public models.User bind (String key, String value) {
        return findByLogin(value);
    }
    @Override
    public String unbind(String key) {
        return this.login_name;
    }
    @Override
    public String javascriptUnbind() {
        return this.login_name;
    }

    public User() {}

    public User(String login_name, String user_name, String description) {
        this.login_name = login_name;
        this.user_name = user_name;
        this.description = description;
    }

    public String toString() {
        return String.format("%s - %s", login_name, user_name);
    }
    public String toTxt() { return String.format("%s.txt", user_name); }

    //TODO users store in memory of application -
    //TODO use database for it -
    //Database added
    /*
    private static List<User> users;
    static {
        users = new ArrayList<>();
        users.add(new User("bvdmitri", "Dmitri", "test_description"));
    }

    public static List<User> findByName(String term) {
        final List<models.User> results = new ArrayList<User>();
        for (models.User candidate : users) {
            if (candidate.user_name.toLowerCase().contains(term.toLowerCase())) {
                results.add(candidate);
            }
        }
        return results;
    }

    public static boolean remove(User user) {
        return users.remove(user);
    }

    public void save(){
        users.remove(findByLogin(this.login_name));
        users.add(this);
    }
    */
    public static List<User> findAll() {
        return find().all();
    }

    public static User findByLogin(String login_name) {
        return find().where().eq("login_name", login_name).findUnique();
    }

    public static User findById(Long database_id) {
        return find().where().eq("id", database_id).findUnique();
    }

    public static Model.Finder<Long, User> find() {
        return new Model.Finder<>(Long.class, User.class);
    }
}
