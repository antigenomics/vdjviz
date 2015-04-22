package utils;


import com.avaje.ebean.Ebean;
import models.Account;
import models.LocalToken;
import models.LocalUser;
import org.joda.time.DateTime;
import play.Application;
import play.Logger;
import play.Play;
import scala.Option;
import scala.Some;
import securesocial.core.*;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserService extends BaseUserService {

    public UserService(Application application) {
        super(application);
    }

    @Override
    public void doDeleteExpiredTokens() {
        List<LocalToken> list = LocalToken.find.where().lt("expireAt", new DateTime().toString()).findList();
        for(LocalToken localToken : list) {
            localToken.delete();
        }
    }

    @Override
    public void doDeleteToken(String uuid) {
        LocalToken localToken = LocalToken.find.byId(uuid);
        if(localToken != null) {
            localToken.delete();
        }
    }

    @Override
    public Identity doFind(IdentityId userId) {
        LocalUser localUser = LocalUser.find.byId(userId.userId());
        if(localUser == null) return null;
        return new SocialUser(new IdentityId(localUser.id, localUser.provider),
                localUser.firstName, localUser.lastName, String.format("%s %s", localUser.firstName, localUser.lastName),
                Option.apply(localUser.email), null, new AuthenticationMethod("userPassword"),
                null, null, Some.apply(new PasswordInfo("bcrypt", localUser.password, null))
        );
    }


    @Override
    public Identity doFindByEmailAndProvider(String email, String providerId) {
        List<LocalUser> list = LocalUser.find.where().eq("email", email).eq("provider", providerId).findList();
        if (list.size() != 1) return null;
        LocalUser localUser = list.get(0);
        return new SocialUser(new IdentityId(localUser.id, localUser.provider),
                localUser.firstName, localUser.lastName, String.format("%s %s", localUser.firstName, localUser.lastName),
                Option.apply(localUser.email), null, new AuthenticationMethod("userPassword"),
                null, null, Some.apply(new PasswordInfo("bcrypt", localUser.password, null))
        );
    }

    @Override
    public Token doFindToken(String token) {
        LocalToken localToken = LocalToken.find.byId(token);
        if(localToken == null) return null;
        Token result = new Token();
        result.uuid = localToken.uuid;
        result.creationTime = new DateTime(localToken.createdAt);
        result.email = localToken.email;
        result.expirationTime = new DateTime(localToken.expireAt);
        result.isSignUp = localToken.isSignUp;
        return result;
    }

    @Override
    public Identity doSave(Identity user) {
        LocalUser localUser;
        localUser = LocalUser.find.byId(user.identityId().userId());
        if (localUser == null) {
            String usersDirPath = Play.application().configuration().getString("uploadPath") + "/users/";
            File userDir = new File(usersDirPath + "/" + user.email().get() + "/");
            if (!userDir.exists()) {
                Boolean created = userDir.mkdir();
                if (!created) {
                    Logger.of(UserService.class).error("Error while creating directory for user " + user.email().get());
                    throw new RuntimeException("Error while creating directory");
                }
            }
            localUser = new LocalUser(user.identityId().userId(), user.identityId().providerId(),
                                      user.firstName(), user.lastName(), user.email().get(),
                                      user.passwordInfo().get().password());
            Account localUserAccount = new Account(localUser, localUser.email, usersDirPath + "/" + user.email().get() + "/");
            Ebean.save(localUser);
            Ebean.save(localUserAccount);
            localUser.account = localUserAccount;
            localUser.update();
            Logger.of("UserService").info("New user " + localUser.email + " created");
        } else {
            localUser.id = user.identityId().userId();
            localUser.provider = user.identityId().providerId();
            localUser.firstName = user.firstName();
            localUser.lastName = user.lastName();
            localUser.email = user.email().get();
            localUser.password = user.passwordInfo().get().password();
            localUser.update();
        }
        return user;
    }

    @Override
    public void doSave(Token token) {
        LocalToken localToken = new LocalToken();
        localToken.uuid = token.getUuid();
        localToken.email = token.getEmail();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            localToken.createdAt = df.parse(token.getCreationTime().toString("yyyy-MM-dd HH:mm:ss"));
            localToken.expireAt = df.parse(token.getExpirationTime().toString("yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            Logger.error("SqlUserService.doSave(): ", e);
        }
        localToken.isSignUp = token.isSignUp;
        localToken.save();
    }
}