import com.avaje.ebean.Ebean;
import models.Account;
import models.IPAddress;
import models.LocalUser;
import models.UserFile;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.mindrot.jbcrypt.BCrypt;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import scala.Option;
import scala.Some;
import scala.concurrent.duration.Duration;
import securesocial.core.*;
import securesocial.core.providers.utils.BCryptPasswordHasher;
import securesocial.core.providers.utils.PasswordHasher;
import utils.CommonUtil;
import utils.UserService;
import utils.server.Configuration;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Global extends GlobalSettings {

    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }

    @Override
    public Action onRequest(Http.Request request, Method method) {
        String ip = request.remoteAddress();
        IPAddress ipAddress = IPAddress.findByIp(ip);
        if (ipAddress == null) {
            ipAddress = new IPAddress(ip, 1L, false);
            ipAddress.save();
        } else {
            if (ipAddress.isBanned()) {
                return new Action.Simple() {
                    @Override
                    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
                        return F.Promise.pure((SimpleResult) status(0));
                    }
                };
            }
        }
        return super.onRequest(request, method);
    }

    public void onStart(Application app) {

        Logger.info("Application started");
        String uploadPath = Configuration.getUploadPath();
        File file = new File(uploadPath);
        if (!file.exists()) {
            try {
                if (!file.mkdir()) {
                    System.out.println(file.getAbsolutePath());
                };
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while creating directory for server files, please check 'uploadPath' value in application.conf file");
                System.exit(-1);
            }
        } else {
            if (!file.canRead() || !file.canWrite()) {
                System.out.println("Error: Server have no read and write access to " + uploadPath + ", please check 'uploadPath' value in application.conf file");
                System.exit(-1);
            }
        }

        //Checking existence of main directory
        String usersFilesDir = Configuration.getUploadPath();
        File applicationDir = new File(usersFilesDir + "/users/");
        if (!applicationDir.exists()) {
            Boolean createAppDir = applicationDir.mkdir();
            if (!createAppDir) {
                Logger.warn("Error while creating users directory");
                System.exit(-1);
            } else {
                Logger.info("Users directory created");
            }
        }

        if (Configuration.isApplyNewLimits()) {
            for (Account account : Account.findAll()) {
                account.setNewLimits();
            }
        }

        if (Configuration.isCreateDefaultUsers()) {
            UserService userService = new UserService(app);
            try {
                Integer nDefault = Configuration.getnDefaultUsers();
                String nameDefault = Configuration.getNameDefaultUser();
                for (int i = 1; i <= nDefault; i++) {
                    String username = nameDefault + Integer.toString(i);
                    String email = username + "@vdjviz.com";
                    LocalUser localUser = LocalUser.find.byId(email);
                    if (localUser == null) {
                        Option<PasswordHasher> bcrypt = Registry.hashers().get("bcrypt");
                        SocialUser socialUser = new SocialUser(new IdentityId(email, "userpass"),
                                username, username, String.format("%s %s", username, username),
                                Option.apply(email), null, AuthenticationMethod.UserPassword(),
                                null, null, Some.apply(new PasswordInfo("bcrypt", BCrypt.hashpw(username, BCrypt.gensalt()), null))
                        );
                        userService.doSave(socialUser);
                    }
                }
            } catch (RuntimeException e) {
                Logger.error("Error while creating default users");
                e.printStackTrace();
            }
        }

        UserService userService = new UserService(app);
        if (Configuration.isUserManagementSystemEnabled()) {
            String username = "administrator";
            try {
                List<Map<String, String>> userManagementSystemAccounts = Configuration.getUserManagementSystemAccounts();
                for (Map<String, String> userManagementSystemAccount : userManagementSystemAccounts) {
                    String email = userManagementSystemAccount.get("email");
                    String password = userManagementSystemAccount.get("password");
                    LocalUser localUser = LocalUser.find.byId(email);
                    if (localUser != null) {
                        localUser.account.setPrivelegies(true);
                        localUser.account.update();
                        localUser.update();
                    } else {
                        Option<PasswordHasher> bcrypt = Registry.hashers().get("bcrypt");
                        SocialUser socialUser = new SocialUser(new IdentityId(email, "userpass"),
                                username, username, String.format("%s %s", username, username),
                                Option.apply(email), null, AuthenticationMethod.UserPassword(),
                                null, null, Some.apply(new PasswordInfo("bcrypt", BCrypt.hashpw(password, BCrypt.gensalt()), null))
                        );
                        userService.doSave(socialUser);
                        LocalUser localUser1 = LocalUser.find.byId(email);
                        localUser1.account.setPrivelegies(true);
                        localUser1.account.update();
                        localUser1.update();
                    }
                }
            } catch (RuntimeException e) {
                Logger.error("Error while creating admin users");
                e.printStackTrace();
            }
        }

        if (Configuration.isVidjilSharingEnabled()) {
            String username = Configuration.getVidjilUser();
            LocalUser localUser = LocalUser.find.byId(username);
            try {
                if (localUser != null) {
                    localUser.account.setPrivelegies(true);
                    localUser.account.update();
                    localUser.update();
                } else {
                    Option<PasswordHasher> bcrypt = Registry.hashers().get("bcrypt");
                    SocialUser socialUser = new SocialUser(new IdentityId(username, "userpass"),
                            username, username, String.format("%s %s", username, username),
                            Option.apply(username), null, AuthenticationMethod.UserPassword(),
                            null, null, Some.apply(new PasswordInfo("bcrypt", BCrypt.hashpw(CommonUtil.RandomStringGenerator.generateRandomString(20, CommonUtil.RandomStringGenerator.Mode.ALPHANUMERIC),
                            BCrypt.gensalt()), null))
                    );
                    userService.doSave(socialUser);
                    LocalUser localUser1 = LocalUser.find.byId(username);
                    localUser1.account.setPrivelegies(true);
                    localUser1.account.update();
                    localUser1.update();
                }
            } catch (Exception e) {
                Logger.error("Vidjil user not created.");
                e.printStackTrace();
            }
        }

        //Deleting empty files
        for (Account account: Account.findAll()) {
            for (UserFile userFile: account.getUserfiles()) {
                File fileDir = new File(userFile.getDirectoryPath());
                if (!fileDir.exists() || !userFile.checkExist()) {
                    UserFile.deleteFile(userFile);
                    Logger.of("user." + account.getUserName()).warn("Deleted empty file " + userFile.getFileName() + " for user : " + account.getUserName());
                }
            }
        }
        final Integer deleteAfter = Configuration.getDeleteAfter();
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
