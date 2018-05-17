package controllers;

import com.logdyn.Command;
import com.logdyn.CommandDelegator;
import com.logdyn.NoSuchExecutorException;
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
            CommandDelegator.getINSTANCE().publish(command);
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }

    @Test
    void unsubscribe() {
        UnsubscribeTestExecutor executor = new UnsubscribeTestExecutor();
        Command command = new UnsubscribeCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, UnsubscribeCommand.class));
        assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        try {
            assertThrows(NoSuchExecutorException.class, () -> CommandDelegator.getINSTANCE().publish(command));
            assertFalse(executor.executed);
        }catch(Exception e) {
            fail(e);
        } finally {
            assertFalse(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }

    }

    @Test
    void publish() {
        PublishTestExecutor executor = new PublishTestExecutor();
        Command command = new PublishCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, PublishCommand.class));
        try {
            CommandDelegator.getINSTANCE().publish(command);
            assertTrue(executor.executed);
        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }

    @Test
    void undo() {
        UndoTestExecutor executor = new UndoTestExecutor();
        Command command = new UndoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, UndoCommand.class));
        try {
            CommandDelegator.getINSTANCE().publish(command);
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo
            CommandDelegator.getINSTANCE().undo();
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
            //Redo
            CommandDelegator.getINSTANCE().redo();
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo-redo
            CommandDelegator.getINSTANCE().undo();
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }

    @Test
    void multipleUndo() {

        int count = 3;
        MultipleUndoTestExecutor executor = new MultipleUndoTestExecutor();
        Command command = new UndoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, UndoCommand.class));

        try {
            for (int i = 0; i < count; i++) {
                assertDoesNotThrow(
                        () -> CommandDelegator.getINSTANCE().publish(command),
                        "Published command count: " + i);
            }

            assertEquals(count, executor.executedCount);

            CommandDelegator.getINSTANCE().undo(count);

            assertEquals(count, executor.unexecutedCount);

        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }

    @Test
    void redo() {
        RedoTestExecutor executor = new RedoTestExecutor();
        Command command = new RedoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, RedoCommand.class));
        try {
            CommandDelegator.getINSTANCE().publish(command);
            assertTrue(executor.executed);
            assertTrue(executor.redone);
            //Undo
            CommandDelegator.getINSTANCE().undo();
            assertTrue(executor.unexecuted);
            assertFalse(executor.redone);
            //Redo
            CommandDelegator.getINSTANCE().redo();
            assertTrue(executor.executed);
            assertTrue(executor.redone);
        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }

    @Test
    void multipleRedo() {

        int count = 3;
        MultipleRedoTestExecutor executor = new MultipleRedoTestExecutor();
        Command command = new RedoCommand();

        assertTrue(CommandDelegator.getINSTANCE().subscribe(executor, RedoCommand.class));

        try {

            for (int i = 0; i < count; i++) {
                assertDoesNotThrow(
                        () -> CommandDelegator.getINSTANCE().publish(command),
                        "Published command count: " + i);
            }
            assertEquals(count, executor.executedCount);

            CommandDelegator.getINSTANCE().undo(count);

            assertEquals(count, executor.unexecutedCount);

            CommandDelegator.getINSTANCE().redo(count);

            assertEquals(count, executor.reexecutedCount);

        } catch (Exception e) {
            fail(e);
        } finally {
            assertTrue(CommandDelegator.getINSTANCE().unsubscribe(executor));
        }
    }
}