package ru.whatsplan.bot.spring;

import org.telegram.telegrambots.meta.api.objects.Update;

public class SelectHandle {
    private static BotApiMethodContainer container = BotApiMethodContainer.getInstance();

    public static BotApiMethodController getHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText())
            return getTextMessageHandle(update);

        if (update.hasCallbackQuery())
            return getCallbackQueryHandle(update);

        return new FakeBotApiMethodController();
    }

    private static BotApiMethodController getCallbackQueryHandle(Update update) {
        String path = update.getCallbackQuery().getData().split("/")[1].trim();
        return container.getBotApiMethodController(path);
    }

    private static BotApiMethodController getTextMessageHandle(Update update) {
        String path = update.getMessage().getText().split(" ")[0].trim();
        BotApiMethodController controller = container.getBotApiMethodController(path);
        if (controller == null) controller = container.getBotApiMethodController("");
        return controller;
    }
}
