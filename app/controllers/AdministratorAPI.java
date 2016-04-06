package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Account;
import models.LocalUser;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import scala.Some;
import securesocial.core.*;
import securesocial.core.java.SecureSocial;
import securesocial.core.providers.utils.PasswordHasher;
import utils.UserService;
import utils.server.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bvdmitri on 06.04.16.
 */
@SecureSocial.SecuredAction
public class AdministratorAPI extends Controller {

    private static Account getCurrentAccount() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        LocalUser localUser = LocalUser.find.byId(user.identityId().userId());
        return localUser.account;
    }

    private static String getAdministratorName() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        return user.identityId().userId();
    }

    private static Boolean isAccessDenied() {
        return !Configuration.isUserManagementSystemEnabled() || !getCurrentAccount().isPrivilege();
    }

    public static Result index() {
        if (isAccessDenied()) {
            return redirect("/");
        }
        return ok(views.html.administrator.index.render(getAdministratorName()));
    }

    public static Result getAllAccounts() {
        if (isAccessDenied()) return badRequest();
        List<Account.AccountInformation> accounts = new ArrayList<>();
        for (Account account : Account.findAll()) {
            accounts.add(Account.getAccountInformation(account));
        }
        return ok(Json.toJson(accounts));
    }

    public static Result deleteUser() {
        if (isAccessDenied()) return badRequest("Access denied");
        JsonNode request = request().body().asJson();
        if (!request.has("userName")) {
            return badRequest(Json.toJson("Empty user name"));
        }
        try {
            Account account = Account.findByUserName(request.get("userName").asText());
            if (account == null) return badRequest("Invalid user name");
            account.deleteAccount();
            return ok("Successfully deleted");
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest("Error while deleting");
        }
    }

    public static Result createUser() {
        if (isAccessDenied()) return badRequest("Access denied");
        JsonNode request = request().body().asJson();
        if (!request.has("userName") || !request.has("password")) return badRequest("Invalid request");
        String userName = request.get("userName").asText();
        String password = request.get("password").asText();
        Integer maxFileSize = request.has("maxFileSize") ? request.get("maxFileSize").asInt() : Configuration.getMaxFileSize();
        Integer maxFilesCount = request.has("maxFilesCount") ? request.get("maxFilesCount").asInt() : Configuration.getMaxFilesCount();
        Integer maxClonotypesCount = request.has("maxClonotypesCount") ? request.get("maxClonotypesCount").asInt() : Configuration.getMaxClonotypesCount();
        Integer maxSharedGroups = request.has("maxSharedFiles") ? request.get("maxSharedFiles").asInt() : Configuration.getMaxSharedGroups();
        Boolean privelegies = request.has("privelegies") && request.get("privelegies").asBoolean();

        UserService userService = new UserService(Play.application());
        //Integer minimalPasswordLength = Play.application().configuration().getInt("securesocial.userpass.minimumPasswordLength");

        //if (password.length() < minimalPasswordLength) return badRequest("Minimal password length: " + minimalPasswordLength);

        try {
            LocalUser localUser = LocalUser.find.byId(userName);
            if (localUser != null) return badRequest("User " + userName + " already created");
            Option<PasswordHasher> bcrypt = Registry.hashers().get("bcrypt");
            SocialUser socialUser = new SocialUser(new IdentityId(userName, "userpass"),
                    userName, userName, String.format("%s %s", userName, userName),
                    Option.apply(userName), null, AuthenticationMethod.UserPassword(),
                    null, null, Some.apply(new PasswordInfo("bcrypt", BCrypt.hashpw(password, BCrypt.gensalt()), null))
            );
            userService.doSave(socialUser);
            Account createdAccount = Account.findByUserName(userName);
            createdAccount.setPrivelegies(privelegies);
            createdAccount.setMaxClonotypesCount(maxClonotypesCount);
            createdAccount.setMaxFilesCount(maxFilesCount);
            createdAccount.setMaxFilesSize(maxFileSize);
            createdAccount.setMaxSharedFiles(maxSharedGroups);
            createdAccount.update();
            createdAccount.save();
            return ok("Successfully created");
        } catch (RuntimeException e) {
            Logger.error("Error while creating default users");
            e.printStackTrace();
        }
        return ok();
    }

    public static Result editUser() {
        if (isAccessDenied()) return badRequest("Access denied");
        JsonNode request = request().body().asJson();
        if (!request.has("userName")) return badRequest("Invalid request");
        String oldUserName = request.get("userName").asText();
        Account account = Account.findByUserName(oldUserName);
        if (account == null) return badRequest("Invalid request");
        Integer maxFileSize = request.has("maxFileSize") ? request.get("maxFileSize").asInt() : account.getMaxFilesSize();
        Integer maxFilesCount = request.has("maxFilesCount") ? request.get("maxFilesCount").asInt() : account.getMaxFilesCount();
        Integer maxClonotypesCount = request.has("maxClonotypesCount") ? request.get("maxClonotypesCount").asInt() : account.getMaxClonotypesCount();
        Integer maxSharedGroups = request.has("maxSharedFiles") ? request.get("maxSharedFiles").asInt() : account.getMaxSharedFiles();
        Boolean privelegies = request.has("privelegies") && request.get("privelegies").asBoolean();

        account.setPrivelegies(privelegies);
        account.setMaxClonotypesCount(maxClonotypesCount);
        account.setMaxFilesCount(maxFilesCount);
        account.setMaxFilesSize(maxFileSize);
        account.setMaxSharedFiles(maxSharedGroups);
        account.update();
        account.save();
        return ok("Successfully edited");
    }

}
