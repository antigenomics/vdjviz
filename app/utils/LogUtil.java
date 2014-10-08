package utils;

import models.Account;

import java.io.IOException;
import java.util.List;
import java.util.logging.*;

public class LogUtil {
    public static Logger globalLogger = Logger.getLogger("global");
    public static Logger userLogger = Logger.getLogger("user");

    public static void createLogs() throws IOException {
        FileHandler fh = new FileHandler("/tmp/log", true);
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            globalLogger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        globalLogger.info("Application started");
    }

    public static void createUserLog(Account account) throws IOException {
        FileHandler fh = new FileHandler(account.userDirPath + "/userLog", true);
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            userLogger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void closeLogs() {
        for (Handler handler : globalLogger.getHandlers()) {
            handler.close();
        }

        List<Account> accounts = Account.findAll();
        for (Account account: accounts) {
            Logger userLogger = Logger.getLogger(account.id.toString());
            for (Handler handler: userLogger.getHandlers()) {
                handler.close();
            }
        }
    }

    public static void GlobalLog(String message) {
        globalLogger.info(message);
    }

    //TODO
    public static void UserLog(Account account, String message) {
        Logger logger = Logger.getLogger(account.id.toString());
        logger.info(message);
    }
}