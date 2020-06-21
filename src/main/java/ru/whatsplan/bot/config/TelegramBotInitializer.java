package ru.whatsplan.bot.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;
import ru.whatsplan.bot.spring.annotation.AfterBotRegistration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TelegramBotInitializer implements InitializingBean {

    private final TelegramBotsApi telegramBotsApi;
    private final List<LongPollingBot> longPollingBots;
    private final List<WebhookBot> webHookBots;

    public TelegramBotInitializer(@NonNull TelegramBotsApi telegramBotsApi,
                                  @NonNull List<LongPollingBot> longPollingBots,
                                  @NonNull List<WebhookBot> webHookBots) {
        this.telegramBotsApi = telegramBotsApi;
        this.longPollingBots = longPollingBots;
        this.webHookBots = webHookBots;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (LongPollingBot bot : longPollingBots) {
            BotSession session = telegramBotsApi.registerBot(bot);
            handleAfterRegistrationHook(bot, session);
        }

        for (WebhookBot bot : webHookBots) {
            telegramBotsApi.registerBot(bot);
        }
    }

    private void handleAfterRegistrationHook(LongPollingBot bot, BotSession session) {
        Arrays.stream(bot.getClass().getMethods())
                .filter(method -> method.getAnnotation(AfterBotRegistration.class) != null)
                .forEach(method -> handleAnnotatedMethod(bot, method, session));
    }

    private void handleAnnotatedMethod(LongPollingBot bot, Method method, BotSession session) {
        if (method.getParameterCount() > 1) {
            log.warn("Method {} of Type {} has too many parameters",
                    method.getName(), method.getDeclaringClass().getCanonicalName());
            return;
        }

        if (method.getParameterCount() == 0) {
            ReflectionUtils.invokeMethod(method, bot);
            return;
        }

        if (method.getParameterTypes()[0].equals(BotSession.class)) {
            ReflectionUtils.invokeMethod(method, bot, session);
            return;
        }

        log.warn("Method {} of Type {} has invalid parameter type",
                method.getName(), method.getDeclaringClass().getCanonicalName());
    }
}
