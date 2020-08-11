package ru.whatsplan.bot.spring;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.Webhook;
import ru.whatsplan.bot.WhatsPlanBotApplication;
import ru.whatsplan.bot.spring.bot.BotScriptContainer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BotApiMethodContainerTest {

    @BeforeAll
    public static void init() {
        ApiContextInitializer.init();
    }

    @Test
    public void testGetUserStartScript() {
        BotScriptContainer container = BotScriptContainer.getInstance();
        BotApiMethodController startMethodController = container.getBotApiMethodController(123, "/start");
        assertNotNull(startMethodController, "Не удалось получить контроллер команды /start");

        BotApiMethodController remindMethodController = container.getBotApiMethodController(123, "/remind");
        assertNotNull(remindMethodController, "Не удалось получить контроллер команды /remind");

        BotApiMethodController textMethodController = container.getBotApiMethodController(123, "/text");
        assertNotNull(textMethodController, "Не удалось получить контроллер команды /text");

        BotApiMethodController timeMethodController = container.getBotApiMethodController(123, "/time");
        assertNotNull(timeMethodController, "Не удалось получить контроллер команды /time");

        BotApiMethodController saveMethodController = container.getBotApiMethodController(123, "/save");
        assertNotNull(saveMethodController, "Не удалось получить контроллер команды /save");

        BotApiMethodController cancelMethodController = container.getBotApiMethodController(123, "/cancel");
        assertNull(cancelMethodController, "Удалось получить контроллер команды /cancel");
    }

}