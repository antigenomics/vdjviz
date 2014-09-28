import controllers.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.data.format.Formatters;
import play.data.format.Formatters.*;
import utils.AnnotationDateFormatter;

import controllers.Application.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Formatters.register(Date.class,
            new SimpleFormatter<Date>() {
                private final static String PATTERN = "dd-MM-yyyy";

                public Date parse(String text, Locale locale)
                    throws ParseException {
                    if (text == null || text.trim().isEmpty()) {
                        return null;
                    }
                    SimpleDateFormat sdf =
                        new SimpleDateFormat(PATTERN, locale);

                    sdf.setLenient(false);
                    return sdf.parse(text);
                }

                public String print(Date value, Locale locale) {
                    if (value == null) {
                        return "";
                    }
                return new SimpleDateFormat(PATTERN, locale).format(value);
                }
            });
        Formatters.register(Date.class, new AnnotationDateFormatter());
    }

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        Class[] filters = {CSRFFilter.class};
        return filters;
    }
}