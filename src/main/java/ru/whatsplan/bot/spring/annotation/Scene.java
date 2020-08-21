package ru.whatsplan.bot.spring.annotation;

import ru.whatsplan.bot.spring.bot.BotRequestMethod;
import ru.whatsplan.bot.spring.bot.BotSceneMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scene {
    /// value - команда которая подлежит обработке
    String[] value();
    // next[] - команды которые можно вызвать следующим сообщением
    String[] next() default {};
    // patterns[] - регулярные выражения на случай, если пользователь не прислал команду, но можно спарсить информацию из текста
    String[] patterns() default {};
    // mode - режим сценария, может быть:
    // BotSceneMode.START - стартовым, то есть команда не доступна только внутри другого скрипта
    // BotSceneMode.STEP (по умолчанию) - шаговым, команда доступна только внутри скрипта к которому он относится
    // BotSceneMode.CONTROL - последним, команда доступна только внутри скрипта к которму он относится и является одним из последних
    BotSceneMode mode() default BotSceneMode.STEP;
    // Пока не используется
    BotRequestMethod[] method() default {BotRequestMethod.TEXT};
}
