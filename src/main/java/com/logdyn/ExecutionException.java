package com.logdyn;

public class ExecutionException extends Exception {

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
