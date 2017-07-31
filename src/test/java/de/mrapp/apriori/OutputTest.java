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

import de.mrapp.apriori.Apriori.Configuration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the functionality of the class {@link Output}.
 *
 * @author Michael Rapp
 */
public class OutputTest {

    /**
     * Tests, if all class members are set correctly by the constructor.
     */
    @Test
    public final void testConstructor() {
        Configuration configuration = new Configuration();
        long startTime = 0;
        long endTime = 2;
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        frequentItemSets.add(new ItemSet<>());
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ruleSet.add(new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5));
        Output<NamedItem> output = new Output<>(configuration, startTime, endTime, frequentItemSets,
                ruleSet);
        assertEquals(configuration, output.getConfiguration());
        assertEquals(startTime, output.getStartTime());
        assertEquals(endTime, output.getEndTime());
        assertEquals(endTime - startTime, output.getRuntime());
        assertEquals(frequentItemSets, output.getFrequentItemSets());
        assertEquals(ruleSet, output.getRuleSet());
    }


    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the configuration, which is
     * passed to the constructor, is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenConfigurationIsNull() {
        new Output<>(null, 0, 1, new FrequentItemSets<>(null), null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the start time, which is
     * passed to the constructor, is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenStartTimeIsLessThanZero() {
        new Output<>(new Configuration(), -1, 0, new FrequentItemSets<>(null), null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the end time, which is passed
     * to the constructor, is less than the start time.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenEndTimeIsLessThanStartTime() {
        new Output<>(new Configuration(), 1, 0, new FrequentItemSets<>(null), null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the frequent item sets, which
     * are passed to the constructor, are null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenFrequentItemSetsAreNull() {
        new Output<>(new Configuration(), 0, 1, null, null);
    }

    /**
     * Tests the functionality of the clone-method.
     */
    @Test
    public final void testClone() {
        Configuration configuration = new Configuration();
        long startTime = 0;
        long endTime = 2;
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        frequentItemSets.add(new ItemSet<>());
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ruleSet.add(new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5));
        Output<NamedItem> output1 = new Output<>(configuration, startTime, endTime,
                frequentItemSets,
                ruleSet);
        Output<NamedItem> output2 = output1.clone();
        assertEquals(output1, output2);
    }

    /**
     * Tests the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        Configuration configuration = new Configuration();
        long startTime = 0;
        long endTime = 2;
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.add(new NamedItem("a"));
        itemSet1.setSupport(0.5);
        frequentItemSets.add(itemSet1);
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.add(new NamedItem("b"));
        itemSet2.setSupport(0.6);
        ItemSet<NamedItem> itemSet3 = new ItemSet<>();
        itemSet3.add(new NamedItem("c"));
        itemSet3.setSupport(0.8);
        AssociationRule<NamedItem> rule = new AssociationRule<>(itemSet2, itemSet3, 0.5);
        ruleSet.add(rule);
        Output<NamedItem> output = new Output<>(configuration, startTime, endTime, frequentItemSets,
                ruleSet);
        assertEquals("configuration=" + configuration.toString() + ",\nstartTime=" + startTime +
                ",\nendTime=" + endTime + ",\nruntime=" + output.getRuntime() +
                ",\nfrequentItemSets=" +
                FrequentItemSets.formatFrequentItemSets(frequentItemSets) + ",\nruleSet=" +
                ruleSet.toString(), output.toString());
    }

    /**
     * Tests the functionality of the hashCode-method.
     */
    @Test
    public final void testHashCode() {
        Output<NamedItem> output1 = new Output<>(new Configuration(), 0, 2,
                new FrequentItemSets<>(null), null);
        Output<NamedItem> output2 = new Output<>(new Configuration(), 0, 2,
                new FrequentItemSets<>(null), null);
        assertEquals(output1.hashCode(), output1.hashCode());
        assertEquals(output1.hashCode(), output2.hashCode());
        Configuration configuration = new Configuration();
        configuration.setMinSupport(0.2);
        output1 = new Output<>(configuration, 0, 2, new FrequentItemSets<>(null), null);
        assertNotSame(output1.hashCode(), output2.hashCode());
        output1 = new Output<>(new Configuration(), 1, 2, new FrequentItemSets<>(null),
                null);
        assertNotSame(output1.hashCode(), output2.hashCode());
        output1 = new Output<>(new Configuration(), 0, 1, new FrequentItemSets<>(null),
                null);
        assertNotSame(output1.hashCode(), output2.hashCode());
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        frequentItemSets.add(new ItemSet<>());
        output1 = new Output<>(new Configuration(), 0, 2, frequentItemSets, null);
        assertNotSame(output1.hashCode(), output2.hashCode());
        output1 = new Output<>(new Configuration(), 0, 2, new FrequentItemSets<>(null),
                new RuleSet<>(null));
        assertNotSame(output1.hashCode(), output2.hashCode());
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ruleSet.add(new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5));
        output1 = new Output<>(new Configuration(), 0, 2, new FrequentItemSets<>(null),
                ruleSet);
        assertNotSame(output1.hashCode(), output2.hashCode());
    }

    /**
     * Tests the functionality of the equals-method.
     */
    @Test
    public final void testEquals() {
        Output<NamedItem> output1 = new Output<>(new Configuration(), 0, 2,
                new FrequentItemSets<>(null), null);
        Output<NamedItem> output2 = new Output<>(new Configuration(), 0, 2,
                new FrequentItemSets<>(null), null);
        assertFalse(output1.equals(null));
        assertFalse(output1.equals(new Object()));
        assertTrue(output1.equals(output1));
        assertTrue(output1.equals(output2));
        Configuration configuration = new Configuration();
        configuration.setMinSupport(0.2);
        output1 = new Output<>(configuration, 0, 2, new FrequentItemSets<>(null), null);
        assertFalse(output1.equals(output2));
        output1 = new Output<>(new Configuration(), 1, 2, new FrequentItemSets<>(null),
                null);
        assertFalse(output1.equals(output2));
        output1 = new Output<>(new Configuration(), 0, 1, new FrequentItemSets<>(null),
                null);
        assertFalse(output1.equals(output2));
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        frequentItemSets.add(new ItemSet<>());
        output1 = new Output<>(new Configuration(), 0, 2, frequentItemSets, null);
        assertFalse(output1.equals(output2));
        output1 = new Output<>(new Configuration(), 0, 2, new FrequentItemSets<>(null),
                new RuleSet<>(null));
        assertFalse(output1.equals(output2));
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ruleSet.add(new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5));
        output1 = new Output<>(new Configuration(), 0, 2, new FrequentItemSets<>(null),
                ruleSet);
        assertFalse(output1.equals(output2));
    }

}