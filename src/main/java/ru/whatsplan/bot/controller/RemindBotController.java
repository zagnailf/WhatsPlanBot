package ru.whatsplan.bot.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    @SceneStep(value = "/remind", next = "/text")
    public SendMessage startScene(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Что тебе напомнить? /text");
    }

    @SceneStep(value = "/text", next = "/time")
    public SendMessage text(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Когда напомнить? /time");
    }

    @SceneStep(value = "/time", next = {"/save", "/cancel"})
    public SendMessage time(Update update) {
        // Parse time
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Сохранить или отменить? /save или /cancel");
    }

    @SceneStep(value = "/save")
    public SendMessage save(Update update) {
        // TODO: checkRequireCommands and save
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Напоминание сохранено!");
    }

    @SceneStep(value = "/cancel")
    public SendMessage cancel(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Напоминание удалено!");
    }
}
