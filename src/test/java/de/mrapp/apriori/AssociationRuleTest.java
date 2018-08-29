/*
 * Copyright 2017 - 2018 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.apriori;

import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Tests the functionality of the class {@link AssociationRule}.
 *
 * @author Michael Rapp
 */
public class AssociationRuleTest {

    /**
     * Tests, if all class members are set correctly by the constructor.
     */
    @Test
    public final void testConstructor() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        head.add(new NamedItem("d"));
        double support = 0.5;
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, support);
        assertEquals(body, associationRule.getBody());
        assertEquals(head, associationRule.getHead());
        assertEquals(support, associationRule.getSupport(), 0);
    }

    /**
     * Ensures, tha an {@link IllegalArgumentException} is thrown, if the support, which is passed
     * as a constructor parameter, is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenSupportIsLessThanZero() {
        new AssociationRule<NamedItem>(new ItemSet<>(), new ItemSet<>(), -1);
    }

    /**
     * Ensures, tha an {@link IllegalArgumentException} is thrown, if the support, which is passed
     * as a constructor parameter, is greater than 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenSupportIsGreaterThanOne() {
        new AssociationRule<NamedItem>(new ItemSet<>(), new ItemSet<>(), 1.1);
    }

    /**
     * Tests the functionality of the {@link AssociationRule#covers(Item[])} method.
     */
    @Test
    public final void testCoversWithArrayParameter() {
        NamedItem[] items = {
                new NamedItem("a"),
                new NamedItem("c"),
                new NamedItem("d"),
                new NamedItem("e"),
                new NamedItem("f")};
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), head,
                0.5);
        assertTrue(associationRule.covers(items));
        associationRule = new AssociationRule<>(body, head, 0.5);
        assertFalse(associationRule.covers(items));
        items[1] = new NamedItem("b");
        assertTrue(associationRule.covers(items));
    }

    /**
     * Tests the functionality of the {@link AssociationRule#covers(Iterable)} method.
     */
    @Test
    public final void testCoversWithIterableParameter() {
        Collection<NamedItem> items = new LinkedList<>();
        items.add(new NamedItem("a"));
        items.add(new NamedItem("c"));
        items.add(new NamedItem("d"));
        items.add(new NamedItem("e"));
        items.add(new NamedItem("f"));
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), head,
                0.5);
        assertTrue(associationRule.covers(items));
        associationRule = new AssociationRule<>(body, head, 0.5);
        assertFalse(associationRule.covers(items));
        items.add(new NamedItem("b"));
        assertTrue(associationRule.covers(items));
    }

    /**
     * Tests the functionality of the {@link AssociationRule#covers(Iterator)} method.
     */
    @Test
    public final void testCoversWithIteratorParameter() {
        Collection<NamedItem> items = new LinkedList<>();
        items.add(new NamedItem("a"));
        items.add(new NamedItem("c"));
        items.add(new NamedItem("d"));
        items.add(new NamedItem("e"));
        items.add(new NamedItem("f"));
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), head,
                0.5);
        assertTrue(associationRule.covers(items.iterator()));
        associationRule = new AssociationRule<>(body, head, 0.5);
        assertFalse(associationRule.covers(items.iterator()));
        items.add(new NamedItem("b"));
        assertTrue(associationRule.covers(items.iterator()));
    }

    /**
     * Tests the functionality of the compareTo-method.
     */
    @Test
    public final void testCompareTo() {
        NamedItem item1 = new NamedItem("a");
        NamedItem item2 = new NamedItem("b");
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(item1);
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(item2);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body, head, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body, head, 0.5);
        assertEquals(0, associationRule1.compareTo(associationRule2));
        associationRule2 = new AssociationRule<>(body, head, 0.6);
        assertEquals(-1, associationRule1.compareTo(associationRule2));
        associationRule2 = new AssociationRule<>(body, head, 0.4);
        assertEquals(1, associationRule1.compareTo(associationRule2));
    }

    /**
     * Tests the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        head.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        assertEquals("[a, b] -> [c, d]", associationRule.toString());
    }

}