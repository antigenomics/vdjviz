
import models.Account;
import models.LocalUser;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;

import java.io.File;
import play.Logger;


public class Global extends GlobalSettings {

    public void onStart(Application app) {
        /**
         * Create the app directory in appPath (application.conf)
        **/

        Logger.of(Global.class).info("Application started");

        String usersFilesDir = Play.application().configuration().getString("usersFilesDir");
        File applicationDir = new File(usersFilesDir);
        if (!applicationDir.exists()) {
            Boolean createAppDir = applicationDir.mkdir();
            if (!createAppDir) {
                Logger.of(Global.class).warn("Error while creating users directory");
            } else {
                Logger.of(Global.class).info("Users directory created");
            }
        } else {
            Logger.of(Global.class).info("App directory already exists");
        }
    }

    public void onStop(Application app) {
        Logger.of(Global.class).info("Application stopped");
    }


    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        //Class[] filters = {CSRFFilter.class};
        Class[] filters = {};
        return filters;
    }
}
