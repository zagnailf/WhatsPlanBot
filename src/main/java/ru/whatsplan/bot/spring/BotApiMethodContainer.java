package ru.whatsplan.bot.spring;

import lombok.extern.slf4j.Slf4j;
import ru.whatsplan.bot.spring.bot.BotScript;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BotApiMethodContainer {

    /**
     * Хранит ссылку на последнюю команду из сценария бота
     *
     * Пользователь выполнил команду /start
     * Ему будут доступны команды из основного меню, и он не сможет выполнить команды из не основного меню
     * То есть пользователь сможет выполнить /remind, /settings, /stop, помеченные аннотацией @StartScript
     */
    private final Map<Integer, BotScript> userScriptMap;

    public static BotApiMethodContainer getInstance() {
        return Holder.instance;
    }

    private BotApiMethodContainer() {
        userScriptMap = new HashMap<>();
    }

    public BotApiMethodController getBotApiMethodController(Integer userId, String message) {
        /*if (message != null && message.startsWith("/start")) {
            StartBotScript startScript = new StartBotScript();
            userScriptMap.put(userId, startScript);
            return startScript.getBotApiMethodController();
        }

        if (!userScriptMap.containsKey(userId)) {
            userScriptMap.put(userId, new StartBotScript());
        }

        Script script = userScriptMap.get(userId);
        Set<String> availableCommands = script.getAvailableCommands();
        if (!messageStartWithAnyCommand(message, availableCommands)) {
            // Может быть так нужно парсить некоторый текст
            if (script.matchingTextForNextCommands(message))
                throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", message));

        }
        Script nextScript = script.getNextScript(message);

        if (nextScript == null) throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", message));

        userScriptMap.put(userId, nextScript);

        return nextScript.getBotApiMethodController();*/
        return null;
    }

    private boolean messageStartWithAnyCommand(String message, Set<String> availableCommands) {
        if (message == null) return false;

        return availableCommands.stream()
                .anyMatch(message::startsWith);
    }

    private static class Holder {
        final static BotApiMethodContainer instance = new BotApiMethodContainer();
    }
}
