package ru.whatsplan.bot.spring.bot;

import ru.whatsplan.bot.exception.CommandIsNotAvailableException;
import ru.whatsplan.bot.spring.BotApiMethodController;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotScript {

    private final String command;
    private final BotApiMethodController controller;
    private final List<Pattern> patterns;
    private final Set<BotScript> nextScripts;

    public BotScript(String command, BotApiMethodController controller) {
        this(command, controller, new ArrayList<>(), new HashSet<>());
    }

    public BotScript(String command, BotApiMethodController controller, Set<BotScript> nextScripts) {
        this(command, controller, new ArrayList<>(), nextScripts);
    }

    public BotScript(String command, BotApiMethodController controller, List<Pattern> patterns, Set<BotScript> nextScripts) {
        this.command = command;
        this.controller = controller;
        this.patterns = patterns;
        this.nextScripts = nextScripts;
    }

    public BotApiMethodController getBotApiMethodController() {
        return controller;
    }

    public Set<String> getAvailableCommands() {
        return nextScripts.stream()
                .map(BotScript::command)
                .collect(Collectors.toSet());
    }

    public BotScript getNextScript(String message) {
        Optional<BotScript> availableScript = nextScripts.stream()
                .filter(script -> message.startsWith(script.command()))
                .findFirst();

        if (availableScript.isPresent()) return availableScript.get();

        Optional<BotScript> availableScriptFromMessage = nextScripts.stream()
                .filter(script -> script.match(message))
                .findFirst();

        return availableScriptFromMessage.orElse(null);
    }

    public boolean match(String message) {
        return patterns.stream()
                .anyMatch(pattern -> pattern.matcher(message).find());
    }

    public boolean isCommand(String strCommand) {
        return command.equals(strCommand);
    }

    public String command() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotScript botScript = (BotScript) o;
        return Objects.equals(command, botScript.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }
}
