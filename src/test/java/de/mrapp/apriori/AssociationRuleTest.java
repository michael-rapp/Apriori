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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link AssociationRule}.
 *
 * @author Michael Rapp
 */
public class AssociationRuleTest {

    /**
     * Tests, if all class members are set correctly by the constructor.
     */
    @Test
    public final void testConstructor() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        head.add(new NamedItem("d"));
        double support = 0.5;
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, support);
        assertEquals(body, associationRule.getBody());
        assertEquals(head, associationRule.getHead());
        assertEquals(support, associationRule.getSupport(), 0);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the body, which is passed as
     * a constructor parameter, is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenBodyIsNull() {
        new AssociationRule<>(null, new ItemSet<>(), 0.5);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the head, which is passed as
     * a constructor parameter, is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenHeadIsNull() {
        new AssociationRule<>(new ItemSet<>(), null, 0.5);
    }

    /**
     * Ensures, tha an {@link IllegalArgumentException} is thrown, if the support, which is passed
     * as a constructor parameter, is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenSupportIsLessThanZero() {
        new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), -1);
    }

    /**
     * Ensures, tha an {@link IllegalArgumentException} is thrown, if the support, which is passed
     * as a constructor parameter, is greater than 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenSupportIsGreaterThanOne() {
        new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 1.1);
    }

    /**
     * Tests the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.add(new NamedItem("b"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("c"));
        head.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        assertEquals("[a, b] -> [c, d]", associationRule.toString());
    }

    /**
     * Tests the functionality of the hashCode-method.
     */
    @Test
    public final void testHashCode() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(associationRule1.hashCode(), associationRule1.hashCode());
        assertEquals(associationRule1.hashCode(), associationRule2.hashCode());
        associationRule2 = new AssociationRule<>(body2, head1, 0.5);
        assertNotEquals(associationRule1.hashCode(), associationRule2.hashCode());
        associationRule2 = new AssociationRule<>(body1, head2, 0.5);
        assertNotEquals(associationRule1.hashCode(), associationRule2.hashCode());
    }

    /**
     * Tests the functionality of the equals-method.
     */
    @Test
    public final void testEquals() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        AssociationRule associationRule2 = new AssociationRule<>(body1, head1, 0.5);
        assertFalse(associationRule1.equals(null));
        assertFalse(associationRule1.equals(new Object()));
        assertTrue(associationRule1.equals(associationRule1));
        assertTrue(associationRule1.equals(associationRule2));
        associationRule2 = new AssociationRule<>(body2, head1, 0.5);
        assertFalse(associationRule1.equals(associationRule2));
        associationRule2 = new AssociationRule<>(body1, head2, 0.5);
        assertFalse(associationRule1.equals(associationRule2));
    }

    /**
     * Tests the functionality of the comparator, which allows to compare the heuristic values of
     * two association rules.
     */
    @Test
    public final void testComparator() {
        AssociationRule<?> associationRule1 = mock(AssociationRule.class);
        AssociationRule<?> associationRule2 = mock(AssociationRule.class);
        Operator operator = mock(Operator.class);
        AssociationRule.Comparator comparator = new AssociationRule.Comparator(operator);
        when(operator.evaluate(any())).thenReturn(0.5, 0.6);
        assertEquals(-1, comparator.compare(associationRule1, associationRule2));
        when(operator.evaluate(any())).thenReturn(0.5, 0.5);
        assertEquals(0, comparator.compare(associationRule1, associationRule1));
        when(operator.evaluate(any())).thenReturn(0.6, 0.5);
        assertEquals(1, comparator.compare(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor of the
     * comparator, which allows to compare the heuristic values of two association rules, if null is
     * passed as a constructor parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testComparatorConstructorThrowsException() {
        new AssociationRule.Comparator(null);
    }

}