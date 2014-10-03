package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

import java.util.logging.Logger;


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

    @Override
    public String toString() {
        return this.id + " - " + this.firstName;
    }
}