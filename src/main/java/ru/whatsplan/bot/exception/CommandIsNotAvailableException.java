package ru.whatsplan.bot.exception;

import java.util.function.Supplier;

public class CommandIsNotAvailableException extends RuntimeException {

    public CommandIsNotAvailableException(String message) {
        super(message);
    }

}
