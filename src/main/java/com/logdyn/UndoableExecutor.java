package com.logdyn;

public interface UndoableExecutor<C extends UndoableCommand> extends Executor<C>  {
    void unexecute(final C command) throws Exception;

    /**
     * Default implementation calls execute()
     * Can be overriden to implement specific behaviour
     * @param command The command to reexecute
     * @throws Exception Any exception encountered during execution
     */
    default void reexecute(final C command) throws Exception {
        execute(command);
    }
}