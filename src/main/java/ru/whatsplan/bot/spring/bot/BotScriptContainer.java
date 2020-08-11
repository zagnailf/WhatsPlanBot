package ru.whatsplan.bot.spring.bot;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.whatsplan.bot.exception.CommandIsNotAvailableException;
import ru.whatsplan.bot.spring.BotApiMethodController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BotScriptContainer {

    private final Map<Integer, BotScript> lastUserScript;

    @Setter @Getter
    private BotScript script;

    public BotApiMethodController getBotApiMethodController(Integer userId, String message) {
        if (message != null && message.startsWith("/start")) {
            lastUserScript.put(userId, script);
            return script.getBotApiMethodController();
        }

        if (!lastUserScript.containsKey(userId)) {
            lastUserScript.put(userId, script);
        }

        BotScript currentScript = lastUserScript.get(userId);
        Set<String> availableCommands = currentScript.getAvailableCommands();
        if (!messageStartWithAnyCommand(message, availableCommands)) {
            if (currentScript.match(message))
                throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", message));
        }

        BotScript nextScript = currentScript.getNextScript(message);
        if (nextScript == null) throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", message));

        lastUserScript.put(userId, nextScript);

        return nextScript.getBotApiMethodController();
    }

    private boolean messageStartWithAnyCommand(String message, Set<String> availableCommands) {
        if (message == null) return false;

        return availableCommands.stream()
                .anyMatch(message::startsWith);
    }

    public static BotScriptContainer getInstance() {
        return Holder.instance;
    }

    private BotScriptContainer() {
        lastUserScript = new HashMap<>();
    }

    private static class Holder {
        final static BotScriptContainer instance = new BotScriptContainer();
    }
}
