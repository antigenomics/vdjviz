package utils;

import models.LocalUser;

import java.util.logging.Logger;

public class LogUtil {

    public static void GlobalLog(String message) {
        Logger logger = Logger.getLogger("Global");
        logger.info(message);
    }

    //TODO
    public static void UserLog(LocalUser localUser, String message) {
        Logger logger = Logger.getLogger(localUser.email);
        logger.info(message);
    }
}