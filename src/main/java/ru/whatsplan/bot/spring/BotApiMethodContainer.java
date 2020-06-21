package ru.whatsplan.bot.spring;

import lombok.extern.slf4j.Slf4j;
import ru.whatsplan.bot.exception.BotApiMethodContainerException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BotApiMethodContainer {
    private final Map<String, BotApiMethodController> controllerMap;

    public static BotApiMethodContainer getInstance() {
        return Holder.instance;
    }

    public void addBotController(String path, BotApiMethodController controller) {
        if (controllerMap.containsKey(path)) {
            throw new BotApiMethodContainerException(String.format("path %s already add", path));
        }
        log.trace("Add telegram bot controller for path: {}", path);
        controllerMap.put(path, controller);
    }

    public BotApiMethodController getBotApiMethodController(String path) {
        return controllerMap.get(path);
    }

    private BotApiMethodContainer() {
        controllerMap = new HashMap<>();
    }

    private static class Holder {
        final static BotApiMethodContainer instance = new BotApiMethodContainer();
    }
}
