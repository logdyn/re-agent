package com.logdyn.staging;

import com.logdyn.Command;
import com.logdyn.ExecutionException;
import com.logdyn.NoSuchExecutorException;

public class Executor <C extends Command> {

    private ExecutionStrategy<C> doStrat, undoStrat, redoStrat;

    public boolean hasExecute() {
        return doStrat == null;
    }

    public boolean hasUnexecute() {
        return undoStrat == null;
    }

    public boolean hasReexecute() {
        return redoStrat == null;
    }

    public Executor<C> setExecute(final ExecutionStrategy<C> doStrat) {
        this.doStrat = doStrat;
        return this;
    }

    public Executor<C> setUnexecute(final ExecutionStrategy<C> undoStrat) {
        this.undoStrat = undoStrat;
        return this;
    }

    public Executor<C> setReexecute(final ExecutionStrategy<C> redoStrat) {
        this.redoStrat = redoStrat;
        return this;
    }

    public void execute (C command) throws NoSuchExecutorException, ExecutionException {
        if (doStrat == null) {
            throw new NoSuchExecutorException(command);
        }

        doStrat.execute(command);
    }

    public void unexecute (C command) throws NoSuchExecutorException, ExecutionException {
        if (undoStrat == null) {
            throw new NoSuchExecutorException(command);
        }

        undoStrat.execute(command);
    }

    public void reexecute (C command) throws NoSuchExecutorException, ExecutionException {
        if (redoStrat == null) {
            throw new NoSuchExecutorException(command);
        }

        redoStrat.execute(command);
    }
}
