package com.logdyn;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static com.logdyn.resources.ClassHierarchyComparatorTestUtilities.*;

class ClassHierarchyComparatorTest {

    @Test
    void compareEqualClasses() {
        Comparator<Class<?>> comparator = new ClassHierarchyComparator();
        assertEquals(0, comparator.compare(
                StandaloneCommand.class,
                StandaloneCommand.class));
    }

    @Test
    void compareFirstClassGreater() {
        Set<Class<?>> set = new TreeSet<>(new ClassHierarchyComparator());
        set.add(ParentCommand.class);
        set.add(ChildCommand.class);
        Iterator<Class<?>> iterator = set.iterator();

        assertEquals(ParentCommand.class, iterator.next());
        assertEquals(ChildCommand.class, iterator.next());
    }

    @Test
    void compareSecondClassGreater() {
        Set<Class<?>> set = new TreeSet<>(new ClassHierarchyComparator());
        set.add(ChildCommand.class);
        set.add(ParentCommand.class);
        Iterator<Class<?>> iterator = set.iterator();

        assertEquals(ParentCommand.class, iterator.next());
        assertEquals(ChildCommand.class, iterator.next());
    }

    @Test
    void compareSeparateClasses() {
        Comparator<Class<?>> comparator = new ClassHierarchyComparator();
        assertNotEquals(0, comparator.compare(
                StandaloneCommand.class,
                ParentCommand.class));
    }
}