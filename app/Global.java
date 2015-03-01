
import models.Account;
import models.IPAddress;
import models.UserFile;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;


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
        final Integer deleteAfter = Play.application().configuration().getInt("deleteAfter");
        if (deleteAfter > 0) {
            Akka.system().scheduler().schedule(
                    Duration.create(nextExecutionInSeconds(1, 0), TimeUnit.SECONDS),
                    Duration.create(24, TimeUnit.HOURS),
                    new Runnable() {
                        public void run() {
                            Long currentTime = new DateTime().getMillis();
                            for (Account account : Account.findAll()) {
                                if (!account.isPrivilege()) {
                                    for (UserFile userFile : account.getUserfiles()) {
                                        Long hours = (currentTime - userFile.getCreatedAtTimeInMillis()) / (60 * 60 * 1000);
                                        if (hours > deleteAfter) {
                                            UserFile.asyncDeleteFile(userFile);
                                            Logger.of("user." + account.getUserName()).info("File " + userFile.getFileName() + " was deleted");
                                        }
                                    }
                                }
                            }
                            for (IPAddress ipAddress : IPAddress.find().all()) {
                                ipAddress.flushCount();
                            }
                        }
                    },
                    Akka.system().dispatcher()
            );
        }

    }

    public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    public static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }

    public void onStop(Application app) {
        Logger.info("Application stopped");
    }


}
