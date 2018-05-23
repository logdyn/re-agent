package com.logdyn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ExecutorTree<C extends Command> {

    private Collection<ExecutorTreeElement<? extends C>> elements = new HashSet<>();

    public void put(final Class<C> commandClass, final Executor<? super C> executor){

        for (final Iterator<ExecutorTreeElement<? extends C>> i = elements.iterator(); i.hasNext();) {
            final ExecutorTreeElement<? extends C> element = i.next();
            if (element.getCommandClass().isAssignableFrom(commandClass)){
                //noinspection unchecked
                element.getTree().put((Class) commandClass, executor);
                return;
            }
            else if(commandClass.isAssignableFrom(element.getCommandClass())) {
                final ExecutorTree newTree = new ExecutorTree<>();
                newTree.elements.add(element);
                i.remove();
                final ExecutorTreeElement newElement = new ExecutorTreeElement(commandClass, executor, element.tree);
                elements.add(newElement);
                return;
            }
        }

        //noinspection unchecked
        elements.add(new ExecutorTreeElement(commandClass, executor));
    }

    public <T extends Command> Executor<? super T> get(final T command, final boolean getSpecific) {
        Executor<? super T> result = null;
        for (ExecutorTreeElement<? extends C> element : elements) {
            if (getSpecific && element.getCommandClass().isAssignableFrom(command.getClass())){
                result = element.getTree().get(command, true);
            }
            if (result == null)
            {
                //noinspection unchecked
                result = (Executor<? super T>) element.getExecutor();
            }
        }
        return result;
    }

    class ExecutorTreeElement<SC extends C> {
        private Class<SC> commandClass;
        private Executor<SC> executor;
        private ExecutorTree<SC> tree;

        public ExecutorTreeElement(final Class<SC> commandClass, final Executor<SC> executor) {
            this(commandClass, executor, new ExecutorTree<>());
        }

        ExecutorTreeElement(final Class<SC> commandClass, final Executor<SC> executor, final ExecutorTree<SC> tree) {
            this.commandClass = commandClass;
            this.executor = executor;
            this.tree = tree;
        }

        Class<SC> getCommandClass() {
            return commandClass;
        }

        public Executor<SC> getExecutor() {
            return executor;
        }

        ExecutorTree<SC> getTree() {
            return tree;
        }
    }
}
