package ru.whatsplan.bot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class BotApiMethodController {
    private final Object bean;
    private final Method method;
    private final Process processUpdate;

    public BotApiMethodController(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.processUpdate = typeListReturnDetect() ? this::processList : this::processSingle;
    }

    private List<BotApiMethod<?>> processSingle(Update update) {
        BotApiMethod<?> botApiMethod = (BotApiMethod<?>) ReflectionUtils.invokeMethod(method, bean, update);
        return botApiMethod != null ? Collections.singletonList(botApiMethod) : new ArrayList<>(0);
    }

    private List<BotApiMethod<?>> processList(Update update) {
        List<BotApiMethod<?>> botApiMethods = (List<BotApiMethod<?>>) ReflectionUtils.invokeMethod(method, bean, update);
        return botApiMethods != null ? botApiMethods : new ArrayList<>(0);
    }

    private boolean typeListReturnDetect() {
        return List.class.equals(method.getReturnType());
    }

    public abstract boolean successUpdatePredicate(Update update);

    public List<BotApiMethod<?>> process(Update update) {
        if (!successUpdatePredicate(update)) {
            return null;
        }
        return processUpdate.accept(update);
    }

    private interface Process {
        List<BotApiMethod<?>> accept(Update update);
    }
}
