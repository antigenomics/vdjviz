package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class UserFile {

    @ManyToOne
    public User user;
    public Long quantity;

    File

}