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
import de.mrapp.apriori.datastructure.TransactionalItemSet;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import de.mrapp.apriori.modules.FrequentItemSetMinerModule;
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
public class FrequentItemSetMinerTask<ItemType extends Item> extends AbstractTask<ItemType> {

    /**
     * The frequent item set miner, which is used by the task.
     */
    private final FrequentItemSetMiner<ItemType> frequentItemSetMiner;

    /**
     * Creates a new task, which tries to find a specific number of frequent item sets.
     *
     * @param configuration The configuration, which is used by the taks, as an instance of the
     *                      class {@link Configuration}. The configuration may not be null
     */
    public FrequentItemSetMinerTask(@NotNull final Configuration configuration) {
        this(configuration, new FrequentItemSetMinerModule<>());
    }

    /**
     * Creates a new task, which tries to find a specific number of frequent item sets.
     *
     * @param configuration        The configuration, which is used by the taks, as an instance of
     *                             the class {@link Configuration}. The configuration may not be
     *                             null
     * @param frequentItemSetMiner The frequent item set miner, which should be used by the task, as
     *                             an instance of the class {@link FrequentItemSetMiner}. The
     *                             frequent item set miner may not be null
     */
    public FrequentItemSetMinerTask(@NotNull final Configuration configuration,
                                    @NotNull final FrequentItemSetMiner<ItemType> frequentItemSetMiner) {
        super(configuration);
        ensureNotNull(frequentItemSetMiner, "The frequent item set miner may not be null");
        this.frequentItemSetMiner = frequentItemSetMiner;
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
    public final Map<Integer, TransactionalItemSet<ItemType>> findFrequentItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        if (getConfiguration().getFrequentItemSetCount() > 0) {
            Map<Integer, TransactionalItemSet<ItemType>> result = new HashMap<>();
            double currentMinSupport = getConfiguration().getMaxSupport();

            while (currentMinSupport >= getConfiguration().getMinSupport() &&
                    result.size() < getConfiguration().getFrequentItemSetCount()) {
                Map<Integer, TransactionalItemSet<ItemType>> frequentItemSets = frequentItemSetMiner
                        .findFrequentItemSets(iterator, currentMinSupport);

                if (frequentItemSets.size() >= result.size()) {
                    result = frequentItemSets;
                }

                currentMinSupport -= getConfiguration().getSupportDelta();
            }

            return result;
        } else {
            return frequentItemSetMiner
                    .findFrequentItemSets(iterator, getConfiguration().getMinSupport());
        }
    }

}