package controllers;

import com.antigenomics.vdjtools.diversity.Diversity;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    public static Result index() {
        Diversity f = new com.antigenomics.vdjtools.diversity.Diversity(1,1,1,false);
        System.out.println( f.toString());
        return ok(index.render("Your new application is ready."));
    }
}
