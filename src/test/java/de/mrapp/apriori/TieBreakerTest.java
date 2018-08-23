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

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link TieBreaker}.
 *
 * @author Michael Rapp
 */
public class TieBreakerTest {

    /**
     * Tests the functionality of the default tie-breaking strategy for item sets.
     */
    @Test
    public final void testDefaultTieBreakerForItemSets() {
        ItemSet itemSet1 = mock(ItemSet.class);
        ItemSet itemSet2 = mock(ItemSet.class);
        TieBreaker<ItemSet<?>> tieBreaker = TieBreaker.Companion.forItemSets();
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for item sets, which prefers small item
     * sets.
     */
    @Test
    public final void testTieBreakingForItemSetsPreferSmall() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.add(new NamedItem("a"));
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.add(new NamedItem("b"));
        TieBreaker<ItemSet<?>> tieBreaker = TieBreaker.Companion.forItemSets().preferSmall();
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2));
        itemSet1.add(new NamedItem("c"));
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2));
        itemSet2.add(new NamedItem("d"));
        itemSet2.add(new NamedItem("e"));
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for item sets, which prefers large item
     * sets.
     */
    @Test
    public final void testTieBreakingForItemSetsPreferLarge() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.add(new NamedItem("a"));
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.add(new NamedItem("b"));
        TieBreaker<ItemSet<?>> tieBreaker = TieBreaker.Companion.forItemSets().preferLarge();
        assertEquals(0, tieBreaker.compare(itemSet1, itemSet2));
        itemSet1.add(new NamedItem("c"));
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2));
        itemSet2.add(new NamedItem("d"));
        itemSet2.add(new NamedItem("e"));
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the functionality of a custom tie-breaking strategy for item sets.
     */
    @Test
    public final void testCustomTieBreakingForItemSets() {
        ItemSet itemSet1 = mock(ItemSet.class);
        ItemSet itemSet2 = mock(ItemSet.class);
        Comparator<ItemSet<?>> comparator = (o1, o2) -> 1;
        TieBreaker<ItemSet<?>> tieBreaker = TieBreaker.Companion.forItemSets().custom(comparator);
        assertEquals(1, tieBreaker.compare(itemSet1, itemSet2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the <code>custom</code>-method
     * of a tie-breaking strategy for item sets, if the comparator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCustomTieBreakingForItemSetsThrowsExceptionIfFunctionIsNull() {
        TieBreaker.Companion.forItemSets().custom(null);
    }

    /**
     * Tests the functionality of a multiple tie-breaking strategies for item sets.
     */
    @Test
    public final void testMultipleTieBreakingStrategiesForItemSets() {
        ItemSet itemSet1 = mock(ItemSet.class);
        ItemSet itemSet2 = mock(ItemSet.class);
        Comparator<ItemSet<?>> comparator1 = (o1, o2) -> 0;
        Comparator<ItemSet<?>> comparator2 = (o1, o2) -> -1;
        TieBreaker<ItemSet<?>> tieBreaker = TieBreaker.Companion.forItemSets().custom(comparator1)
                .custom(comparator2);
        assertEquals(-1, tieBreaker.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the functionality of the default tie-breaking strategy for association rules.
     */
    @Test
    public final void testDefaultTieBreakerForAssociationRules() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules();
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for association rules, which uses a
     * specific operator.
     */
    @Test
    public final void testTieBreakingForAssociationRulesByOperator() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        Operator operator = mock(Operator.class);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .byOperator(operator);
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(1.0, 0.0);
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(0.5, 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(0.0, 1.0);
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the
     * <code>byOperator</code>-method, if the operator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testByOperatorThrowsExceptionIfOperatorIsNull() {
        TieBreaker.Companion.forAssociationRules().byOperator(null);
    }

    /**
     * Tests the functionality of the tie-breaking strategy for association rules, which prefers
     * simple rules.
     */
    @Test
    public final void testTieBreakingForAssociationRulesPreferSimple() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules().preferSimple();
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, head2, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for association rules, which prefers
     * simple rules.
     */
    @Test
    public final void testTieBreakingForAssociationRulesPreferComplex() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .preferComplex();
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, head2, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for association rules, which prefers
     * simple bodies.
     */
    @Test
    public final void testTieBreakingPreferSimpleBody() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .preferSimpleBody();
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy for association rules, which prefers
     * complex bodies.
     */
    @Test
    public final void testTieBreakingForAssociationRulesPreferComplexBody() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .preferComplexBody();
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
    }


    /**
     * Tests the functionality of the tie-breaking strategy, which prefers simple heads.
     */
    @Test
    public final void testTieBreakingForAssociationRulesPreferSimpleHead() {
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(new ItemSet<>(), head1,
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(new ItemSet<>(), head2,
                0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .preferSimpleHead();
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head2, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers complex heads.
     */
    @Test
    public final void testTieBreakingForAssociationRulesPreferComplexHead() {
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(new ItemSet<>(), head1,
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(new ItemSet<>(), head2,
                0.5);
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .preferComplexHead();
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(0, tieBreaker.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head2, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of a custom tie-breaking strategy for association rules.
     */
    @Test
    public final void testCustomTieBreakingForAssociationRules() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        Comparator<AssociationRule<?>> comparator = (o1, o2) -> 1;
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .custom(comparator);
        assertEquals(1, tieBreaker.compare(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the <code>custom</code>-method
     * of a tie-breaking strategy for association rules, if the comparator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCustomTieBreakingForAssociationRulesThrowsExceptionIfFunctionIsNull() {
        TieBreaker.Companion.forAssociationRules().custom(null);
    }

    /**
     * Tests the functionality of a multiple tie-breaking strategies for association rules.
     */
    @Test
    public final void testMultipleTieBreakingStrategiesForAssociationRules() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        Comparator<AssociationRule<?>> comparator1 = (o1, o2) -> 0;
        Comparator<AssociationRule<?>> comparator2 = (o1, o2) -> -1;
        TieBreaker<AssociationRule<?>> tieBreaker = TieBreaker.Companion.forAssociationRules()
                .custom(comparator1).custom(comparator2);
        assertEquals(-1, tieBreaker.compare(associationRule1, associationRule2));
    }

}