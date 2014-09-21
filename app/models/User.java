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

import  javax.persistence.*;


@Entity
public class User implements PathBindable<User> {
    // Custom Validator for UID
    // Need to refactor ?
    public static class UidValidator extends Constraints.Validator<String> implements ConstraintValidator<UID, String> {
        final static public String message = "error.invalid.unique_identifier";
        public UidValidator() {}
        @Override
        public void initialize(UID constraintAnnotation) {}
        @Override
        public boolean isValid(String value) {
            //TODO paterrn
            //Pattern need to be refactor?
            String pattern = "^[0-9]{3}$";
            return value != null && value.matches(pattern);
        }
        @Override
        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return new F.Tuple<String, Object[]>("error.invalid.unique_identifier", new Object[]{});
        }
    }
    @Constraint(validatedBy = UidValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UID {
        String message() default "error.invalid.unique_identifier";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Id
    public Long database_id;
    @UID
    public String unique_identifier;
    @Required
    public String name;
    public String description;
    public byte[] file;


    public class User {
        public String UserName;
        public List<User> users = new ArrayList<>();
        public String toString() {
            return UserName;
        }
    }

    //It is uses for binding in routes file
    @Override
    public models.User bind (String key, String value) {
        return findByEan(value);
    }
    @Override
    public String unbind(String key) {
        return this.unique_identifier;
    }
    @Override
    public String javascriptUnbind() {
        return this.unique_identifier;
    }

    public User() {}

    public User(String unique_identifier, String name, String description) {
        this.unique_identifier = unique_identifier;
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return String.format("%s - %s", unique_identifier, name);
    }
    public String toTxt() { return String.format("%s.txt", name); }

    //TODO files store in memory of application
    //TODO use database for it
    private static List<models.User> files;
    static {
        files = new ArrayList<models.User>();
        //files.add(new UserFile("123", "name", "description"));
    }

    public static List<models.User> findAll() {
        return new ArrayList<models.User>(files);
    }

    public static models.User findByEan(String ean) {
        for (models.User candidate : files) {
            if (candidate.unique_identifier.equals(ean)) {
                return candidate;
            }
        }
        return null;
    }

    public static List<models.User> findByName(String term) {
        final List<models.User> results = new ArrayList<models.User>();
        for (models.User candidate : files) {
            if (candidate.name.toLowerCase().contains(term.toLowerCase())) {
                results.add(candidate);
            }
        }
        return results;
    }

    public static boolean remove(models.User userfile) {
        return files.remove(userfile);
    }

    public void save(){
        files.remove(findByEan(this.unique_identifier));
        files.add(this);
    }
}
