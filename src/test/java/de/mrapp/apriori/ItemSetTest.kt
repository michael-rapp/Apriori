/*
 * Copyright 2017 - 2019 Michael Rapp
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
package de.mrapp.apriori

import kotlin.test.*

/**
 * Tests the functionality of the class [ItemSet].
 *
 * @author Michael Rapp
 */
class ItemSetTest {

    @Test
    fun testDefaultConstructor() {
        val itemSet = ItemSet<NamedItem>()
        assertEquals(0.0, itemSet.support)
        assertTrue(itemSet.isEmpty())
    }

    @Test
    fun testConstructorWithItemSetArgument() {
        val item = NamedItem("a")
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.support = 0.5
        itemSet1.add(item)
        val itemSet2 = ItemSet(itemSet1)
        assertEquals(itemSet1.support, itemSet2.support)
        assertEquals(itemSet1.size.toLong(), itemSet2.size.toLong())
        assertEquals(item, itemSet2.first())
    }

    @Test
    fun testSetSupport() {
        val support = 0.5
        val itemSet = ItemSet<NamedItem>()
        itemSet.support = support
        assertEquals(support, itemSet.support)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetSupportThrowsExceptionWhenSupportIsLessThanZero() {
        val itemSet = ItemSet<NamedItem>()
        itemSet.support = -1.0
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetSupportThrowsExceptionWhenSupportIsGreaterThanOne() {
        val itemSet = ItemSet<NamedItem>()
        itemSet.support = 1.1
    }

    @Test
    fun testAdd() {
        val item1 = NamedItem("c")
        val item2 = NamedItem("a")
        val item3 = NamedItem("b")
        val itemSet = ItemSet<NamedItem>()
        assertTrue(itemSet.isEmpty())
        itemSet.add(item1)
        itemSet.add(item2)
        itemSet.add(item3)
        itemSet.add(item3)
        assertEquals(3, itemSet.size.toLong())
        val iterator = itemSet.iterator()
        assertEquals(item2, iterator.next())
        assertEquals(item3, iterator.next())
        assertEquals(item1, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun testRemove() {
        val item1 = NamedItem("c")
        val item2 = NamedItem("a")
        val item3 = NamedItem("b")
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(item1)
        itemSet.add(item2)
        itemSet.add(item3)
        assertEquals(3, itemSet.size.toLong())
        itemSet.remove(item3)
        itemSet.remove(item3)
        assertEquals(2, itemSet.size.toLong())
        val iterator = itemSet.iterator()
        assertEquals(item2, iterator.next())
        assertEquals(item1, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun testContains() {
        val item1 = NamedItem("a")
        val item2 = NamedItem("b")
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(item1)
        assertEquals(1, itemSet.size.toLong())
        assertTrue(itemSet.contains(item1))
        assertFalse(itemSet.contains(item2))
        itemSet.clear()
        assertTrue(itemSet.isEmpty())
        assertFalse(itemSet.contains(item1))
        assertFalse(itemSet.contains(item2))
    }

    @Test
    fun testCompareTo() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.support = 0.5
        assertEquals(0, itemSet1.compareTo(itemSet2).toLong())
        itemSet1.support = 0.3
        assertEquals(-1, itemSet1.compareTo(itemSet2).toLong())
        itemSet1.support = 0.6
        assertEquals(1, itemSet1.compareTo(itemSet2).toLong())
    }

    @Test
    fun testToString() {
        val item1 = NamedItem("a")
        val item2 = NamedItem("b")
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(item1)
        itemSet.add(item2)
        assertEquals("[" + item1.name + ", " + item2.name + "]", itemSet.toString())
    }

    @Test
    fun testHashCode() {
        val itemSet1 = ItemSet<NamedItem>()
        val itemSet2 = ItemSet<NamedItem>()
        assertEquals(itemSet1.hashCode().toLong(), itemSet1.hashCode().toLong())
        assertEquals(itemSet1.hashCode().toLong(), itemSet2.hashCode().toLong())
        itemSet1.add(NamedItem("a"))
        assertNotEquals(itemSet1.hashCode().toLong(), itemSet2.hashCode().toLong())
        itemSet2.add(NamedItem("a"))
        assertEquals(itemSet1.hashCode().toLong(), itemSet2.hashCode().toLong())
        itemSet1.add(NamedItem("b"))
        assertNotEquals(itemSet1.hashCode().toLong(), itemSet2.hashCode().toLong())
    }

    @Test
    fun testEquals() {
        val itemSet1 = ItemSet<NamedItem>()
        val itemSet2 = ItemSet<NamedItem>()
        assertFalse(itemSet1 == null)
        assertFalse(itemSet1 == Any())
        assertTrue(itemSet1 == itemSet1)
        assertTrue(itemSet1 == itemSet2)
        itemSet1.add(NamedItem("a"))
        assertFalse(itemSet1 == itemSet2)
        itemSet2.add(NamedItem("a"))
        assertTrue(itemSet1 == itemSet2)
        itemSet1.add(NamedItem("b"))
        assertFalse(itemSet1 == itemSet2)
    }

}
