package utils.server;

import play.Play;
import securesocial.core.java.SecureSocial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private static final Integer maxClonotypesCount = Play.application().configuration().getInt("maxClonotypesCount");
    private static final Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
    private static final Integer maxFileSize = Play.application().configuration().getInt("maxFileSize");
    private static final Integer maxSharedGroups = Play.application().configuration().getInt("maxSharedGroups");
    private static final Boolean createDefaultUsers = Play.application().configuration().getBoolean("createDefaultUsers");
    private static final Integer nDefaultUsers = Play.application().configuration().getInt("nDefaultUsers");
    private static final String nameDefaultUser = Play.application().configuration().getString("nameDefaultUser");
    private static final Integer deleteAfter = Play.application().configuration().getInt("deleteAfter");
    private static final String uploadPath = Play.application().configuration().getString("uploadPath");
    private static final Boolean allowRegistration = Play.application().configuration().getBoolean("allowRegistration");
    private static final Boolean allowChangePasswords = Play.application().configuration().getBoolean("allowChangePasswords");
    private static final Boolean allowSharing = Play.application().configuration().getBoolean("allowSharing");
    private static final Boolean applyNewLimitsToOldUsers = Play.application().configuration().getBoolean("applyNewLimitsToOldUsers");
    private static final Boolean allowUserManagmentSystem = Play.application().configuration().getBoolean("userManagementSystem");
    private static final List<play.Configuration> userManagmentSystemAccounts = Play.application().configuration().getConfigList("userManagementSystemAccounts");
    private static final String vidjilUser = Play.application().configuration().getString("vidjilUser");
    private static final Boolean vidjilSharingEnabled = Play.application().configuration().getBoolean("vidjilSharingEnabled");
    private static final Boolean webSocketSecure = Play.application().configuration().getBoolean("webSocketSecure");

    public static Integer getMaxClonotypesCount() {
        return maxClonotypesCount;
    }

    public static Integer getMaxFilesCount() {
        return maxFilesCount;
    }

    public static Integer getMaxFileSize() {
        return maxFileSize;
    }

    public static Integer getMaxSharedGroups() {
        return maxSharedGroups;
    }

    public static Boolean isCreateDefaultUsers() {
        return createDefaultUsers;
    }

    public static Integer getnDefaultUsers() {
        return nDefaultUsers;
    }

    public static String getNameDefaultUser() {
        return nameDefaultUser != null ? nameDefaultUser : "user";
    }

    public static Integer getDeleteAfter() {
        return deleteAfter;
    }

    public static String getUploadPath() {
        if (uploadPath.charAt(0) == '~') {
            File homeDir = new File(System.getProperty("user.home"));
            return homeDir.getAbsolutePath() + uploadPath.substring(1, uploadPath.length() - 1);
        } else {
            return uploadPath;
        }
    }

    public static List<Map<String, String>> getUserManagementSystemAccounts() {
        List<Map<String, String>> accounts = new ArrayList<>();
        //Integer minimalPasswordLength = Play.application().configuration().getConfig("securesocial").getConfig("userpass").getInt("minimumPasswordLength");
        for (play.Configuration userManagmentSystemAccount : userManagmentSystemAccounts) {
            Map<String, String> account = new HashMap<>();
            if (!userManagmentSystemAccount.keys().contains("email") || !userManagmentSystemAccount.keys().contains("password")) {
                LogAggregator.logWarning("Configuration warning: wrong user management account entry, " +
                        "please specify valid email and password, skipping..");
                continue;
            }
            String accountEmail = userManagmentSystemAccount.getString("email");
            String password = userManagmentSystemAccount.getString("password");
            /*if (password.length() < minimalPasswordLength) {
                //TODO?
                LogAggregator.logWarning("Configuration warning: invalid password for account " + accountEmail +
                    ", you can specify minimal password length in securesocial.conf file, skipping..");
                continue;
            }*/
            account.put("email", accountEmail);
            account.put("password", password);
            accounts.add(account);
        }
        return accounts;
    }

    public static Boolean isWebSocketSecure() {
        return webSocketSecure;
    }

    public static Boolean isVidjilSharingEnabled() {
        return vidjilSharingEnabled;
    }

    public static String getVidjilUser() {
        return vidjilUser;
    }

    public static Boolean isUserManagementSystemEnabled() { return allowUserManagmentSystem; }

    public static Boolean isRegistrationEnabled() {
        return allowRegistration;
    }

    public static Boolean isChangePasswordsEnabled() {
        return allowChangePasswords;
    }

    public static Boolean isSharingEnabled() {
        return allowSharing;
    }

    public static Boolean isApplyNewLimits() {
        return applyNewLimitsToOldUsers;
    }
}