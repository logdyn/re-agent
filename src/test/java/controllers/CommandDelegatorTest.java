package controllers;

import com.logdyn.Command;
import com.logdyn.CommandDelegator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static resources.CommandDelegatorTestUtility.*;


@SuppressWarnings("Duplicates")
class CommandDelegatorTest {

    @Test
    void subscribe() {

        SubscribeTestExecutor executor = new SubscribeTestExecutor();
        Command command = new SubscribeCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, SubscribeCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void unsubscribe() {
        UnsubscribeTestExecutor executor = new UnsubscribeTestExecutor();
        Command command = new UnsubscribeCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, UnsubscribeCommand.class));
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
        PublishTestExecutor executor = new PublishTestExecutor();
        Command command = new PublishCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, PublishCommand.class));
        try {
            assertTrue(CommandDelegator.getINSTANCE().publish(command));
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void undo() {
        UndoTestExecutor executor = new UndoTestExecutor();
        Command command = new UndoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, UndoCommand.class));
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
        RedoTestExecutor executor = new RedoTestExecutor();
        Command command = new RedoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, RedoCommand.class));
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