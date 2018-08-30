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

import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [TieBreaker].
 *
 * @author Michael Rapp
 */
class TieBreakerTest {

    @Test
    fun testDefaultTieBreakerForItemSets() {
        val itemSet1 = ItemSet<NamedItem>()
        val itemSet2 = ItemSet<NamedItem>()
        val tieBreaker = TieBreaker.forItemSets()
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testTieBreakingForItemSetsPreferSmall() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("a"))
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.add(NamedItem("b"))
        val tieBreaker = TieBreaker.forItemSets().preferSmall()
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2).toLong())
        itemSet1.add(NamedItem("c"))
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2).toLong())
        itemSet2.add(NamedItem("d"))
        itemSet2.add(NamedItem("e"))
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testTieBreakingForItemSetsPreferLarge() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("a"))
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.add(NamedItem("b"))
        val tieBreaker = TieBreaker.forItemSets().preferLarge()
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2).toLong())
        itemSet1.add(NamedItem("c"))
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2).toLong())
        itemSet2.add(NamedItem("d"))
        itemSet2.add(NamedItem("e"))
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testCustomTieBreakingForItemSets() {
        val itemSet1 = ItemSet<NamedItem>()
        val itemSet2 = ItemSet<NamedItem>()
        val tieBreaker = TieBreaker.forItemSets().custom(Comparator { _, _ -> 1 })
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testMultipleTieBreakingStrategiesForItemSets() {
        val itemSet1 = ItemSet<NamedItem>()
        val itemSet2 = ItemSet<NamedItem>()
        val comparator1 = Comparator<ItemSet<*>> { _, _ -> 0 }
        val comparator2 = Comparator<ItemSet<*>> { _, _ -> -1 }
        val tieBreaker = TieBreaker.forItemSets().custom(comparator1).custom(comparator2)
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2).toLong())
    }

    @Test
    fun testDefaultTieBreakerForAssociationRules() {
        val associationRule1 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val associationRule2 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val tieBreaker = TieBreaker.forAssociationRules()
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesByOperator() {
        val associationRule1 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val associationRule2 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val operator = mock(Operator::class.java)
        val tieBreaker = TieBreaker.forAssociationRules().byOperator(operator)
        doReturn(1.0, 0.0).`when`(operator).evaluate(MockUtil.any())
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        doReturn(0.5, 0.5).`when`(operator).evaluate(MockUtil.any())
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        doReturn(0.0, 1.0).`when`(operator).evaluate(MockUtil.any())
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesPreferSimple() {
        val body1 = ItemSet<NamedItem>()
        val head1 = ItemSet<NamedItem>()
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("a"))
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("b"))
        var associationRule1 = AssociationRule(body1, head1, 0.5)
        var associationRule2 = AssociationRule(body2, head2, 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferSimple()
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.5)
        associationRule2 = AssociationRule(body1, head1, 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body2, head2, 0.5)
        associationRule2 = AssociationRule(body1, head1, 0.5)
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesPreferComplex() {
        val body1 = ItemSet<NamedItem>()
        val head1 = ItemSet<NamedItem>()
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("a"))
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("b"))
        var associationRule1 = AssociationRule(body1, head1, 0.5)
        var associationRule2 = AssociationRule(body2, head2, 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferComplex()
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, head1, 0.5)
        associationRule2 = AssociationRule(body1, head1, 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body2, head2, 0.5)
        associationRule2 = AssociationRule(body1, head1, 0.5)
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingPreferSimpleBody() {
        val body1 = ItemSet<NamedItem>()
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("a"))
        var associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        var associationRule2 = AssociationRule(body2, ItemSet(), 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferSimpleBody()
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        associationRule2 = AssociationRule(body1, ItemSet(), 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body2, ItemSet(), 0.5)
        associationRule2 = AssociationRule(body1, ItemSet(), 0.5)
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesPreferComplexBody() {
        val body1 = ItemSet<NamedItem>()
        val body2 = ItemSet<NamedItem>()
        body2.add(NamedItem("a"))
        var associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        var associationRule2 = AssociationRule(body2, ItemSet(), 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferComplexBody()
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body1, ItemSet(), 0.5)
        associationRule2 = AssociationRule(body1, ItemSet(), 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(body2, ItemSet(), 0.5)
        associationRule2 = AssociationRule(body1, ItemSet(), 0.5)
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesPreferSimpleHead() {
        val head1 = ItemSet<NamedItem>()
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("a"))
        var associationRule1 = AssociationRule(ItemSet(), head1, 0.5)
        var associationRule2 = AssociationRule(ItemSet(), head2, 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferSimpleHead()
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(ItemSet(), head1, 0.5)
        associationRule2 = AssociationRule(ItemSet(), head1, 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(ItemSet(), head2, 0.5)
        associationRule2 = AssociationRule(ItemSet(), head1, 0.5)
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testTieBreakingForAssociationRulesPreferComplexHead() {
        val head1 = ItemSet<NamedItem>()
        val head2 = ItemSet<NamedItem>()
        head2.add(NamedItem("a"))
        var associationRule1 = AssociationRule(ItemSet(), head1, 0.5)
        var associationRule2 = AssociationRule(ItemSet(), head2, 0.5)
        val tieBreaker = TieBreaker.forAssociationRules().preferComplexHead()
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(ItemSet(), head1, 0.5)
        associationRule2 = AssociationRule(ItemSet(), head1, 0.5)
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2).toLong())
        associationRule1 = AssociationRule(ItemSet(), head2, 0.5)
        associationRule2 = AssociationRule(ItemSet(), head1, 0.5)
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testCustomTieBreakingForAssociationRules() {
        val associationRule1 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val associationRule2 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val comparator = Comparator<AssociationRule<*>> { _, _ -> 1 }
        val tieBreaker = TieBreaker.forAssociationRules().custom(comparator)
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

    @Test
    fun testMultipleTieBreakingStrategiesForAssociationRules() {
        val associationRule1 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val associationRule2 = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        val comparator1 = Comparator<AssociationRule<*>> { _, _ -> 0 }
        val comparator2 = Comparator<AssociationRule<*>> { _, _ -> -1 }
        val tieBreaker = TieBreaker.forAssociationRules().custom(comparator1).custom(comparator2)
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2).toLong())
    }

}
