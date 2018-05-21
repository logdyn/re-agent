# Logdyn re-agent [![Build Status](https://travis-ci.org/logdyn/re-agent.svg?branch=master)](https://travis-ci.org/logdyn/re-agent)

Our WIP Java [Command delegation API](http://logdyn.com/re-agent/com/logdyn/package-summary.html).

## Features
- Undo & Redo
- Loosely coupled `Command Pattern`
- Listener support

## Installation
Maven repository coming soon! 

Current release can be found [on GitHub](https://github.com/logdyn/re-agent/releases). 
Add the `re-agent.jar` file as a library and you're good to go!

## How to Use

### Commands
A realization of the  `Command` interface is used to trigger an event in the subscribed `Executor`. `Command`'s do not specify execution behaviour, rather they are used to trigger an event and to capture any information that will be needed for that event.

```java
class ExampleCommand implements UndoableCommand {

    @Override
    public String getName() {
        return "Example Undoable Command";
    }
}
```

There are also `UndoableCommand`'s, which can be executed by any `Executor`, although if you want to make use of undo/redo an `UndoableExecutor` must be used. By default the `reexecute()` method calls `execute()`, although this can be overridden. An `UndoableCommand` must provide data necessary for undoing an action, as well as the initial execution.

### Executors
A realization of the `Executor` interface is used to execute specific behaviour when a `Command` has been published.

```java
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
```

### Subscribing to a Command
Subscribing to a `Command` requires you to specify an `Executor` and the `Command` that it will execute. An `Executor` will execute the type of `Command` it is subscribed to, or any sub-class of that `Command`. As a result of this, only one `Executor` of this type or sub-type of `Command`.

```java
CommandDelegator.getINSTANCE().subscribe(new ExampleExecutor(), ExampleCommand.class);
```

### Publishing a Command
Publishing a `Command` is as simple as passing it into the `publish()` method. This will then call the `execute()` method of the relevant `Executor` class. The call to `execute()` will be on the same thread as the call to `publish`, this means that if you want to initiate a task to run in the background it must be published from the background.

If a published `Command` is not undoable, it will clear the current undo history. Likewise if you have undone a `Command` and a new one is published, the redo history will be cleared.

```java
CommandDelegator.getINSTANCE().publish(new ExampleCommand());
```

### Undo & Redo
Undo & Redo are method calls on the `CommandDelegator`. This operates in the same manner as the `publish()` method, it will  call the `unexecute()` or `reexecute()` method of the relevant `Executor` class.

```java
CommandDelegator.getINSTANCE().undo();
CommandDelegator.getINSTANCE().redo();
```