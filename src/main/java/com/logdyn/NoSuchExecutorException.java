package com.logdyn;

public class NoSuchExecutorException extends RuntimeException {
    public NoSuchExecutorException(final Command command) {
        super(String.format("There is no subscribed Executor for the command '%s'", command.getName()));
    }
}
