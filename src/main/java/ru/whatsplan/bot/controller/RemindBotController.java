package ru.whatsplan.bot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.whatsplan.bot.spring.BotSceneMode;
import ru.whatsplan.bot.spring.annotation.*;

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

    @Scene(value = "/remind", next = "/text", mode = BotSceneMode.START)
    public SendMessage startScene(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Что тебе напомнить? /text или напишите что напомнить");
    }

    @Scene(value = "/text", patterns = "(.?)", next = "/time")
    public SendMessage text(Update update) {
        this.text = update.getMessage().getText().replace("/text", "");

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Когда напомнить? /time или время");
    }

    @Scene(value = "/time", patterns = "(.?)", next = {"/save", "/cancel"})
    public SendMessage time(Update update) {
        this.time = update.getMessage().getText().replace("/time", "");
        // Parse time
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(String.format("Напомнить %s %s. /save или /cancel", time, text));
    }

    @Scene(value = "/save", patterns = {"([Сс]охранить)"}, mode = BotSceneMode.CONTROL)
    public SendMessage save(Update update) {
        // TODO: checkRequireCommands and save
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Напоминание сохранено!");
    }

    @Scene(value = "/cancel", patterns = {"([Оо]тмена)", "([Оо]тменить)"}, mode = BotSceneMode.CONTROL)
    public SendMessage cancel(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Напоминание удалено!");
    }
}
