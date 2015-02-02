
import models.Account;
import models.UserFile;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import static play.mvc.Results.notFound;

import java.io.File;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Results;
import play.mvc.SimpleResult;


public class Global extends GlobalSettings {

    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }

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
            for (UserFile userFile: account.getUserfiles()) {
                File fileDir = new File(userFile.getDirectoryPath());
                if (!fileDir.exists()) {
                    UserFile.deleteFile(userFile);
                    Logger.of("user." + account.getUserName()).warn("Deleted empty file " + userFile.getFileName() + " for user : " + account.getUserName());
                }
            }
        }
    }

    @Override
    public F.Promise<SimpleResult> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<SimpleResult>pure(Results.notFound(
                views.html.commonPages.notFound.render(requestHeader.path())));
    }

    public void onStop(Application app) {
        Logger.info("Application stopped");
    }


}
