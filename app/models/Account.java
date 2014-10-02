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
    @Required
    public String userName;
    @OneToOne
    public LocalUser user;
    @OneToMany(mappedBy="account")
    public List<UserFile> userfiles;

    //It is uses for binding in routes file
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

    public Account(LocalUser user, String userName) {
        this.user = user;
        this.userName = userName;
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
