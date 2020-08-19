package ru.whatsplan.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.whatsplan.bot.spring.BotApiMethodController;
import ru.whatsplan.bot.spring.SelectHandle;
import ru.whatsplan.bot.spring.annotation.AfterBotRegistration;

import java.util.List;

@Component
@Slf4j
public class WhatsPlanBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.token}")
    private String token;

    @AfterBotRegistration
    public void afterBotRegistration(BotSession session) {
        log.info("IN afterBotRegistration - Session is Running: {}", session.isRunning());
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("IN onUpdateReceived - Message: {}", update.getMessage().getText());

        BotApiMethodController handle = SelectHandle.getHandle(update);
        handle.process(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.stream().parallel()
                .forEach(update -> {
                    log.info("IN onUpdateReceived(List) - Message: {}", update.getMessage().getText());

                    BotApiMethodController handle = SelectHandle.getHandle(update);
                    List<BotApiMethod<?>> methods = handle.process(update);
                    methods.forEach(this::executeMethod);
                });
    }

    private void executeMethod(BotApiMethod<?> botApiMethod) {
        try {
            execute(botApiMethod);
        } catch (TelegramApiException e) {
            log.error("TelegramApiException", e);
        }
    }

    @Override
    public String getBotUsername() {
        log.info("IN getBotUsername - username: {}", username);
        return username;
    }

    @Override
    public String getBotToken() {
        log.info("IN getBotToken - token: {}", token);
        return token;
    }
}
