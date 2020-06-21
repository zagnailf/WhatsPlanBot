package ru.whatsplan.bot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.whatsplan.bot.spring.annotation.BotController;
import ru.whatsplan.bot.spring.annotation.BotRequestMapping;

@BotController
public class StartBotController {

    @BotRequestMapping(value = "/start")
    public SendMessage start(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Hello user!!!");
    }

}
