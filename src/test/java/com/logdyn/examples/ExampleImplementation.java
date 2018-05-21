package com.logdyn.examples;

import com.logdyn.*;

public class ExampleImplementation {

    public static void main(String[] args) throws ExecutionException {

        //Subscribe a new executor to listen for ExampleCommand
        CommandDelegator.getINSTANCE().subscribe(new ExampleExecutor(), ExampleCommand.class);


        //Publish a new ExampleCommand
        //The CommandDelegator will then call the execute() method in the ExampleExecutor
        CommandDelegator.getINSTANCE().publish(new ExampleCommand());

        CommandDelegator.getINSTANCE().undo();
        CommandDelegator.getINSTANCE().redo();
    }
}

class ExampleCommand implements UndoableCommand {

    @Override
    public String getName() {
        return "Example Command";
    }
}

class ExampleExecutor implements UndoableExecutor<ExampleCommand> {
    @Override
    public void execute(ExampleCommand command) {
        System.out.println("Hello, World!");
    }

    @Override
    public void unexecute(ExampleCommand command) {
        System.out.println("Goodbye, World!");
    }

    @Override
    public void reexecute(ExampleCommand command) {
        System.out.println("Hello again, World!");
    }
}