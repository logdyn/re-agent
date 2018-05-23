package com.logdyn.staging;

import com.logdyn.Command;
import com.logdyn.ExecutionException;

@FunctionalInterface
public interface ExecutionStrategy <C extends Command> {
    void execute(C command) throws ExecutionException;
}
