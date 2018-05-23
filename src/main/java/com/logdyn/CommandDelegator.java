package com.logdyn;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Controls the delegation of commands to their respective executor classes. Provides the ability to listen for the execution of commands.
 */
public class CommandDelegator{

    private static final CommandDelegator INSTANCE = new CommandDelegator();

    //private final Map<Class<?>, Executor<?>> executors = new TreeMap<>(new ClassHierarchyComparator());
    private final ExecutorTree<Command> executorTree = new ExecutorTree<>();
    private final ListIterator<Command> commands = new LinkedList<Command>().listIterator();
    private final SortedSet<ExecutionRecord> executionRecords = new TreeSet<>();

    private CommandDelegator() {}

    public static CommandDelegator getINSTANCE() {
        return INSTANCE;
    }

    private final List<PropertyChangeListener> listeners = new LinkedList<>();

    /**
     * Subscribes an executor to listen for and execute any commands that are an instance of the specified class, or any sub-classes
     * @param executor The executor for the command or sub-classes of the command
     * @param clazz The class of the command
     *
     * @return Returns true if the executor is successfully subscribed, returns false if the an executor of the class or a parent class is already subscribed
     */
    public <C extends Command> boolean subscribe(final Executor<? super C> executor, final Class <C> clazz) {

        Objects.requireNonNull(executor, "Executor cannot be null");
        Objects.requireNonNull(clazz, "Clazz cannot be null");

        //prevent duplicate subscription to a command
        /*for (Class<?> subbedClass: executors.keySet()) {
            if (subbedClass.isAssignableFrom(clazz)) {
                return false;
            }
        }*/
        executorTree.put((Class<Command>) clazz, (Executor<? super Command>) executor);
        //executors.put(clazz, executor);
        return true;
    }

    /**
     * Unsubscribes the executor from the delegator
     * @param executor The executor to unsubscribe
     * @param <C> The type of the command the executor handles
     * @return Returns true if the executor is found and removed, returns false if it is not found
     */
    public <C extends Command> boolean unsubscribe(final Executor<C> executor) {
        //return executors.entrySet().removeIf((e) -> e.getValue().equals(executor));
        return true; //TODO add remove method
    }

    /**
     * Gets the most generic executor for the given command
     * @param command The command for the executor to handle
     * @return Returns the most generic executor for the given command, returns null if no suitable executor can be found
     * @throws NoSuchExecutorException if there is no registered {@link Executor} for the given {@link Command}
     */
    private Executor getExecutor(final Command command) {
        Objects.requireNonNull(command, "command must be not null");
        /*for (final Map.Entry<Class<?>, Executor<?>> entry : executors.entrySet()) {
            if (entry.getKey().isAssignableFrom(command.getClass())) {
                return entry.getValue();
            }
        }*/
        return executorTree.get(command, true);
        //throw new NoSuchExecutorException(command);
    }

    /**
     * Publishes command to the most generic subscribed executor. Always records for undo, see {@link #publish(Command, boolean)}
     * @param command The command to execute
     * @throws ExecutionException if the command does not execute successfully
     * @throws NoSuchExecutorException if there is no registered {@link Executor} for the given {@link Command}
     */
    public synchronized void publish(final Command command) throws ExecutionException {
        this.publish(command, true);
    }

    /**
     * Publishes command to the most generic subscribed executor for that command, with the option of recording for undo
     * @param command The command to publish and execute
     * @param record whether or not to add the command to the stack, enabling undo/redo
     * @throws ExecutionException if the command does not execute successfully
     * @throws NoSuchExecutorException if there is no registered {@link Executor} for the given {@link Command}
     */
    public synchronized void publish(final Command command, final boolean record) throws ExecutionException {
        final Executor executor = getExecutor(command);

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
        }

        this.addExecutionRecord(new ExecutionRecord(command, ExecutionRecord.Operation.DO));
    }

    /**
     * Call unexecute command on executor on previous command
     * Only works if both command and executor are undoable
     * @throws NoSuchElementException if there is no {@link Command } to be undone
     * @throws ExecutionException if undo does not execute successfully
     * @throws NoSuchExecutorException if there is no registered {@link Executor} for the {@link Command} to be undone
     */
    public synchronized void undo() throws ExecutionException {

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

                    this.addExecutionRecord(new ExecutionRecord(command, ExecutionRecord.Operation.UNDO));
                    return;
                }
            }
            commands.next();
        } catch (NoSuchExecutorException e) {
            commands.next();
            throw e;
        } catch (final Exception e) {
            //Undo rolling history back
            this.clearUndoHistory();
            throw new ExecutionException(e);
        }
    }

    public void undo(int count) throws ExecutionException {
        if (count < 0)
        {
            throw new IndexOutOfBoundsException("Index out of range: " + count);
        }

        for (int i = 0; i < count; i++) {
            undo();
        }
    }

    /**
     * Call reexecute command on executor on previous command
     * Only works if both command and executor are redoable
     * @throws NoSuchElementException if there is no {@link Command } to be redone
     * @throws ExecutionException if redo does not execute successfully
     * @throws NoSuchExecutorException if there is no registered {@link Executor} for the {@link Command} to be redone
     */
    public synchronized void redo() throws ExecutionException {

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

                    this.addExecutionRecord(new ExecutionRecord(command, ExecutionRecord.Operation.REDO));
                    return;
                }
            }
            commands.previous();
        } catch (NoSuchExecutorException e) {
            commands.previous();
            throw e;
        } catch (Exception e) {
            //Undo rolling history back
            this.clearRedoHistory();
            throw new ExecutionException(e);
        }
    }

    public void redo(int count) throws ExecutionException {
        if (count < 0)
        {
            throw new IndexOutOfBoundsException("Index out of range: " + count);
        }

        for (int i = 0; i < count; i++) {
            redo();
        }
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

    public List<String> getUndoNames(int count) {
        List<String> results = new ArrayList<>(count);
        int steps = 0;

        while (steps < count && commands.hasPrevious()) {
            results.add(commands.previous().getName());
            steps++;
        }

        while (steps > 0) {
            commands.next();
            steps--;
        }

        return results;
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

    public List<String> getRedoNames(int count) {
        List<String> results = new ArrayList<>(count);
        int steps = 0;

        while (steps < count && commands.hasNext()) {
            results.add(commands.next().getName());
            steps++;
        }

        while (steps > 0) {
            commands.previous();
            steps--;
        }

        return results;
    }

    /**
     * Adds a new {@link ExecutionRecord} to the list of previously performed commands. Notifies any listeners of {@link CommandDelegator}
     * @param newRecord The ExecutionRecord to add
     * @return true if the record is successfully added, false otherwise
     */
    private boolean addExecutionRecord(final ExecutionRecord newRecord) {
        final ExecutionRecord latestRecord = executionRecords.isEmpty() ? null : executionRecords.first();

        if (executionRecords.add(newRecord)) {
            notifyListeners(latestRecord, newRecord);
            return true;
        }

        return false;
    }

    public Optional<ExecutionRecord> getLatestExecutionRecord() {
        return executionRecords.isEmpty() ? Optional.empty() : Optional.of(executionRecords.first());
    }

    public SortedSet<ExecutionRecord> getExecutionRecords() {
        return new TreeSet<>(executionRecords);
    }

    /**
     * Gets the {@code X} most recent records where x is the value of count parameter.
     *
     * @param count the number of ExecutionRecords to return
     * @return a new SortedSet containing the requested records
     * @throws IndexOutOfBoundsException if {@code count} is less than 0
     */
    public SortedSet<ExecutionRecord> getExecutionRecords(final int count) {
        if (count < 0)
        {
            throw new IndexOutOfBoundsException("Index out of range: " + count);
        }
        final SortedSet<ExecutionRecord> result = new TreeSet<>();
        final Iterator<ExecutionRecord> iterator = executionRecords.iterator();
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            result.add(iterator.next());
        }
        return result;
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

    private void notifyListeners(final Object oldValue, final Object newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, "lastCommandStatus", oldValue, newValue);
        for (final PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }
}
