package com.logdyn.examples;

import com.logdyn.*;

public class ExampleImplementation {

    public static void main(String[] args) throws ExecutionException {

        //Subscribe a new executor to listen for ExampleCommand
        CommandDelegator.getINSTANCE().subscribe(new ExampleExecutor(), ExampleCommand.class);


        //Publish a new ExampleCommand
        //The CommandDelegator will then call the execute() method in the ExampleExecutor
        CommandDelegator.getINSTANCE().publish(new ExampleCommand());
    }
}

class ExampleCommand implements Command {

    @Override
    public String getName() {
        return "Example Command";
    }
}

class ExampleExecutor implements Executor<ExampleCommand> {
    @Override
    public void execute(ExampleCommand command) {
        System.out.println("Hello, World!");
    }
}