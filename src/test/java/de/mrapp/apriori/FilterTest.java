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

import de.mrapp.apriori.metrics.Support;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the functionality of the class {@link Filter}.
 *
 * @author Michael Rapp
 */
public class FilterTest {

    /**
     * Tests the default filter for item sets.
     */
    @Test
    public final void testDefaultFilterForItemSets() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(new NamedItem("a"));
        itemSet.add(new NamedItem("b"));
        itemSet.add(new NamedItem("c"));
        Filter<ItemSet> filter = Filter.forItemSets();
        assertTrue(filter.test(itemSet));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the item set is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterThrowsExceptionIfItemSetIsNull() {
        Filter.forItemSets().test(null);
    }

    /**
     * Tests the filter for item sets, when filtering by support.
     */
    @Test
    public final void testFilterItemSetsBySupport() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(new NamedItem("a"));
        itemSet.add(new NamedItem("b"));
        itemSet.add(new NamedItem("c"));
        itemSet.setSupport(0.5);
        Filter<ItemSet> filter = Filter.forItemSets().bySupport(0.3, 0.8);
        assertTrue(filter.test(itemSet));
        itemSet.setSupport(0.2);
        assertFalse(filter.test(itemSet));
        itemSet.setSupport(0.9);
        assertFalse(filter.test(itemSet));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the minimum support is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMinimumSupportIsLessThanZero() {
        Filter.forItemSets().bySupport(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the minimum support is greater than 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMinimumSupportIsGreaterThanOne() {
        Filter.forItemSets().bySupport(2);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the maximum support is greater than 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMaximumSupportIsGreaterThanOne() {
        Filter.forItemSets().bySupport(0, 2);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the maximum support is less than the minimum support.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMaximumSupportIsLessThanMinimumSupport() {
        Filter.forItemSets().bySupport(0.5, 0.4);
    }

    /**
     * Tests the filter for item sets, when filtering by size.
     */
    @Test
    public final void testFilterForItemSetsBySize() {
        ItemSet<NamedItem> itemSet = new ItemSet<>();
        itemSet.add(new NamedItem("a"));
        itemSet.setSupport(0.5);
        Filter<ItemSet> filter = Filter.forItemSets().bySize(2, 2);
        assertFalse(filter.test(itemSet));
        itemSet.add(new NamedItem("b"));
        assertTrue(filter.test(itemSet));
        itemSet.add(new NamedItem("c"));
        assertFalse(filter.test(itemSet));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the minimum size is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMinimumSizeIsLessThanZero() {
        Filter.forItemSets().bySize(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for item sets, if
     * the maximum size is less than the minimum size.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForItemSetsThrowsExceptionIfMaximumSizeIsLessThanMinimumSize() {
        Filter.forItemSets().bySize(3, 2);
    }

    /**
     * Tests the default filter for association rules.
     */
    @Test
    public final void testDefaultFilterForAssociationRules() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        Filter<AssociationRule> filter = Filter.forAssociationRules();
        assertTrue(filter.test(associationRule));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the association rule is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterThrowsExceptionIfAssociationRuleIsNull() {
        Filter.forAssociationRules().test(null);
    }

    /**
     * Tests the filter for association rules, when filtering by an operator.
     */
    @Test
    public final void testFilterForAssociationRulesByOperator() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        Filter<AssociationRule> filter = Filter.forAssociationRules()
                .byOperator(new Support(), 0.4, 0.6);
        assertTrue(filter.test(associationRule));
        associationRule = new AssociationRule<>(body, head, 0.3);
        assertFalse(filter.test(associationRule));
        associationRule = new AssociationRule<>(body, head, 0.7);
        assertFalse(filter.test(associationRule));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the operator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfOperatorIsNull() {
        Filter.forAssociationRules().byOperator(null, 0.5);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the minimum performance is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMinimumPerformanceIsLessThanZero() {
        Filter.forAssociationRules().byOperator(new Support(), -1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the maximum performance is less than the minimum performance.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMaximumPerformanceIsLessThanMinimumPerformance() {
        Filter.forAssociationRules().byOperator(new Support(), 0.5, 0.3);
    }

    /**
     * Tests the filter for association rules, when filtering by size.
     */
    @Test
    public final void testFilterForAssociationRulesBySize() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        Filter<AssociationRule> filter = Filter.forAssociationRules().bySize(3, 3);
        assertFalse(filter.test(associationRule));
        body.add(new NamedItem("c"));
        assertTrue(filter.test(associationRule));
        body.add(new NamedItem("d"));
        assertFalse(filter.test(associationRule));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the minimum size is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMinimumSizeIsLessThanZero() {
        Filter.forAssociationRules().bySize(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the maximum size is less than the minimum size.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMaximumSizeIsLessThanMinimumSize() {
        Filter.forAssociationRules().bySize(3, 2);
    }

    /**
     * Tests the filter for association rules, when filtering by body size.
     */
    @Test
    public final void testFilterForAssociationRulesByBodySize() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        Filter<AssociationRule> filter = Filter.forAssociationRules().byBodySize(2, 2);
        assertFalse(filter.test(associationRule));
        body.add(new NamedItem("c"));
        assertTrue(filter.test(associationRule));
        body.add(new NamedItem("d"));
        assertFalse(filter.test(associationRule));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the minimum body size is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMinimumBodySizeIsLessThanZero() {
        Filter.forAssociationRules().byBodySize(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the maximum body size is less than the minimum size.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMaximumBodySizeIsLessThanMinimumBodySize() {
        Filter.forAssociationRules().byBodySize(3, 2);
    }

    /**
     * Tests the filter for association rules, when filtering by body size.
     */
    @Test
    public final void testFilterForAssociationRulesByHeadSize() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        Filter<AssociationRule> filter = Filter.forAssociationRules().byHeadSize(2, 2);
        assertFalse(filter.test(associationRule));
        head.add(new NamedItem("c"));
        assertTrue(filter.test(associationRule));
        head.add(new NamedItem("d"));
        assertFalse(filter.test(associationRule));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the minimum head size is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMinimumHeadSizeIsLessThanZero() {
        Filter.forAssociationRules().byHeadSize(-1);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the filter for association
     * rules, if the maximum head size is less than the minimum size.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterForAssociationRulesThrowsExceptionIfMaximumHeadSizeIsLessThanMinimumHeadSize() {
        Filter.forAssociationRules().byHeadSize(3, 2);
    }

}