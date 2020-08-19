package ru.whatsplan.bot.spring.bot;

import ru.whatsplan.bot.spring.BotApiMethodController;
import ru.whatsplan.bot.spring.BotSceneMode;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotScript {

    private final BotSceneMode mode;
    private final String command;
    private final BotApiMethodController controller;
    private final List<Pattern> patterns;
    private final Set<BotScript> nextScripts;
    private final Set<BotScript> controlScripts;

    public BotScript(BotSceneMode mode, String command, BotApiMethodController controller) {
        this(mode, command, controller, new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    public BotScript(BotSceneMode mode, String command, BotApiMethodController controller, Set<BotScript> nextScripts) {
        this(mode, command, controller, new ArrayList<>(), nextScripts, new HashSet<>());
    }

    public BotScript(BotSceneMode mode, String command, BotApiMethodController controller, Set<BotScript> nextScripts, Set<BotScript> controls) {
        this(mode, command, controller, new ArrayList<>(), nextScripts, controls);
    }

    public BotScript(BotSceneMode mode, String command, BotApiMethodController controller, List<Pattern> patterns, Set<BotScript> nextScripts) {
        this(mode, command, controller, patterns, nextScripts, new HashSet<>());
    }

    public BotScript(BotSceneMode mode, String command, BotApiMethodController controller,
                     List<Pattern> patterns, Set<BotScript> nextScripts, Set<BotScript> controls) {
        this.mode = mode;
        this.command = command;
        this.controller = controller;
        this.patterns = patterns;
        this.nextScripts = nextScripts;
        this.controlScripts = controls;
    }

    public BotApiMethodController getBotApiMethodController() {
        return controller;
    }

    public Set<String> getAvailableCommands() {
        return nextScripts.stream()
                .map(BotScript::command)
                .collect(Collectors.toSet());
    }

    public Set<BotScript> getAvailableScripts() {
        return nextScripts;
    }

    public BotScript getScriptFromCommand(String command) {
        return nextScripts.stream()
                .filter(script -> script.command.equals(command))
                .findFirst().orElse(null);
    }

    public BotScript getControlScriptFromCommand(String command) {
        return controlScripts.stream()
                .filter(script -> script.command.equals(command))
                .findFirst().orElse(null);
    }

    public BotScript getScriptFromMessage(String message) {
        return nextScripts.stream()
                .filter(script -> script.isPatternMatching(message))
                .findFirst().orElse(null);
    }

    public BotScript getControlScriptFromMessage(String message) {
        return controlScripts.stream()
                .filter(script -> script.isPatternMatching(message))
                .findFirst().orElse(null);
    }

    public boolean isPatternMatching(String message) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(message).find());
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

    public BotSceneMode mode() {
        return mode;
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
