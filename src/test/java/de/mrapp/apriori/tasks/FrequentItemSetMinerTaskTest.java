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
package de.mrapp.apriori.tasks;

import de.mrapp.apriori.AbstractDataTest;
import de.mrapp.apriori.Apriori.Configuration;
import de.mrapp.apriori.DataIterator;
import de.mrapp.apriori.NamedItem;
import de.mrapp.apriori.Transaction;
import de.mrapp.apriori.datastructure.TransactionalItemSet;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import de.mrapp.apriori.modules.FrequentItemSetMinerModule;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link FrequentItemSetMinerTask}.
 *
 * @author Michael Rapp
 */
public class FrequentItemSetMinerTaskTest extends AbstractDataTest {

    /**
     * A class, which implements the interface {@link FrequentItemSetMiner} for testing purposes.
     */
    private class FrequentItemSetMinerMock implements FrequentItemSetMiner<NamedItem> {

        /**
         * A list, which contains the minimum supports, which have been passed when invoking
         * the {@link FrequentItemSetMinerMock#findFrequentItemSets(Iterator, double)} method.
         */
        private final List<Double> minSupports = new LinkedList<>();

        @NotNull
        @Override
        public Map<Integer, TransactionalItemSet<NamedItem>> findFrequentItemSets(
                @NotNull final Iterator<Transaction<NamedItem>> iterator, final double minSupport) {
            minSupports.add(minSupport);
            return new HashMap<>();
        }

    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration as a parameter, if the configuration is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationParameterThrowsException() {
        new FrequentItemSetMinerTask<>(null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and a frequent item set miner as parameters, if the configuration is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndFrequentItemSetMinerParameterThrowsExceptionWhenConfigurationIsNull() {
        new FrequentItemSetMinerTask<>(null, new FrequentItemSetMinerModule<>());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and a frequent item set miner as parameters, if the frequent item set miner
     * is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndFrequentItemSetMinerParameterThrowsExceptionWhenFrequentItemSetMinerIsNull() {
        new FrequentItemSetMinerTask<>(mock(Configuration.class), null);
    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent
     * item sets.
     */
    @Test
    public final void testFindFrequentItemSets() {
        double minSupport = 0.5;
        double maxSupport = 0.8;
        double supportDelta = 0.1;
        Configuration configuration = mock(Configuration.class);
        when(configuration.getFrequentItemSetCount()).thenReturn(1);
        when(configuration.getMinSupport()).thenReturn(minSupport);
        when(configuration.getMaxSupport()).thenReturn(maxSupport);
        when(configuration.getSupportDelta()).thenReturn(supportDelta);
        FrequentItemSetMinerMock frequentItemSetMinerMock = new FrequentItemSetMinerMock();
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask = new FrequentItemSetMinerTask<>(
                configuration, frequentItemSetMinerMock);
        File file = getInputFile(INPUT_FILE_1);
        DataIterator dataIterator = new DataIterator(file);
        frequentItemSetMinerTask.findFrequentItemSets(dataIterator);
        assertEquals(Math.round((maxSupport - minSupport) / supportDelta) + 1,
                frequentItemSetMinerMock.minSupports.size(), 0);

        for (int i = 0; i < frequentItemSetMinerMock.minSupports.size(); i++) {
            double support = frequentItemSetMinerMock.minSupports.get(i);
            assertEquals(maxSupport - (i * supportDelta), support, 0.01);
        }
    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent
     * item sets, if no specific number of frequent item sets should be found.
     */
    @Test
    public final void testFindFrequentItemSetsWhenFrequentItemSetCountIsZero() {
        double minSupport = 0.5;
        Configuration configuration = mock(Configuration.class);
        when(configuration.getFrequentItemSetCount()).thenReturn(0);
        when(configuration.getMinSupport()).thenReturn(minSupport);
        FrequentItemSetMinerMock frequentItemSetMinerMock = new FrequentItemSetMinerMock();
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask = new FrequentItemSetMinerTask<>(
                configuration, frequentItemSetMinerMock);
        File file = getInputFile(INPUT_FILE_1);
        DataIterator dataIterator = new DataIterator(file);
        frequentItemSetMinerTask.findFrequentItemSets(dataIterator);
        assertEquals(1, frequentItemSetMinerMock.minSupports.size());
        assertEquals(minSupport, frequentItemSetMinerMock.minSupports.get(0), 0);
    }

}