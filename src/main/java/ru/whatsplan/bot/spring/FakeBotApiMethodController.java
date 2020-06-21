package ru.whatsplan.bot.spring;

import org.telegram.telegrambots.meta.api.objects.Update;

public class FakeBotApiMethodController extends BotApiMethodController {
    public FakeBotApiMethodController() {
        super(null, null);
    }

    @Override
    public boolean successUpdatePredicate(Update update) {
        return true;
    }
}
