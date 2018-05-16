package com.logdyn;

public interface UndoableExecutor<C extends UndoableCommand> extends Executor<C>  {
    void unexecute(final C command) throws Exception;

    /**
     * Default implementation calls execute()
     * Can be overriden to implement specific behaviour
     * @param command
     * @throws Exception
     */
    default void reexecute(final C command) throws Exception {
        execute(command);
    }
}