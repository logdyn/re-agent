package com.logdyn;

import java.util.Comparator;

public class ClassHierarchyComparator implements Comparator<Class<?>> {

    @Override
    public int compare(final Class<?> first, final Class<?> second) {

        if (first.equals(second)) {
            return 0;
        }

        if (first.isAssignableFrom(second)) {
            return -1;
        }

        if (second.isAssignableFrom(first)) {
            return 1;
        }
        //if classes are unrelated order alphabetically.
        return first.getName().compareTo(second.getName());
    }
}
