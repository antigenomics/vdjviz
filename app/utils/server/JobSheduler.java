package utils.server;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

/**
 * Created by bvdmitri on 26.04.16.
 */
public class JobSheduler {
    public static void sheduleJobOnce(Runnable job, FiniteDuration delay) {
        Akka.system().scheduler().scheduleOnce(delay, job, Akka.system().dispatcher());
    }

    public static void sheduleJob(Runnable job, FiniteDuration delay, FiniteDuration interval) {
        Akka.system().scheduler().schedule(delay, interval, job, Akka.system().dispatcher());
    }

    public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    private static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }
}
