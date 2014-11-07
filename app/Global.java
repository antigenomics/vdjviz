
import play.Application;
import play.GlobalSettings;
import play.Play;

import java.io.File;
import play.Logger;


public class Global extends GlobalSettings {

    public void onStart(Application app) {
        /**
         * Create the app directory in appPath (application.conf)
        **/

        Logger.info("Application started");

        String usersFilesDir = Play.application().configuration().getString("usersFilesDir");
        File applicationDir = new File(usersFilesDir);
        if (!applicationDir.exists()) {
            Boolean createAppDir = applicationDir.mkdir();
            if (!createAppDir) {
                Logger.warn("Error while creating users directory");
            } else {
                Logger.info("Users directory created");
            }
        } else {
            Logger.info("App directory already exists");
        }
    }

    public void onStop(Application app) {
        Logger.info("Application stopped");
    }

}
