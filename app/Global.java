
import models.Account;
import models.UserFile;
import play.Application;
import play.GlobalSettings;
import play.Play;

import java.io.File;
import play.Logger;


public class Global extends GlobalSettings {

    public void onStart(Application app) {

        Logger.info("Application started");
        //Checking existence of main directory
        String usersFilesDir = Play.application().configuration().getString("uploadPath");
        File applicationDir = new File(usersFilesDir + "/users/");
        if (!applicationDir.exists()) {
            Boolean createAppDir = applicationDir.mkdir();
            if (!createAppDir) {
                Logger.warn("Error while creating users directory");
            } else {
                Logger.info("Users directory created");
            }
        }
        //Deleting empty files
        for (Account account: Account.findAll()) {
            for (UserFile userFile: account.userfiles) {
                File fileDir = new File(userFile.fileDirPath);
                if (!fileDir.exists()) {
                    UserFile.deleteFile(userFile);
                    Logger.of("user." + account.userName).warn("Deleted empty file " + userFile.fileName + " for user : " + account.userName);
                }
            }
        }
    }

    public void onStop(Application app) {
        Logger.info("Application stopped");
    }


}
