package models;


import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IPAddress extends Model {
    @Id
    private String ip;
    private Long count;
    private Boolean banned;

    public IPAddress(String ip, Long count, Boolean banned) {
        this.ip = ip;
        this.count = count;
        this.banned = banned;
    }

    public static IPAddress findByIp(String ip) {
        return find().where().eq("ip", ip).findUnique();
    }

    public Boolean isBanned() {
        return banned;
    }

    public void count() {
        count++;
        this.update();
    }

    public void flushCount() {
        count /= 2;
        this.update();
    }

    public static Model.Finder<Long, IPAddress> find() {
        return new Model.Finder<>(Long.class, IPAddress.class);
    }
}
