package ru.whatsplan.bot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.whatsplan.bot.spring.annotation.BotController;
import ru.whatsplan.bot.spring.annotation.StartBot;

@BotController
public class ZStartBotController {

    // Из-за особенности кода контроллер StartBot должен выполняться после всех контроллеров
    @StartBot(value = "/start", next = {"/remind"})
    public SendMessage start(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("У тебя есть возможность выполнить: /remind");
    }

}
