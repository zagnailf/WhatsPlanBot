package ru.whatsplan.bot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.whatsplan.bot.spring.annotation.BotController;
import ru.whatsplan.bot.spring.annotation.StartBot;

import java.util.List;

@BotController
public class ZStartBotController {

    public static ReplyKeyboardMarkup defaultKeyboardMarkup = new ReplyKeyboardMarkup(
            List.of(
                    new KeyboardRow() {{
                        add(new KeyboardButton("Напоминание"));
                    }}
            )
    );

    // Из-за особенности кода контроллер StartBot должен выполняться после всех контроллеров
    @StartBot(value = "/start", next = {"/remind"})
    public SendMessage start(Update update) {

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(defaultKeyboardMarkup)
                .setText("У тебя есть возможность выполнить");
    }

}
