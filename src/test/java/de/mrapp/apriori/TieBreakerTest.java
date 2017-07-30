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

import java.util.function.BiFunction;

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
     * Tests the functionality of the default tie-breaking strategy.
     */
    @Test
    public final void testDefaultTieBreaker() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        TieBreaker tieBreaker = new TieBreaker();
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which uses a specific operator.
     */
    @Test
    public final void testTieBreakingByOperator() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        Operator operator = mock(Operator.class);
        TieBreaker tieBreaker = new TieBreaker().byOperator(operator);
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(1.0, 0.0);
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(0.5, 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        when(operator.evaluate(any(AssociationRule.class))).thenReturn(0.0, 1.0);
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the
     * <code>byOperator</code>-method, if the operator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testByOperatorThrowsExceptionIfOperatorIsNull() {
        new TieBreaker().byOperator(null);
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers simple rules.
     */
    @Test
    public final void testTieBreakingPreferSimple() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        TieBreaker tieBreaker = new TieBreaker().preferSimple();
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, head2, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers simple rules.
     */
    @Test
    public final void testTieBreakingPreferComplex() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        TieBreaker tieBreaker = new TieBreaker().preferComplex();
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, head2, 0.5);
        associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers simple bodies.
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
        TieBreaker tieBreaker = new TieBreaker().preferSimpleBody();
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers complex bodies.
     */
    @Test
    public final void testTieBreakingPreferComplexBody() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        TieBreaker tieBreaker = new TieBreaker().preferComplexBody();
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body2, new ItemSet<>(), 0.5);
        associationRule2 = new AssociationRule<>(body1, new ItemSet<>(), 0.5);
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }


    /**
     * Tests the functionality of the tie-breaking strategy, which prefers simple heads.
     */
    @Test
    public final void testTieBreakingPreferSimpleHead() {
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(new ItemSet<>(), head1,
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(new ItemSet<>(), head2,
                0.5);
        TieBreaker tieBreaker = new TieBreaker().preferSimpleHead();
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head2, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of the tie-breaking strategy, which prefers complex heads.
     */
    @Test
    public final void testTieBreakingPreferComplexHead() {
        ItemSet<NamedItem> head1 = new ItemSet<>();
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("a"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(new ItemSet<>(), head1,
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(new ItemSet<>(), head2,
                0.5);
        TieBreaker tieBreaker = new TieBreaker().preferComplexHead();
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(0, tieBreaker.tieBreak(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(new ItemSet<>(), head2, 0.5);
        associationRule2 = new AssociationRule<>(new ItemSet<>(), head1, 0.5);
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Tests the functionality of a custom tie-breaking strategy.
     */
    @Test
    public final void testCustomTieBreaking() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        BiFunction<AssociationRule, AssociationRule, Integer> function = (associationRule, associationRule21) -> 1;
        TieBreaker tieBreaker = new TieBreaker().custom(function);
        assertEquals(1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the
     * <code>custom</code>-method, if the tie-breaking function is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCustomThrowsExceptionIfFunctionIsNull() {
        new TieBreaker().custom(null);
    }

    /**
     * Tests the functionality of a multiple tie-breaking strategies.
     */
    @Test
    public final void testMultipleTieBreakingStrategies() {
        AssociationRule associationRule1 = mock(AssociationRule.class);
        AssociationRule associationRule2 = mock(AssociationRule.class);
        BiFunction<AssociationRule, AssociationRule, Integer> function1 = (associationRule, associationRule21) -> 0;
        BiFunction<AssociationRule, AssociationRule, Integer> function2 = (associationRule, associationRule21) -> -1;
        TieBreaker tieBreaker = new TieBreaker().custom(function1).custom(function2);
        assertEquals(-1, tieBreaker.tieBreak(associationRule1, associationRule2));
    }

}