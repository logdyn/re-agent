package com.logdyn;

import java.util.Comparator;

class ExecutionRecord implements Comparable<ExecutionRecord>{
    private Command command;
    private long timestamp;
    private Operation operation;

    public ExecutionRecord(Command command, Operation operation) {
        this(command, operation, System.currentTimeMillis());
    }

    public ExecutionRecord(Command command, Operation operation, long timestamp) {
        this.command = command;
        this.timestamp = timestamp;
        this.operation = operation;
    }

    public Command getCommand() {
        return command;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public int compareTo(ExecutionRecord o) {
        int result = Long.compare(o.timestamp, this.timestamp);
        if (result == 0)
        {
            final Comparator<String> comparator = Comparator.nullsFirst(Comparator.naturalOrder());
            result = comparator.compare(o.command.getName(), this.command.getName());
        }
        if (result == 0)
        {
            result = o.operation.compareTo(this.operation);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ExecutionRecord)) return false;
        final ExecutionRecord other = (ExecutionRecord) obj;
        return this.timestamp == other.timestamp
                && this.command.equals(other.command)
                && this.operation.equals(other.operation);
    }

    enum Operation {
        DO,
        UNDO,
        REDO
    }
}
