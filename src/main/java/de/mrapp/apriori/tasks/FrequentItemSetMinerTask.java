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

import de.mrapp.apriori.Apriori.Configuration;
import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Transaction;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A task, which tries to find a specific number of frequent item sets.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class FrequentItemSetMinerTask<ItemType extends Item> {

    /**
     * The configuration, which is used by the task.
     */
    private final Configuration configuration;

    /**
     * Creates a new task, which tries to find a specific number of frequent item sets.
     *
     * @param configuration The configuration, which is used by the taks, as an instance of the
     *                      class {@link Configuration}. The configuration may not be null
     */
    public FrequentItemSetMinerTask(@NotNull final Configuration configuration) {
        ensureNotNull(configuration, "The configuration may not be null");
        this.configuration = configuration;
    }

    /**
     * Tries to find a specific number of frequent item sets.
     *
     * @param iterator An iterator, which allows to iterate the transactions of the data set, which
     *                 should be processed by the algorithm, as an instance of the type {@link
     *                 Iterator}. The iterator may not be null
     * @return A map, which contains the frequent item sets, which have been found, as an instance
     * of the type {@link Map} or an empty map, if no frequent item sets have been found. The map
     * stores instances of the class {@link ItemSet} as values and their hash codes as the
     * corresponding keys
     */
    @NotNull
    public final Map<Integer, ItemSet<ItemType>> findFrequentItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        FrequentItemSetMiner<ItemType> frequentItemSetMiner;

        if (configuration.getFrequentItemSetCount() > 0) {
            Map<Integer, ItemSet<ItemType>> result = new HashMap<>();
            double currentMinSupport = configuration.getMaxSupport();

            while (result.size() < configuration.getFrequentItemSetCount()) {
                frequentItemSetMiner = new FrequentItemSetMiner<>(currentMinSupport);
                Map<Integer, ItemSet<ItemType>> frequentItemSets = frequentItemSetMiner
                        .findFrequentItemSets(iterator);

                if (frequentItemSets.size() >= result.size()) {
                    result = frequentItemSets;
                }

                currentMinSupport -= configuration.getSupportDelta();
            }

            return result;
        } else {
            frequentItemSetMiner = new FrequentItemSetMiner<>(configuration.getMinSupport());
            return frequentItemSetMiner.findFrequentItemSets(iterator);
        }
    }

}