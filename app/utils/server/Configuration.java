package utils.server;

import play.Play;

import java.io.File;

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

    public static Boolean isRegistrationEnabled() {
        return allowRegistration;
    }

    public static Boolean isChangePasswordsEnabled() {
        return allowChangePasswords;
    }
}