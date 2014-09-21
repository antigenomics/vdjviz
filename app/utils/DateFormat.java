package utils;

import play.data.Form;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Form.Display(name = "format.date", attributes = {"values"})
public @interface DateFormat {
    String value();
}


