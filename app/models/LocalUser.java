package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import play.db.ebean.Model;
import java.util.List;


@Entity
public class LocalUser extends Model  {

    private static final long serialVersionUID = 1L;
    @Id
    public String id;
    public String provider;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    @OneToOne
    public Account account;

    public static Finder<String, LocalUser> find = new Finder<String, LocalUser>(
            String.class, LocalUser.class
    );

    public LocalUser(String id, String provider,
                     String firstName, String lastName,
                     String email, String password) {
        this.id = id;
        this.provider = provider;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public static List<LocalUser> findAll() {
        return find.all();
    }

    @Override
    public String toString() {
        return this.id + " - " + this.firstName;
    }
}