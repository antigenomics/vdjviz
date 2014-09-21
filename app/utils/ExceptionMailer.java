package utils;

public class ExceptionMailer {
    public static void send(Throwable e) {
        System.out.println("Sending email containg exception " + e);
    }
}