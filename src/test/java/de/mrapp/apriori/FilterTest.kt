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

import de.mrapp.apriori.metrics.Support
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [Filter].
 *
 * @author Michael Rapp
 */
class FilterTest {

    @Test
    fun testDefaultFilterForItemSets() {
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(NamedItem("a"))
        itemSet.add(NamedItem("b"))
        itemSet.add(NamedItem("c"))
        val filter = Filter.forItemSets()
        assertTrue(filter.invoke(itemSet))
    }

    /**
     * Tests the filter for item sets, when filtering by support.
     */
    @Test
    fun testFilterItemSetsBySupport() {
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(NamedItem("a"))
        itemSet.add(NamedItem("b"))
        itemSet.add(NamedItem("c"))
        itemSet.support = 0.5
        val filter = Filter.forItemSets().bySupport(0.3, 0.8)
        assertTrue(filter.invoke(itemSet))
        itemSet.support = 0.2
        assertFalse(filter.invoke(itemSet))
        itemSet.support = 0.9
        assertFalse(filter.invoke(itemSet))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMinimumSupportIsLessThanZero() {
        Filter.forItemSets().bySupport(-1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMinimumSupportIsGreaterThanOne() {
        Filter.forItemSets().bySupport(2.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMaximumSupportIsGreaterThanOne() {
        Filter.forItemSets().bySupport(0.0, 2.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMaximumSupportIsLessThanMinimumSupport() {
        Filter.forItemSets().bySupport(0.5, 0.4)
    }

    @Test
    fun testFilterForItemSetsBySize() {
        val itemSet = ItemSet<NamedItem>()
        itemSet.add(NamedItem("a"))
        itemSet.support = 0.5
        val filter = Filter.forItemSets().bySize(2, 2)
        assertFalse(filter.invoke(itemSet))
        itemSet.add(NamedItem("b"))
        assertTrue(filter.invoke(itemSet))
        itemSet.add(NamedItem("c"))
        assertFalse(filter.invoke(itemSet))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMinimumSizeIsLessThanZero() {
        Filter.forItemSets().bySize(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForItemSetsThrowsExceptionIfMaximumSizeIsLessThanMinimumSize() {
        Filter.forItemSets().bySize(3, 2)
    }

    @Test
    fun testDefaultFilterForAssociationRules() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("c"))
        val associationRule = AssociationRule(body, head, 0.5)
        val filter = Filter.forAssociationRules()
        assertTrue(filter.invoke(associationRule))
    }

    @Test
    fun testFilterForAssociationRulesByOperator() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        var associationRule = AssociationRule(body, head, 0.5)
        val filter = Filter.forAssociationRules().byOperator(Support(), 0.4, 0.6)
        assertTrue(filter.invoke(associationRule))
        associationRule = AssociationRule(body, head, 0.3)
        assertFalse(filter.invoke(associationRule))
        associationRule = AssociationRule(body, head, 0.7)
        assertFalse(filter.invoke(associationRule))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMinimumPerformanceIsLessThanZero() {
        Filter.forAssociationRules().byOperator(Support(), -1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMaximumPerformanceIsLessThanMinimumPerformance() {
        Filter.forAssociationRules().byOperator(Support(), 0.5, 0.3)
    }

    @Test
    fun testFilterForAssociationRulesBySize() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        val associationRule = AssociationRule(body, head, 0.5)
        val filter = Filter.forAssociationRules().bySize(3, 3)
        assertFalse(filter.invoke(associationRule))
        body.add(NamedItem("c"))
        assertTrue(filter.invoke(associationRule))
        body.add(NamedItem("d"))
        assertFalse(filter.invoke(associationRule))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMinimumSizeIsLessThanZero() {
        Filter.forAssociationRules().bySize(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMaximumSizeIsLessThanMinimumSize() {
        Filter.forAssociationRules().bySize(3, 2)
    }

    @Test
    fun testFilterForAssociationRulesByBodySize() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        val associationRule = AssociationRule(body, head, 0.5)
        val filter = Filter.forAssociationRules().byBodySize(2, 2)
        assertFalse(filter.invoke(associationRule))
        body.add(NamedItem("c"))
        assertTrue(filter.invoke(associationRule))
        body.add(NamedItem("d"))
        assertFalse(filter.invoke(associationRule))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMinimumBodySizeIsLessThanZero() {
        Filter.forAssociationRules().byBodySize(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMaximumBodySizeIsLessThanMinimumBodySize() {
        Filter.forAssociationRules().byBodySize(3, 2)
    }

    @Test
    fun testFilterForAssociationRulesByHeadSize() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        val associationRule = AssociationRule(body, head, 0.5)
        val filter = Filter.forAssociationRules().byHeadSize(2, 2)
        assertFalse(filter.invoke(associationRule))
        head.add(NamedItem("c"))
        assertTrue(filter.invoke(associationRule))
        head.add(NamedItem("d"))
        assertFalse(filter.invoke(associationRule))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMinimumHeadSizeIsLessThanZero() {
        Filter.forAssociationRules().byHeadSize(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterForAssociationRulesThrowsExceptionIfMaximumHeadSizeIsLessThanMinimumHeadSize() {
        Filter.forAssociationRules().byHeadSize(3, 2)
    }

}
