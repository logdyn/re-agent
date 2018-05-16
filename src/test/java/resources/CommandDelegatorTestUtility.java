package resources;

import com.logdyn.Command;
import com.logdyn.Executor;
import com.logdyn.UndoableCommand;
import com.logdyn.UndoableExecutor;

public class CommandDelegatorTestUtility {

    public static class SubscribeCommand implements Command {

        @Override
        public String getName() {
            return null;
        }
    }

    public static class UnsubscribeCommand implements Command {

        @Override
        public String getName() {
            return null;
        }
    }

    public static class PublishCommand implements Command {

        @Override
        public String getName() {
            return null;
        }
    }

    public static class UndoCommand implements UndoableCommand {

        @Override
        public String getName() {
            return null;
        }
    }

    public static class RedoCommand implements UndoableCommand {

        @Override
        public String getName() {
            return null;
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
}
