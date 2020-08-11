package ru.whatsplan.bot.spring;

import java.util.Set;

public interface Scene {

    boolean hasChild();

    boolean hasNext();

    boolean containsScene(String command);

    boolean isCommand(String command);

    Set<Scene> next();

    void setRequired(boolean required);

    void setNext(Set<Scene> next);

    void addChild(Set<Scene> child);

    void addChild(Scene child);

    BotApiMethodController controller();

}
