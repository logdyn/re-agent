package com.logdyn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Controls the delegation of commands to their respective executor classes. Provides the ability to listen for the execution of commands.
 */
public class CommandDelegator{

    private static final CommandDelegator INSTANCE = new CommandDelegator();

    private final Map<Class<?>, Executor<?>> executors = new TreeMap<>(new ClassHierarchyComparator());
    private final ListIterator<Command> commands = new LinkedList<Command>().listIterator();

    private CommandDelegator() {}

    public static CommandDelegator getINSTANCE() {
        return INSTANCE;
    }

    private final List<PropertyChangeListener> listeners = new LinkedList<>();

    private String lastCommandStatus;

    /**
     * Subscribes an executor to listen for and execute any commands that are an instance of the specified class, or any sub-classes
     * @param executor The executor for the command or sub-classes of the command
     * @param clazz The class of the command
     * @param <C> The type of the command the executor will handle
     * @return Returns true if the executor is successfully subscribed, returns false if the an executor of the class or a parent class is already subscribed
     */
    public <C extends Command> boolean subscribe(final Executor<C> executor, final Class <C> clazz) {

        //prevent duplicate subscription to a command
        for (Class<?> subbedClass: executors.keySet()) {
            if (subbedClass.isAssignableFrom(clazz)) {
                return false;
            }
        }

        executors.put(clazz, executor);
        return true;
    }

    /**
     * Unsubscribes the executor from the delegator
     * @param executor The executor to unsubscribe
     * @param <C> The type of the command the executor handles
     * @return Returns true if the executor is found and removed, returns false if it is not found
     */
    public <C extends Command> boolean unsubscribe(final Executor<C> executor) {
        return executors.entrySet().removeIf((e) -> e.getValue().equals(executor));
    }

    /**
     * Gets the most generic executor for the given command
     * @param command The command for the executor to handle
     * @return Returns the most generic executor for the given command, returns null if no suitable executor can be found
     */
    private Executor getExecutor(final Command command) {
        for (final Map.Entry<Class<?>, Executor<?>> entry : executors.entrySet()) {
            if (entry.getKey().isAssignableFrom(command.getClass())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Publishes command to the most generic subscribed executor. Always records for undo, see {@link #publish(Command, boolean)} <br/>
     * @param command The command to execute
     * @return false if no executor is subscribed.
     * @throws ExecutionException if the command does not execute successfully
     */
    public synchronized boolean publish(final Command command) throws ExecutionException {
        return publish(command, true);
    }

    /**
     * Publishes command to the most generic subscribed executor for that command, with the option of recording for undo <br/>
     * @param command The command to execute
     * @param record whether or not to add the command to the stack, enabling undo/redo
     * @return Returns true if the command is executed, returns false if no executor is subscribed.
     * @throws ExecutionException if the command does not execute successfully
     */
    public synchronized boolean publish(final Command command, final boolean record) throws ExecutionException {
        final Executor executor = getExecutor(command);

        if (executor == null) {
            return false;
        }

        //remove any redoable commands in front of published command
        //i.e. can't publish, undo, publish, then redo the first publish
        if (record)
        {
            this.clearRedoHistory();
        }


        //If the command is not undoable, clear all previous history
        if (!(command instanceof UndoableCommand) && record) {
            this.clearUndoHistory();
        }

        try {
            //Unchecked call to execute()
            //doing this because can't determine type until runtime, will be correct
            //noinspection unchecked
            executor.execute(command);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

        if (record) {
            commands.add(command);

            final String newCommandStatus = "Do " + command.getName();
            notifyListeners(lastCommandStatus, newCommandStatus);
            lastCommandStatus = newCommandStatus;

            System.out.println(newCommandStatus);
        }

        return true;
    }

    /**
     * Call unexecute command on executor on previous command
     * Only works if both command and executor are undoable
     * @return Returns true if the command undoes successfully, returns false if no executor is subscribed
     * @throws ExecutionException if undo does not execute successfully
     */
    public synchronized boolean undo() throws ExecutionException {
        if (commands.hasPrevious()) {
            final Command command = commands.previous();

            try {
                if (command instanceof UndoableCommand) {
                    Executor executor = getExecutor(command);
                    if (executor instanceof UndoableExecutor) {
                        final UndoableExecutor undoableExecutor = (UndoableExecutor) executor;
                        //Unchecked call to unexecute()
                        //doing this because can't determine type until runtime, will be correct
                        //noinspection unchecked
                        undoableExecutor.unexecute((UndoableCommand) command);

                        final String newCommandStatus = "Undo " + command.getName();
                        notifyListeners(lastCommandStatus, newCommandStatus);
                        lastCommandStatus = newCommandStatus;

                        System.out.println(newCommandStatus);
                        return true;
                    }
                }
                commands.next();
            } catch (final Exception e) {
                //Undo rolling history back
                this.clearUndoHistory();
                throw new ExecutionException(e);
            }
        }

        return false;
    }

    /**
     * Call reexecute command on executor on previous command
     * Only works if both command and executor are redoable
     * @return Returns true if command redoes successfuly, returns false if no executor is subscribed
     * @throws ExecutionException if undo does not execute successfully
     */
    public synchronized boolean redo() throws ExecutionException {
        if (commands.hasNext()) {
            final Command command = commands.next();

            try {
                if (command instanceof UndoableCommand) {
                    final Executor executor = getExecutor(command);
                    if (executor instanceof UndoableExecutor) {
                        final UndoableExecutor undoableExecutor = (UndoableExecutor) executor;
                        //Unchecked call to unexecute()
                        //doing this because can't determine type until runtime, will be correct
                        //noinspection unchecked
                        undoableExecutor.reexecute((UndoableCommand) command);

                        final String newCommandStatus = "Redo " + command.getName();
                        notifyListeners(lastCommandStatus, newCommandStatus);
                        lastCommandStatus = newCommandStatus;

                        System.out.println(newCommandStatus);
                        return true;
                    }
                }
                commands.previous();
            } catch (Exception e) {
                //Undo rolling history back
                this.clearRedoHistory();
                throw new ExecutionException(e);
            }
        }

        return false;
    }

    private void clearUndoHistory()
    {
        while (commands.hasPrevious()) {
            commands.previous();
            commands.remove();
        }
    }

    private void clearRedoHistory()
    {
        while (commands.hasNext()) {
            commands.next();
            commands.remove();
        }
    }

    /**
     * @return true if there is a command that can be undone
     */
    public boolean canUndo() {
        //Check if there is a previous command, that is undoable
        if (commands.hasPrevious()) {
            final Command previous = commands.previous();
            commands.next(); //revert position of ListIterator
            return previous instanceof UndoableCommand;
        }
        return false;
    }

    /**
     * @return true if there is a command that can be redone
     */
    public boolean canRedo() {
        if (commands.hasNext()) {
            final Command next = commands.next();
            commands.previous(); //Revert position of ListIterator
            return next instanceof UndoableCommand;
        }
        return false;
    }

    /**
     * @return the name of the command that would be the result of calling the {@link #undo()} method, returns null if no command can be undone
     */
    public String getUndoName() {
        if (canUndo()) {
            commands.previous();
            return commands.next().getName();
        }

        return null;
    }

    /**
     * @return the name of the command that would be the result of calling the {@link #redo()} method, returns null if no command can be redone,
     */
    public String getRedoName() {
        if (canRedo()) {
            commands.next();
            return commands.previous().getName();
        }

        return null;
    }

    public String getLastCommandStatus() {
        return lastCommandStatus;
    }

    /**
     * Registers an invalidation listener, listener is invalidated when a command is executed, unexecuted, or reexecuted
     * @param listener The listener to register
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean addListener(final PropertyChangeListener listener) {
        return this.listeners.add(listener);
    }

    public boolean removeListener(final PropertyChangeListener listener) {
        return this.listeners.remove(listener);
    }

    private void notifyListeners(final String oldValue, final String newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, "lastCommandStatus", oldValue, newValue);
        for (final PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }
}