
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
        /*
        try {
            LogUtil.createLogs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        /**
         * Create the app directory in appPath (application.conf)
        **/

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

        /*
        List<Account> accounts = Account.findAll();

        for (Account account: accounts) {
            try {
                LogUtil.createUserLog(account);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */


    }

    public void onStop(Application app) {
        /*
        LogUtil.GlobalLog("Application stopped");
        LogUtil.closeLogs();
        */
    }


    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        //Class[] filters = {CSRFFilter.class};
        Class[] filters = {};
        return filters;
    }
}
