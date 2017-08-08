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

import de.mrapp.apriori.Sorting.Order;
import de.mrapp.apriori.metrics.Confidence;
import de.mrapp.apriori.metrics.Leverage;
import de.mrapp.apriori.metrics.Lift;
import de.mrapp.apriori.metrics.Support;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Tests the functionality of the class {@link RuleSet}.
 *
 * @author Michael Rapp
 */
public class RuleSetTest {

    /**
     * Tests, if all class members are initialized correctly by the constructor, which expects a
     * comparator as a parameter.
     */
    @Test
    public final void testConstructorWithComparatorParameter() {
        Comparator<AssociationRule> comparator = Sorting.forAssociationRules();
        RuleSet<NamedItem> ruleSet = new RuleSet<>(comparator);
        assertTrue(ruleSet.isEmpty());
        assertEquals(0, ruleSet.size());
        assertEquals(comparator, ruleSet.comparator());
    }

    /**
     * Tests, if all class members are initialized correctly by the constructor, which excepts a
     * collection and a comparator as parameters.
     */
    @Test
    public final void testConstructorWithCollectionAndComparatorParameters() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        Collection<AssociationRule<NamedItem>> collection = new LinkedList<>();
        collection.add(associationRule1);
        collection.add(associationRule2);
        RuleSet<NamedItem> ruleSet = new RuleSet<>(collection, Sorting.forAssociationRules());
        assertEquals(collection.size(), ruleSet.size());
        Iterator<AssociationRule<NamedItem>> iterator = ruleSet.iterator();
        assertEquals(associationRule2, iterator.next());
        assertEquals(associationRule1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a collection and a comparator as parameters, if the collection is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithCollectionAndComparatorParametersThrowsException() {
        new RuleSet<>(null, null);
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
        RuleSet<NamedItem> ruleSet = new RuleSet<>(Sorting.forAssociationRules());
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        assertEquals(associationRule1, ruleSet.first());
        assertEquals(associationRule2, ruleSet.last());
        Sorting<AssociationRule> sorting = Sorting.forAssociationRules().withOrder(Order.ASCENDING);
        RuleSet<NamedItem> sortedRuleSet = ruleSet.sort(sorting);
        assertEquals(associationRule2, sortedRuleSet.first());
        assertEquals(associationRule1, sortedRuleSet.last());
    }

    /**
     * Tests the functionality of the method, which allows to filter the rules of a rule set.
     */
    @Test
    public final void testFilter() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("b"));
        body2.setSupport(0.7);
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, new ItemSet<>(),
                0.4);
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, new ItemSet<>(),
                0.5);
        RuleSet<NamedItem> ruleSet = new RuleSet<>(Sorting.forAssociationRules());
        ruleSet.add(associationRule1);
        ruleSet.add(associationRule2);
        assertEquals(associationRule1, ruleSet.first());
        assertEquals(associationRule2, ruleSet.last());
        Filter<AssociationRule> filter = Filter.forAssociationRules()
                .byOperator(new Support(), 0.5);
        RuleSet<NamedItem> filteredRuleSet = ruleSet.filter(filter);
        assertEquals(1, filteredRuleSet.size());
        assertEquals(associationRule2, filteredRuleSet.first());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the method, which allows to
     * filter the rules of a rule set, if the given predicate is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testFilterThrowsExceptionIfPredicateIsNull() {
        new RuleSet<>(null).filter(null);
    }

    /**
     * Tests the functionality of the clone-method.
     */
    @Test
    public final void testClone() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        body2.setSupport(0.6);
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.5);
        RuleSet<NamedItem> ruleSet1 = new RuleSet<>(Sorting.forAssociationRules());
        ruleSet1.add(associationRule1);
        ruleSet1.add(associationRule2);
        RuleSet<NamedItem> ruleSet2 = ruleSet1.clone();
        assertEquals(ruleSet1.size(), ruleSet2.size());
        assertEquals(ruleSet1.first(), ruleSet2.first());
        assertEquals(ruleSet1.last(), ruleSet2.last());
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
        RuleSet<NamedItem> ruleSet = new RuleSet<>(Sorting.forAssociationRules());
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
        assertEquals("[]", new RuleSet<>(null).toString());
    }

}