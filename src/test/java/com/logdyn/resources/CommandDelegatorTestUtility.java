package com.logdyn.resources;

import com.logdyn.Command;
import com.logdyn.Executor;
import com.logdyn.UndoableCommand;
import com.logdyn.UndoableExecutor;

public class CommandDelegatorTestUtility {

    public static class SubscribeCommand implements Command {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    public static class UnsubscribeCommand implements Command {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    public static class PublishCommand implements Command {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    public static class UndoCommand implements UndoableCommand {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    public static class RedoCommand implements UndoableCommand {

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    public static class NamedCommand implements UndoableCommand {

        private String name;

        public NamedCommand(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class GenericUndoableExecutor implements  UndoableExecutor<UndoableCommand> {

        @Override
        public void unexecute(UndoableCommand command) throws Exception {
            //NOOP
        }

        @Override
        public void execute(UndoableCommand command) throws Exception {
            //NOOP
        }
    }

    public static class SubscribeTestExecutor implements Executor<SubscribeCommand> {

        public boolean executed = false;

        @Override
        public void execute(SubscribeCommand command) throws Exception {
            executed = true;
        }
    }

    public static class UnsubscribeTestExecutor implements Executor<UnsubscribeCommand> {

        public boolean executed = false;

        @Override
        public void execute(UnsubscribeCommand command) throws Exception {
            executed = true;
        }
    }

    public static class PublishTestExecutor implements Executor<PublishCommand> {

        public boolean executed = false;

        @Override
        public void execute(PublishCommand command) throws Exception {
            executed = true;
        }
    }

    public static class UndoTestExecutor implements UndoableExecutor<UndoCommand> {

        public boolean executed = false;
        public boolean unexecuted = false;
        public boolean redone = false;

        @Override
        public void execute(UndoCommand command) throws Exception {
            executed = true;
            redone = true;
        }

        @Override
        public void unexecute(UndoCommand command) throws Exception {
            unexecuted = true;
            redone = false;
        }
    }

    public static class MultipleUndoTestExecutor implements UndoableExecutor<UndoCommand> {

        public int executedCount = 0;
        public int unexecutedCount = 0;

        @Override
        public void execute(UndoCommand command) throws Exception {
            executedCount++;
        }

        @Override
        public void unexecute(UndoCommand command) throws Exception {
            unexecutedCount++;
        }
    }

    public static class RedoTestExecutor implements UndoableExecutor<RedoCommand> {

        public boolean executed = false;
        public boolean unexecuted = false;
        public boolean redone = false;

        @Override
        public void execute(RedoCommand command) throws Exception {
            executed = true;
            redone = true;
        }

        @Override
        public void unexecute(RedoCommand command) throws Exception {
            unexecuted = true;
            redone = false;
        }
    }

    public static class MultipleRedoTestExecutor implements UndoableExecutor<RedoCommand> {

        public int executedCount = 0;
        public int unexecutedCount = 0;
        public int reexecutedCount = 0;

        @Override
        public void execute(RedoCommand command) throws Exception {
            executedCount++;
        }

        @Override
        public void unexecute(RedoCommand command) throws Exception {
            unexecutedCount++;
        }

        @Override
        public void reexecute(RedoCommand command) throws Exception {
            reexecutedCount++;
        }
    }
}
