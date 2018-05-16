package controllers;

import com.logdyn.Command;
import com.logdyn.CommandDelegator;
import org.junit.jupiter.api.Test;
import resources.CommandDelegatorTestUtility;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("Duplicates")
class CommandDelegatorTest {

    @Test
    void subscribe() {

        CommandDelegatorTestUtility.SubscribeTestExecutor executor = new CommandDelegatorTestUtility.SubscribeTestExecutor();
        Command command = new CommandDelegatorTestUtility.SubscribeCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, CommandDelegatorTestUtility.SubscribeCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void unsubscribe() {
        CommandDelegatorTestUtility.UnsubscribeTestExecutor executor = new CommandDelegatorTestUtility.UnsubscribeTestExecutor();
        Command command = new CommandDelegatorTestUtility.UnsubscribeCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, CommandDelegatorTestUtility.UnsubscribeCommand.class));
        assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        try {
            assertFalse(CommandDelegator.getINSTANCE().publish(command));
            assertFalse(executor.executed);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void publish() {
        CommandDelegatorTestUtility.PublishTestExecutor executor = new CommandDelegatorTestUtility.PublishTestExecutor();
        Command command = new CommandDelegatorTestUtility.PublishCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, CommandDelegatorTestUtility.PublishCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void undo() {
        CommandDelegatorTestUtility.UndoTestExecutor executor = new CommandDelegatorTestUtility.UndoTestExecutor();
        Command command = new CommandDelegatorTestUtility.UndoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, CommandDelegatorTestUtility.UndoCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo
            assertTrue(CommandDelegator.getINSTANCE().undo());
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
            //Redo
            assertTrue(CommandDelegator.getINSTANCE().redo());
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo-redo
            assertTrue(CommandDelegator.getINSTANCE().undo());
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void redo() {
        CommandDelegatorTestUtility.RedoTestExecutor executor = new CommandDelegatorTestUtility.RedoTestExecutor();
        Command command = new CommandDelegatorTestUtility.RedoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, CommandDelegatorTestUtility.RedoCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo
            assertTrue(CommandDelegator.getINSTANCE().undo());
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
            //Redo
            assertTrue(CommandDelegator.getINSTANCE().redo());
            assertTrue(executor.executed);
            assertTrue(executor.redone);
        } catch (Exception e) {
            fail(e);
        }
    }
}