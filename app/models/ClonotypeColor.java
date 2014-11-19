package models;

import com.antigenomics.vdjtools.Software;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ClonotypeColor {

    @Id
    public Long id;
    public String colorKey;
    @Constraints.Required
    public String color;

    public ClonotypeColor(String colorKey, String color) {
        this.colorKey = colorKey;
        this.color = color;
    }

    public static ClonotypeColor findByKey(String colorKey) {
        return find().where().eq("colorKey", colorKey).findUnique();
    }

    public static Model.Finder<Long, ClonotypeColor> find() {
        return new Model.Finder<>(Long.class, ClonotypeColor.class);
    }
}
