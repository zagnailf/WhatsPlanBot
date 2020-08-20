package ru.whatsplan.bot.controller;

import org.dom4j.rule.Action;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonPollType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.whatsplan.bot.spring.BotSceneMode;
import ru.whatsplan.bot.spring.annotation.*;

import java.util.List;

@BotController
public class RemindBotController {

    /*
    Команды помеченные как @RequiredCommand автоматически появляются как кнопки
     */

    /*
    user > [Напомнить](/remind) Напомни сесть за домашний проект
    или
    user > [Напомнить](/remind)
    bot  > Что тебе напонить?
    user > Напомни сесть за домашний проект
    ----- next scene step
    bot  > Когда напомнить?
    user > 22.06.2020 в 20:00
    bot  > Напомнить: Напомни сесть за домашний проект
           Время: 22.06.2020 в 20:00
    bot  > [Сохранить](/save) [Отменить](/cancel)
    user > [Сохранить](/save)
    ---- Идеальная реализация
    user > Поставь следующей задачей
    bot  > У вас нет текущих задач
    user > Сейчас
    bot  > Напомнить: Напомни сесть за домашний проект
           Время: 22.06.2020 в 20:00
    bot  > [Сохранить](/save) [Отменить](/cancel)
    user > [Сохранить](/save)
     */

    private String text;
    private String time;
    private Integer lastMessageId;

    @Scene(value = "/remind", patterns = "([Нн]апоминание)", next = "/text", mode = BotSceneMode.START)
    public SendMessage startScene(Update update) {
        this.lastMessageId = update.getMessage().getMessageId();

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(new ReplyKeyboardMarkup(List.of(
                        new KeyboardRow() {{ add(new KeyboardButton("Исправить ошибки проекта")); }},
                        new KeyboardRow() {{ add(new KeyboardButton("Сделать что-нибудь")); }}
                )))
                .setText("Что тебе напомнить? /text или напишите что напомнить");
    }

    @Scene(value = "/text", patterns = "(.?)", next = "/time")
    public SendMessage text(Update update) {
        this.text = update.getMessage().getText().replace("/text", "");

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(new ReplyKeyboardMarkup(List.of(
                        new KeyboardRow() {{ add(new KeyboardButton("Вечером")); }},
                        new KeyboardRow() {{ add(new KeyboardButton("Завтра")); }}
                )))
                .setText("Когда напомнить? /time или время");
    }

    @Scene(value = "/time", patterns = "(.?)", next = {"/save", "/cancel"})
    public SendMessage time(Update update) {
        this.time = update.getMessage().getText().replace("/time", "");

        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(new KeyboardButton("Сохранить"));
        keyboardButtons.add(new KeyboardButton("Отменить"));

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(keyboardButtons));
        // Parse time
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(replyKeyboardMarkup)
                .setText(String.format("Напомнить %s %s. /save или /cancel", time, text));
    }

    @Scene(value = "/save", patterns = {"([Сс]охранить)"}, mode = BotSceneMode.CONTROL)
    public SendMessage save(Update update) {
        // TODO: checkRequireCommands and save
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(new ReplyKeyboardRemove())
                .setText("Напоминание сохранено!");
    }

    @Scene(value = "/cancel", patterns = {"([Оо]тмена)", "([Оо]тменить)"}, mode = BotSceneMode.CONTROL)
    public SendMessage cancel(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setReplyMarkup(new ReplyKeyboardRemove())
                .setText("Напоминание удалено!");
    }
}
