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

import de.mrapp.apriori.*;
import de.mrapp.apriori.datastructure.TransactionalItemSet;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
         * A list, which contains the minimum supports, which have been passed when invoking the {@link
         * FrequentItemSetMinerMock#findFrequentItemSets(Iterable, double)} method.
         */
        private final List<Double> minSupports = new LinkedList<>();

        @NotNull
        @Override
        public Map<Integer, TransactionalItemSet<NamedItem>> findFrequentItemSets(
                @NotNull final Iterable<? extends Transaction<NamedItem>> iterable, final double minSupport) {
            minSupports.add(minSupport);
            return new HashMap<>();
        }

    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent item sets.
     */
    @Test
    public final void testFindFrequentItemSets() {
        double minSupport = 0.5;
        double maxSupport = 0.8;
        double supportDelta = 0.1;
        Apriori.Configuration configuration = new Apriori.Configuration();
        configuration.setFrequentItemSetCount(1);
        configuration.setMinSupport(minSupport);
        configuration.setMaxSupport(maxSupport);
        configuration.setSupportDelta(supportDelta);
        FrequentItemSetMinerMock frequentItemSetMinerMock = new FrequentItemSetMinerMock();
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask =
                new FrequentItemSetMinerTask<>(configuration, frequentItemSetMinerMock);
        File file = getInputFile(INPUT_FILE_1);
        frequentItemSetMinerTask.findFrequentItemSets(() -> new DataIterator(file));
        assertEquals(Math.round((maxSupport - minSupport) / supportDelta) + 1,
                frequentItemSetMinerMock.minSupports.size(), 0);

        for (int i = 0; i < frequentItemSetMinerMock.minSupports.size(); i++) {
            double support = frequentItemSetMinerMock.minSupports.get(i);
            assertEquals(maxSupport - (i * supportDelta), support, 0.01);
        }
    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent item sets, if no
     * specific number of frequent item sets should be found.
     */
    @Test
    public final void testFindFrequentItemSetsWhenFrequentItemSetCountIsZero() {
        double minSupport = 0.5;
        Apriori.Configuration configuration = new Apriori.Configuration();
        configuration.setFrequentItemSetCount(0);
        configuration.setMinSupport(minSupport);
        FrequentItemSetMinerMock frequentItemSetMinerMock = new FrequentItemSetMinerMock();
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask =
                new FrequentItemSetMinerTask<>(configuration, frequentItemSetMinerMock);
        File file = getInputFile(INPUT_FILE_1);
        frequentItemSetMinerTask.findFrequentItemSets(() -> new DataIterator(file));
        assertEquals(1, frequentItemSetMinerMock.minSupports.size());
        assertEquals(minSupport, frequentItemSetMinerMock.minSupports.get(0), 0);
    }

}