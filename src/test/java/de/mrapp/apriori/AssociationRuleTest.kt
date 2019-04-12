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

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [AssociationRule].
 *
 * @author Michael Rapp
 */
class AssociationRuleTest {

    @Test
    fun testConstructor() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.add(NamedItem("b"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        head.add(NamedItem("d"))
        val support = 0.5
        val (body1, head1, support1) = AssociationRule(body, head, support)
        assertEquals(body, body1)
        assertEquals(head, head1)
        assertEquals(support, support1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorThrowsExceptionWhenSupportIsLessThanZero() {
        AssociationRule(ItemSet(), ItemSet<NamedItem>(), -1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorThrowsExceptionWhenSupportIsGreaterThanOne() {
        AssociationRule(ItemSet(), ItemSet<NamedItem>(), 1.1)
    }

    @Test
    fun testCoversWithArrayParameter() {
        val items = arrayOf(NamedItem("a"), NamedItem("c"), NamedItem("d"), NamedItem("e"),
                NamedItem("f"))
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.add(NamedItem("b"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        var associationRule = AssociationRule(ItemSet(), head, 0.5)
        assertTrue(associationRule.covers(*items))
        associationRule = AssociationRule(body, head, 0.5)
        assertFalse(associationRule.covers(*items))
        items[1] = NamedItem("b")
        assertTrue(associationRule.covers(*items))
    }

    @Test
    fun testCoversWithIterableParameter() {
        val items = LinkedList<NamedItem>()
        items.add(NamedItem("a"))
        items.add(NamedItem("c"))
        items.add(NamedItem("d"))
        items.add(NamedItem("e"))
        items.add(NamedItem("f"))
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.add(NamedItem("b"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        var associationRule = AssociationRule(ItemSet(), head, 0.5)
        assertTrue(associationRule.covers(items))
        associationRule = AssociationRule(body, head, 0.5)
        assertFalse(associationRule.covers(items))
        items.add(NamedItem("b"))
        assertTrue(associationRule.covers(items))
    }

    @Test
    fun testCoversWithIteratorParameter() {
        val items = LinkedList<NamedItem>()
        items.add(NamedItem("a"))
        items.add(NamedItem("c"))
        items.add(NamedItem("d"))
        items.add(NamedItem("e"))
        items.add(NamedItem("f"))
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.add(NamedItem("b"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        var associationRule = AssociationRule(ItemSet(), head, 0.5)
        assertTrue(associationRule.covers(items.iterator()))
        associationRule = AssociationRule(body, head, 0.5)
        assertFalse(associationRule.covers(items.iterator()))
        items.add(NamedItem("b"))
        assertTrue(associationRule.covers(items.iterator()))
    }

    @Test
    fun testCompareTo() {
        val item1 = NamedItem("a")
        val item2 = NamedItem("b")
        val body = ItemSet<NamedItem>()
        body.add(item1)
        val head = ItemSet<NamedItem>()
        head.add(item2)
        val associationRule1 = AssociationRule(body, head, 0.5)
        var associationRule2 = AssociationRule(body, head, 0.5)
        assertEquals(0, associationRule1.compareTo(associationRule2).toLong())
        associationRule2 = AssociationRule(body, head, 0.6)
        assertEquals(-1, associationRule1.compareTo(associationRule2).toLong())
        associationRule2 = AssociationRule(body, head, 0.4)
        assertEquals(1, associationRule1.compareTo(associationRule2).toLong())
    }

    @Test
    fun testToString() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.add(NamedItem("b"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        head.add(NamedItem("d"))
        val associationRule = AssociationRule(body, head, 0.5)
        assertEquals("[a, b] -> [c, d]", associationRule.toString())
    }

}
