package utils.server;

import play.Play;

public class Configuration {
    private static Integer maxClonotypesCount = Play.application().configuration().getInt("maxClonotypesCount");
    private static Integer maxFilesCount = Play.application().configuration().getInt("maxFilesCount");
    private static Integer maxFileSize = Play.application().configuration().getInt("maxFileSize");


    public static Integer getMaxClonotypesCount() {
        return maxClonotypesCount;
    }

    public static Integer getMaxFilesCount() {
        return maxFilesCount;
    }

    public static Integer getMaxFileSize() {
        return maxFileSize;
    }
}
