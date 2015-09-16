package utils.server;

import models.Account;
import play.Logger;

public class LogAggregator {
    public enum LogType {
        SERVER,
        CLIENT,
        CLIENT_SHARED
    }

    public static void log(String message, LogType logType, Account account) {
        switch (logType) {
            case SERVER:
                Logger.of("server." + account.getUserName()).info("User " + account.getUserName() + ": " + message);
                break;
            case CLIENT:
                Logger.of("client." + account.getUserName()).info("User " + account.getUserName() + ": " + message);
                break;
            case CLIENT_SHARED:
                Logger.of("client_shared." + account.getUserName()).info("Shared content of " + account.getUserName() + ": " + message);
                break;
        }
    }

    public static void logClientError(String message, Account account) {
        log(message, LogType.CLIENT, account);
    }

    public static void logServerError(String message, Account account) {
        log(message, LogType.SERVER, account);
    }

    public static void logClientSharedContentError(String message, Account account) {
        log(message, LogType.CLIENT_SHARED, account);
    }

    public static void logError(String message) {
        Logger.warn(message);
    }

}
