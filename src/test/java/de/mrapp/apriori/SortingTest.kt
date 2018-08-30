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
package de.mrapp.apriori

import de.mrapp.apriori.metrics.Support
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [Sorting].
 *
 * @author Michael Rapp
 */
class SortingTest {

    @Test
    fun testDefaultSortingForItemSets() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.support = 0.5
        val sorting = Sorting.forItemSets()
        assertEquals(0, sorting.compare(itemSet1, itemSet2).toLong())
        itemSet1.support = 0.6
        assertEquals(-1, sorting.compare(itemSet1, itemSet2).toLong())
        itemSet1.support = 0.4
        assertEquals(1, sorting.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testAscendingSortingForItemSets() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.support = 0.5
        val sorting = Sorting.forItemSets().withOrder(Sorting.Order.ASCENDING)
        assertEquals(0, sorting.compare(itemSet1, itemSet2).toLong())
        itemSet1.support = 0.6
        assertEquals(1, sorting.compare(itemSet1, itemSet2).toLong())
        itemSet1.support = 0.4
        assertEquals(-1, sorting.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testSortingForItemSetsWhenTieBreaking() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.support = 0.5
        val sorting = Sorting.forItemSets().withTieBreaking(Comparator { _, _ -> 1 })
        assertEquals(-1, sorting.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testDefaultSortingForAssociationRules() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val head1 = ItemSet<NamedItem>()
        head1.add(NamedItem("b"))
        var associationRule1 = AssociationRule(body1, head1, 0.25)
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("c"))
        body2.support = 0.5
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("d"))
        val associationRule2 = AssociationRule(body2, head2, 0.25)
        val sorting = Sorting.forAssociationRules()
        assertEquals(0, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.5)
        assertEquals(-1, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.1)
        assertEquals(1, sorting.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testAscendingSortingForAssociationRules() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val head1 = ItemSet<NamedItem>()
        head1.add(NamedItem("b"))
        var associationRule1 = AssociationRule(body1, head1, 0.25)
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("c"))
        body2.support = 0.5
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("d"))
        val associationRule2 = AssociationRule(body2, head2, 0.25)
        val sorting = Sorting.forAssociationRules().withOrder(Sorting.Order.ASCENDING)
        assertEquals(0, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.5)
        assertEquals(1, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.1)
        assertEquals(-1, sorting.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testSortingForAssociationRulesWhenTieBreaking() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val head1 = ItemSet<NamedItem>()
        head1.add(NamedItem("b"))
        val associationRule1 = AssociationRule(body1, head1, 0.25)
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("c"))
        body2.support = 0.5
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("d"))
        val associationRule2 = AssociationRule(body2, head2, 0.25)
        val sorting = Sorting.forAssociationRules().withTieBreaking(Comparator { _, _ -> 1 })
        assertEquals(-1, sorting.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testSortingByOperatorForAssociationRules() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val head1 = ItemSet<NamedItem>()
        head1.add(NamedItem("b"))
        var associationRule1 = AssociationRule(body1, head1, 0.25)
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("c"))
        body2.support = 0.5
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("d"))
        val associationRule2 = AssociationRule(body2, head2, 0.25)
        val sorting = Sorting.forAssociationRules().byOperator(Support())
        assertEquals(0, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.5)
        assertEquals(-1, sorting.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.1)
        assertEquals(1, sorting.compare(associationRule1, associationRule2).toLong())
    }

}