/*
 * Copyright 2017 Michael Rapp
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

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Tests the functionality of the class {@link ItemSet}.
 *
 * @author Michael Rapp
 */
public class ItemSetTest {

    /**
     * Tests, if all class members are set correctly by the default constructor.
     */
    @Test
    public final void testDefaultConstructor() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        assertEquals(0, itemSet.getSupport(), 0);
        assertTrue(itemSet.isEmpty());
    }

    /**
     * Tests, if all class members are set correctly by the constructor, which expects another item
     * set as an argument.
     */
    @Test
    public final void testConstructorWithItemSetArgument() {
        NamedItem item = new NamedItem("a");
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        itemSet1.add(item);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>(itemSet1);
        assertEquals(itemSet1.getSupport(), itemSet2.getSupport(), 0);
        assertEquals(itemSet1.size(), itemSet2.size());
        assertEquals(item, itemSet2.first());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, by the constructor, which
     * expects another item set as an argument, if the item set is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithItemSetArgumentThrowsException() {
        new ItemSet<>(null);
    }

    /**
     * Tests the functionality of the method, which allows to set the support of an item set.
     */
    @Test
    public final void testSetSupport() {
        double support = 0.5;
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.setSupport(support);
        assertEquals(support, itemSet.getSupport(), 0);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, which allows to
     * set the support of an item set, if the support is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSetSupportThrowsExceptionWhenSupportIsLessThanZero() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.setSupport(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, which allows to
     * set the support of an item set, if the support is greater than 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSetSupportThrowsExceptionWhenSupportIsGreaterThanOne() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.setSupport(1.1);
    }

    /**
     * Tests the functionality of the method, which allows to add new items to an item set.
     */
    @Test
    public final void testAdd() {
        NamedItem item1 = new NamedItem("c");
        NamedItem item2 = new NamedItem("a");
        NamedItem item3 = new NamedItem("b");
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        assertTrue(itemSet.isEmpty());
        itemSet.add(item1);
        itemSet.add(item2);
        itemSet.add(item3);
        itemSet.add(item3);
        assertEquals(3, itemSet.size());
        Iterator<NamedItem> iterator = itemSet.iterator();
        assertEquals(item2, iterator.next());
        assertEquals(item3, iterator.next());
        assertEquals(item1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the functionality of the method, which allows to remove items from an item set.
     */
    @Test
    public final void testRemove() {
        NamedItem item1 = new NamedItem("c");
        NamedItem item2 = new NamedItem("a");
        NamedItem item3 = new NamedItem("b");
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(item1);
        itemSet.add(item2);
        itemSet.add(item3);
        assertEquals(3, itemSet.size());
        itemSet.remove(item3);
        itemSet.remove(item3);
        assertEquals(2, itemSet.size());
        Iterator<NamedItem> iterator = itemSet.iterator();
        assertEquals(item2, iterator.next());
        assertEquals(item1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the functionality of the method, which allows to check, whether an item is contained by
     * an item set, or not.
     */
    @Test
    public final void testContains() {
        NamedItem item1 = new NamedItem("a");
        NamedItem item2 = new NamedItem("b");
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(item1);
        assertEquals(1, itemSet.size());
        assertTrue(itemSet.contains(item1));
        assertFalse(itemSet.contains(item2));
        itemSet.clear();
        assertTrue(itemSet.isEmpty());
        assertFalse(itemSet.contains(item1));
        assertFalse(itemSet.contains(item2));
    }

    /**
     * Tests the functionality of the compareTo-method.
     */
    @Test
    public final void testCompareTo() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.setSupport(0.5);
        assertEquals(0, itemSet1.compareTo(itemSet2));
        itemSet1.setSupport(0.3);
        assertEquals(-1, itemSet1.compareTo(itemSet2));
        itemSet1.setSupport(0.6);
        assertEquals(1, itemSet1.compareTo(itemSet2));
    }

    /**
     * Tests the functionality of the clone-method.
     */
    @Test
    public final void testClone() {
        NamedItem item = new NamedItem("a");
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        itemSet1.add(item);
        ItemSet<NamedItem> itemSet2 = itemSet1.clone();
        assertEquals(itemSet1.getSupport(), itemSet2.getSupport(), 0);
        assertEquals(itemSet1.size(), itemSet2.size());
        assertEquals(item, itemSet2.first());
    }

    /**
     * Tests the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        NamedItem item1 = new NamedItem("a");
        NamedItem item2 = new NamedItem("b");
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(item1);
        itemSet.add(item2);
        assertEquals("[" + item1.getName() + ", " + item2.getName() + "]", itemSet.toString());
    }

    /**
     * Tests the functionality of the hashCode-method.
     */
    @Test
    public final void testHashCode() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        assertEquals(itemSet1.hashCode(), itemSet1.hashCode());
        assertEquals(itemSet1.hashCode(), itemSet2.hashCode());
        itemSet1.add(new NamedItem("a"));
        assertNotEquals(itemSet1.hashCode(), itemSet2.hashCode());
        itemSet2.add(new NamedItem("a"));
        assertEquals(itemSet1.hashCode(), itemSet2.hashCode());
        itemSet1.add(new NamedItem("b"));
        assertNotEquals(itemSet1.hashCode(), itemSet2.hashCode());
    }

    /**
     * Tests the functionality of the equals-method.
     */
    @Test
    public final void testEquals() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        assertFalse(itemSet1.equals(null));
        assertFalse(itemSet1.equals(new Object()));
        assertTrue(itemSet1.equals(itemSet1));
        assertTrue(itemSet1.equals(itemSet2));
        itemSet1.add(new NamedItem("a"));
        assertFalse(itemSet1.equals(itemSet2));
        itemSet2.add(new NamedItem("a"));
        assertTrue(itemSet1.equals(itemSet2));
        itemSet1.add(new NamedItem("b"));
        assertFalse(itemSet1.equals(itemSet2));
    }

}
