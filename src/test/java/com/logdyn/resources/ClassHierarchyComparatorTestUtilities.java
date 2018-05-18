package com.logdyn.resources;

import com.logdyn.Command;

public class ClassHierarchyComparatorTestUtilities {
    public static class StandaloneCommand implements Command {

        @Override
        public String getName() {
            return "Standalone Command";
        }
    }

    public static class ParentCommand implements Command {

        @Override
        public String getName() {
            return "Parent Command";
        }
    }

    public static class ChildCommand extends ParentCommand {
        @Override
        public String getName() {
            return "Child Command";
        }
    }
}
