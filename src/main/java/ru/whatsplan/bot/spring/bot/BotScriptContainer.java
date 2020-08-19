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

    @Getter
    private BotScript script;
    private Map<String, BotScript> botStartScenes;

    public BotApiMethodController getBotApiMethodController(Integer userId, String message) {

        String command = getCommandFromMessage(message);

        if (command != null && command.startsWith("/start")) {
            lastUserScript.put(userId, script);
            return script.getBotApiMethodController();
        }

        if (!lastUserScript.containsKey(userId)) {
            lastUserScript.put(userId, script);
        }

        BotScript currentScript = lastUserScript.get(userId);

        /*if (!messageStartWithAnyCommand(message, availableCommands)) {
            if (!messageMatchWithAnyCommand(message, currentScript.getAvailableScripts()))
                throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", command));
        }*/

        BotScript nextScript = currentScript.getScriptFromCommand(command);
        nextScript = (nextScript == null && botStartScenes.containsKey(command)) ? botStartScenes.get(command) : nextScript;
        nextScript = (nextScript == null) ? currentScript.getControlScriptFromCommand(command) : nextScript;
        nextScript = (nextScript == null) ? currentScript.getControlScriptFromMessage(message) : nextScript;
        nextScript = (nextScript == null) ? currentScript.getScriptFromMessage(message) : nextScript;
        if (nextScript == null) throw new CommandIsNotAvailableException(String.format("Команда \"%s\" не доступна для выполнения", command));

        lastUserScript.put(userId, nextScript);

        return nextScript.getBotApiMethodController();
    }

    private String getCommandFromMessage(String message) {
        if (message == null) return null;

        if (message.startsWith("/")) {
            int commandEndIndex = message.indexOf(" ");
            return (commandEndIndex > 0) ? message.substring(0, commandEndIndex) : message;
        }
        return message;
    }

    public void setScript(BotScript script, Map<String, BotScript> botStartSceneCommandMap) {
        this.script = script;
        this.botStartScenes = botStartSceneCommandMap;
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
