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
package de.mrapp.apriori;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        Apriori.Configuration configuration = new Apriori.Configuration();
        long startTime = 0;
        long endTime = 2;
        FrequentItemSets<NamedItem> frequentItemSets = new FrequentItemSets<>(null);
        frequentItemSets.add(new ItemSet<>());
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        ruleSet.add(new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5));
        Output<NamedItem> output = new Output<>(configuration, startTime, endTime, frequentItemSets, ruleSet);
        assertEquals(configuration, output.getConfiguration());
        assertEquals(startTime, output.getStartTime());
        assertEquals(endTime, output.getEndTime());
        assertEquals(endTime - startTime, output.getRuntime());
        assertEquals(frequentItemSets, output.getFrequentItemSets());
        assertEquals(ruleSet, output.getRuleSet());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the start time, which is passed to the
     * constructor, is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenStartTimeIsLessThanZero() {
        new Output<>(new Apriori.Configuration(), -1, 0, new FrequentItemSets<NamedItem>(null), null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the end time, which is passed to the constructor,
     * is less than the start time.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsExceptionWhenEndTimeIsLessThanStartTime() {
        new Output<>(new Apriori.Configuration(), 1, 0, new FrequentItemSets<NamedItem>(null), null);
    }

}