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

import de.mrapp.apriori.Sorting.Order
import de.mrapp.apriori.metrics.Confidence
import de.mrapp.apriori.metrics.Leverage
import de.mrapp.apriori.metrics.Lift
import de.mrapp.apriori.metrics.Support
import java.text.DecimalFormat
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [RuleSet].
 *
 * @author Michael Rapp
 */
class RuleSetTest {

    @Test
    fun testConstructorWithComparatorParameter() {
        val comparator = Sorting.forAssociationRules()
        val ruleSet = RuleSet<NamedItem>(comparator)
        assertTrue(ruleSet.isEmpty())
        assertEquals(0, ruleSet.size.toLong())
        assertEquals(comparator, ruleSet.comparator())
    }

    @Test
    fun testConstructorWithCollectionAndComparatorParameters() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        val head1 = ItemSet<NamedItem>()
        head1.add(NamedItem("b"))
        val associationRule1 = AssociationRule(body1, head1, 0.5)
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("c"))
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("d"))
        val associationRule2 = AssociationRule(body2, head2, 0.5)
        val collection = LinkedList<AssociationRule<NamedItem>>()
        collection.add(associationRule1)
        collection.add(associationRule2)
        val ruleSet = RuleSet(collection, Sorting.forAssociationRules())
        assertEquals(collection.size.toLong(), ruleSet.size.toLong())
        val iterator = ruleSet.iterator()
        assertEquals(associationRule2, iterator.next())
        assertEquals(associationRule1, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun testSort() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("b"))
        body2.support = 0.7
        val associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        val associationRule2 = AssociationRule(body2, ItemSet(), 0.5)
        val ruleSet = RuleSet<NamedItem>(Sorting.forAssociationRules())
        ruleSet.add(associationRule1)
        ruleSet.add(associationRule2)
        assertEquals(associationRule1, ruleSet.first())
        assertEquals(associationRule2, ruleSet.last())
        val sorting = Sorting.forAssociationRules().withOrder(Order.ASCENDING)
        val sortedRuleSet = ruleSet.sort(sorting)
        assertEquals(associationRule2, sortedRuleSet.first())
        assertEquals(associationRule1, sortedRuleSet.last())
    }

    @Test
    fun testFilter() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("b"))
        body2.support = 0.7
        val associationRule1 = AssociationRule(body1, ItemSet(), 0.4)
        val associationRule2 = AssociationRule(body2, ItemSet(), 0.5)
        val ruleSet = RuleSet<NamedItem>(Sorting.forAssociationRules())
        ruleSet.add(associationRule1)
        ruleSet.add(associationRule2)
        assertEquals(associationRule1, ruleSet.first())
        assertEquals(associationRule2, ruleSet.last())
        val filter = Filter.forAssociationRules().byOperator(Support(), 0.5)
        val filteredRuleSet = ruleSet.filter(filter)
        assertEquals(1, filteredRuleSet.size.toLong())
        assertEquals(associationRule2, filteredRuleSet.first())
    }

    @Test
    fun testToString() {
        val body1 = ItemSet<NamedItem>()
        body1.add(NamedItem("a"))
        body1.support = 0.5
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("b"))
        body2.support = 0.8
        val associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        val associationRule2 = AssociationRule(body2, ItemSet(), 0.7)
        val ruleSet = RuleSet<NamedItem>(Sorting.forAssociationRules())
        ruleSet.add(associationRule1)
        ruleSet.add(associationRule2)
        val decimalFormat = DecimalFormat()
        decimalFormat.minimumFractionDigits = 1
        decimalFormat.maximumFractionDigits = 2
        assertEquals("[" + associationRule1.toString() + " (support = " +
                decimalFormat.format(Support().evaluate(associationRule1)) + ", confidence = " +
                decimalFormat.format(Confidence().evaluate(associationRule1)) + ", lift = " +
                decimalFormat.format(Lift().evaluate(associationRule1)) + ", leverage = " +
                decimalFormat.format(Leverage().evaluate(associationRule1)) + "),\n"
                + associationRule2.toString() + " (support = " +
                decimalFormat.format(Support().evaluate(associationRule2)) + ", confidence = " +
                decimalFormat.format(Confidence().evaluate(associationRule2)) + ", lift = " +
                decimalFormat.format(Lift().evaluate(associationRule2)) + ", leverage = " +
                decimalFormat.format(Leverage().evaluate(associationRule2)) + ")]",
                ruleSet.toString())
    }

    @Test
    fun testToStringIfEmpty() {
        assertEquals("[]", RuleSet<NamedItem>(null).toString())
    }

}