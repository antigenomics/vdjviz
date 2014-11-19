package models;

import com.antigenomics.vdjtools.Software;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class vColor {

    @Id
    public Long id;
    public String colorKey;
    @Constraints.Required
    public String color;

    public vColor(String colorKey, String color) {
        this.colorKey = colorKey;
        this.color = color;
    }

    public static List<vColor> findByKey(String colorKey) {
        return find().where().eq("colorKey", colorKey).findList();
    }

    public static Long getColorId(String colorKey) {
        return findByKey(colorKey).get(0).id;
    }

    public static Model.Finder<Long, vColor> find() {
        return new Model.Finder<>(Long.class, vColor.class);
    }
}
