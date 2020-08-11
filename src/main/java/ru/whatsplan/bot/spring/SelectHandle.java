package ru.whatsplan.bot.spring;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.whatsplan.bot.spring.bot.BotScriptContainer;

import java.util.Set;

public class SelectHandle {
    //private static BotApiMethodContainer container = BotApiMethodContainer.getInstance();
    private static BotScriptContainer container = BotScriptContainer.getInstance();

    public static BotApiMethodController getHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText())
            return getTextMessageHandle(update);

        if (update.hasCallbackQuery())
            return getCallbackQueryHandle(update);

        return new FakeBotApiMethodController();
    }

    private static BotApiMethodController getCallbackQueryHandle(Update update) {
        String data = update.getCallbackQuery().getData();
        User from = update.getCallbackQuery().getFrom();
        return container.getBotApiMethodController(from.getId(), data);
    }

    private static BotApiMethodController getTextMessageHandle(Update update) {
        String message = update.getMessage().getText();
        User from = update.getMessage().getFrom();
        BotApiMethodController controller = container.getBotApiMethodController(from.getId(), message);
        if (controller == null) controller = container.getBotApiMethodController(from.getId(), "");
        return controller;
    }
}
