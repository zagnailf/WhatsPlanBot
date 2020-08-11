package ru.whatsplan.bot.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BotScene implements Scene {

    private final String command;
    private final BotApiMethodController controller;
    private Set<Scene> next;

    private final List<Set<Scene>> childs;

    private boolean required;

    public BotScene(String command, BotApiMethodController controller) {
        this.command = command;
        this.controller = controller;

        this.childs = new ArrayList<>();
    }

    @Override
    public boolean hasChild() {
        return !childs.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public boolean containsScene(String command) {
        throw new RuntimeException("Не реализовано");
    }

    @Override
    public boolean isCommand(String command) {
        return this.command.equals(command);
    }

    @Override
    public Set<Scene> next() {
        return next;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public void setNext(Set<Scene> next) {
        this.next = next;
    }

    @Override
    public void addChild(Set<Scene> child) {
        this.childs.add(child);
    }

    @Override
    public void addChild(Scene child) {
        this.childs.add(Set.of(child));
    }

    @Override
    public BotApiMethodController controller() {
        return controller;
    }
}
