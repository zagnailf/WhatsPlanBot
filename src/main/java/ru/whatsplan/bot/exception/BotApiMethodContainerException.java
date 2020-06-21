package ru.whatsplan.bot.exception;

public class BotApiMethodContainerException extends RuntimeException {
    public BotApiMethodContainerException(String message) {
        super(message);
    }

    public BotApiMethodContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
