
import models.LocalUser;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger logger = Logger.getLogger("Global");
        FileHandler fh;
        try {
            File log = new File("/tmp/log");
            fh = new FileHandler(log.getAbsolutePath());
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        logger.info("Application started");

        List<LocalUser> listOfUsers = LocalUser.findAll();

        String uploadPath = Play.application().configuration().getString("uploadPath");

        for (LocalUser user: listOfUsers) {
            Logger userLogger = Logger.getLogger(user.email);
            try {
                File userDir = new File(uploadPath + "/" + user.email + "/");
                File userLogFile = new File(userDir + "/log");
                if (!userLogFile.exists()) {
                    userLogFile.createNewFile();
                }
                FileHandler fileHandler = new FileHandler(userLogFile.getAbsolutePath());
                userLogger.addHandler(fileHandler);
                SimpleFormatter formatter = new SimpleFormatter();
                fileHandler.setFormatter(formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Create the app directory in appPath (application.conf)
         */

        String usersFilesDir = Play.application().configuration().getString("usersFilesDir");
        File applicationDir = new File(usersFilesDir);
        if (!applicationDir.exists()) {
            Boolean createAppDir = applicationDir.mkdir();
            if (!createAppDir) {
                LogUtil.GlobalLog("Error while creating app Directory");
            } else {
                LogUtil.GlobalLog("App directory created");
            }
        }

    }

    public void onStop(Application app) {
        LogUtil.GlobalLog("Application stopped");
    }

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        //Class[] filters = {CSRFFilter.class};
        Class[] filters = {};
        return filters;
    }
}
