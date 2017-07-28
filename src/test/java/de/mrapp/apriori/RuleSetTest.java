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

import de.mrapp.apriori.metrics.Confidence;
import de.mrapp.apriori.metrics.Leverage;
import de.mrapp.apriori.metrics.Lift;
import de.mrapp.apriori.metrics.Support;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests the functionality of the class {@link RuleSet}.
 *
 * @author Michael Rapp
 */
public class RuleSetTest {

    /**
     * Tests, if all class members are initialized correctly by the default constructor.
     */
    @Test
    public final void testDefaultConstructor() {
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        assertTrue(ruleSet.isEmpty());
        assertEquals(0, ruleSet.size());
    }

    /**
     * Tests, if all class members are initialized correctly by the constructor, which excepts a
     * sorted set as an argument.
     */
    @Test
    public final void testConstructorWithSortedSetArgument() {
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(new ItemSet<>(),
                new ItemSet<>(), 0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(new ItemSet<>(),
                new ItemSet<>(), 0.6);
        SortedSet<AssociationRule<NamedItem>> sortedSet = new TreeSet<>(
                new AssociationRule.Comparator(new Support()));
        sortedSet.add(associationRule1);
        sortedSet.add(associationRule2);
        RuleSet<NamedItem> ruleSet = new RuleSet<>(sortedSet);
        assertEquals(sortedSet.size(), ruleSet.size());
        Iterator<AssociationRule<NamedItem>> iterator = ruleSet.iterator();
        assertEquals(associationRule2, iterator.next());
        assertEquals(associationRule1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a sorted set as an argument, if the sorted set is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithSortedSetArgumentThrowsException() {
        new RuleSet<>(null);
    }

    /**
     * Tests the functionality of the method, which allows to add new rules to a rule set.
     */
    @Test
    public final void testAdd() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        body2.setSupport(0.7);
        ItemSet<NamedItem> body3 = new ItemSet<>();
        body3.add(new NamedItem("c"));
        body3.setSupport(0.9);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule3 = new AssociationRule<>(body3, new ItemSet<>(),
                0.5);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        assertTrue(ruleSet.isEmpty());
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        ruleSet.add(associationRule3);
        ruleSet.add(associationRule3);
        assertEquals(3, ruleSet.size());
        Iterator<AssociationRule<NamedItem>> iterator = ruleSet.iterator();
        assertEquals(associationRule1, iterator.next());
        assertEquals(associationRule2, iterator.next());
        assertEquals(associationRule3, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the functionality of the method, which allows to remove rules from a rule set.
     */
    @Test
    public final void testRemove() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        body2.setSupport(0.7);
        ItemSet<NamedItem> body3 = new ItemSet<>();
        body3.add(new NamedItem("c"));
        body3.setSupport(0.9);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule3 = new AssociationRule<>(body3, new ItemSet<>(),
                0.5);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        ruleSet.add(associationRule3);
        assertEquals(3, ruleSet.size());
        ruleSet.remove(associationRule2);
        ruleSet.remove(associationRule2);
        assertEquals(2, ruleSet.size());
        Iterator<AssociationRule<NamedItem>> iterator = ruleSet.iterator();
        assertEquals(associationRule1, iterator.next());
        assertEquals(associationRule3, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the functionality of the method, which allows to check, whether an item is contained by
     * an item set, or not.
     */
    @Test
    public final void testContains() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.7);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        ruleSet.add(associationRule1);
        assertEquals(1, ruleSet.size());
        assertTrue(ruleSet.contains(associationRule1));
        assertFalse(ruleSet.contains(associationRule2));
        ruleSet.clear();
        assertTrue(ruleSet.isEmpty());
        assertFalse(ruleSet.contains(associationRule1));
        assertFalse(ruleSet.contains(associationRule2));
    }

    /**
     * Tests the functionality of the method, which allows to sort the rules of a rule set.
     */
    @Test
    public final void testSort() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        body2.setSupport(0.7);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        assertEquals(associationRule1, ruleSet.first());
        assertEquals(associationRule2, ruleSet.last());
        RuleSet<NamedItem> sortedRuleSet = ruleSet.sort(new Support());
        assertEquals(associationRule2, sortedRuleSet.first());
        assertEquals(associationRule1, sortedRuleSet.last());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, which allows to
     * sort the rules of a rule set, when null is passed as an argument.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testSortThrowsException() {
        new RuleSet<>().sort(null);
    }

    /**
     * Tests the functionality of the method, which allows to filter the rules of a rule set.
     */
    @Test
    public final void testFilter() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.7);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        assertEquals(2, ruleSet.size());
        RuleSet<NamedItem> filteredRuleSet = ruleSet.filter(new Support(), 0.6);
        assertEquals(1, filteredRuleSet.size());
        assertEquals(associationRule2, filteredRuleSet.first());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, which allows to
     * filter the rules of a rule set, when the operator, which is passed to the method, is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterThrowsExceptionWhenOperatorIsNull() {
        new RuleSet<>().filter(null, 0.5);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, wihch allows to
     * filter the rules of a rule set, when the threshold, which is passed to the method is not
     * greater than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterThrowsExceptionWhenThresholdIsNotGreaterThanZero() {
        new RuleSet<>().filter(mock(Operator.class), 0);
    }

    /**
     * Tests the functionality of the clone-method.
     */
    @Test
    public final void testClone() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(body, head, 0.5);
        RuleSet<NamedItem> ruleSet1 = new RuleSet<>();
        ruleSet1.add(associationRule);
        RuleSet<NamedItem> ruleSet2 = ruleSet1.clone();
        assertEquals(ruleSet1.size(), ruleSet2.size());
        assertEquals(associationRule, ruleSet2.first());
    }

    /**
     * Tests the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        body2.setSupport(0.8);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.7);
        RuleSet<NamedItem> ruleSet = new RuleSet<>();
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(2);
        assertEquals("[" + associationRule1.toString() + " (support = " +
                        decimalFormat.format(new Support().evaluate(associationRule1)) + ", confidence = " +
                        decimalFormat.format(new Confidence().evaluate(associationRule1)) + ", lift = " +
                        decimalFormat.format(new Lift().evaluate(associationRule1)) + ", leverage = " +
                        decimalFormat.format(new Leverage().evaluate(associationRule1)) + "),\n"
                        + associationRule2.toString() + " (support = " +
                        decimalFormat.format(new Support().evaluate(associationRule2)) + ", confidence = " +
                        decimalFormat.format(new Confidence().evaluate(associationRule2)) + ", lift = " +
                        decimalFormat.format(new Lift().evaluate(associationRule2)) + ", leverage = " +
                        decimalFormat.format(new Leverage().evaluate(associationRule2)) + ")]",
                ruleSet.toString());
    }

    /**
     * Tests the functionality of the toString-method, if a rule set is empty.
     */
    @Test
    public final void testToStringIfEmpty() {
        assertEquals("[]", new RuleSet<>().toString());
    }

    /**
     * Tests the functionality of the hashCode-method.
     */
    @Test
    public final void testHashCode() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.7);
        RuleSet<NamedItem> ruleSet1 = new RuleSet<>();
        RuleSet<NamedItem> ruleSet2 = new RuleSet<>();
        assertEquals(ruleSet1.hashCode(), ruleSet1.hashCode());
        assertEquals(ruleSet1.hashCode(), ruleSet2.hashCode());
        ruleSet1.add(associationRule1);
        assertNotEquals(ruleSet1.hashCode(), ruleSet2.hashCode());
        ruleSet2.add(associationRule1);
        assertEquals(ruleSet1.hashCode(), ruleSet2.hashCode());
        ruleSet1.add(associationRule2);
        assertNotEquals(ruleSet1.hashCode(), ruleSet2.hashCode());
    }

    /**
     * Tests the functionality of the equals-method.
     */
    @Test
    public final void testEquals() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.5);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.7);
        RuleSet<NamedItem> ruleSet1 = new RuleSet<>();
        RuleSet<NamedItem> ruleSet2 = new RuleSet<>();
        assertFalse(ruleSet1.equals(null));
        assertFalse(ruleSet1.equals(new Object()));
        assertTrue(ruleSet1.equals(ruleSet1));
        assertTrue(ruleSet1.equals(ruleSet2));
        ruleSet1.add(associationRule1);
        assertFalse(ruleSet1.equals(ruleSet2));
        ruleSet2.add(associationRule1);
        assertTrue(ruleSet1.equals(ruleSet2));
        ruleSet1.add(associationRule2);
        assertFalse(ruleSet1.equals(ruleSet2));
    }

}