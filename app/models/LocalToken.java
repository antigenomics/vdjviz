package models;


import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class LocalToken extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    public String uuid;

    public String email;

    public Date createdAt;

    public Date expireAt;

    public boolean isSignUp;

    public static final Finder<String, LocalToken> find = new Finder<String, LocalToken>(
            String.class, LocalToken.class
    );
}