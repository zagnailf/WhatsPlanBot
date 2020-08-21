package ru.whatsplan.bot.spring.annotation;

import ru.whatsplan.bot.spring.bot.BotRequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BotRequestMapping {
    String[] value() default {};
    BotRequestMethod[] method() default {BotRequestMethod.TEXT};
}
