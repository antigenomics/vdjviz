package utils.server;

import play.Play;

public class Configuration {
    private static final Integer maxClonotypesCount = Play.application().configuration().getInt("maxClonotypesCount");
    private static final Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
    private static final Integer maxFileSize = Play.application().configuration().getInt("maxFileSize");
    private static final Integer maxSharedGroups = Play.application().configuration().getInt("maxSharedFiles");


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
}
