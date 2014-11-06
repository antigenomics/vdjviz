package utils;

import com.avaje.ebean.Ebean;
import models.Account;
import models.UserFile;
import play.Logger;
import play.libs.Json;

import java.io.File;
import java.nio.file.Files;

public class CommonUtil {

    public static class RandomStringGenerator {

        public enum Mode {
            ALPHA, ALPHANUMERIC, NUMERIC
        }

        public static String generateRandomString(int length, Mode mode) throws Exception {

            StringBuilder buffer = new StringBuilder();
            String characters = "";

            switch (mode) {

                case ALPHA:
                    characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    break;

                case ALPHANUMERIC:
                    characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                    break;

                case NUMERIC:
                    characters = "1234567890";
                    break;
            }

            int charactersLength = characters.length();

            for (int i = 0; i < length; i++) {
                double index = Math.random() * charactersLength;
                buffer.append(characters.charAt((int) index));
            }
            return buffer.toString();
        }
    }

    public static void deleteFile(UserFile file, Account account) {

        File fileDir = new File(file.fileDirPath);
        File[] files = fileDir.listFiles();
        if (files == null) {
            if (fileDir.delete()) {
                Ebean.delete(file);
            }
            return;
        }
        Logger.of("user." + account.userName).error("User: " + account.userName + "Server is trying to delete file " + file.fileName + " after failed rendering");
        Boolean deleted = false;
        try {
            for (File cache : files) {
                Files.deleteIfExists(cache.toPath());
            }
            if (fileDir.delete()) {
                deleted = true;
            }
        } catch (Exception e) {
            Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + file.fileName);
        }
        if (deleted) {
            Ebean.delete(file);
            Logger.of("user." + account.userName).info("User " + account.userName + " successfully deleted file named " + file.fileName);
        } else {
            Logger.of("user." + account.userName).error("User: " + account.userName + "Error while deleting file " + file.fileName);
        }
    }
}