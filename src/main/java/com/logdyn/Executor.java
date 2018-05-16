package com.logdyn;

public interface Executor<C extends Command> {
    void execute(final C command) throws Exception;
}