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
package de.mrapp.apriori.tasks

import de.mrapp.apriori.Apriori.Configuration
import de.mrapp.apriori.Item
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.Transaction
import de.mrapp.apriori.datastructure.TransactionalItemSet
import de.mrapp.apriori.modules.FrequentItemSetMiner
import de.mrapp.apriori.modules.FrequentItemSetMinerModule
import java.util.*

/**
 * A task, which tries to find a specific number of frequent item sets.
 *
 * @param    ItemType             The type of the items, which are processed by the algorithm
 * @property configuration        The configuration that is used by the task
 * @property frequentItemSetMiner The frequent item set miner, which should be used by the task
 * @author Michael Rapp
 * @since 1.0.0
 */
class FrequentItemSetMinerTask<ItemType : Item> @JvmOverloads constructor(
        configuration: Configuration,
        private val frequentItemSetMiner: FrequentItemSetMiner<ItemType> = FrequentItemSetMinerModule()) :
        AbstractTask(configuration) {

    /**
     * Tries to find a specific number of frequent item sets.
     *
     * @param iterable An iterable, which allows to iterate the transactions of the data set, which
     *                 should be processed by the algorithm
     * @return A map that contains the frequent item sets, which have been found. The map stores
     * instances of the class [ItemSet] as values and their hash codes as the corresponding keys
     */
    fun findFrequentItemSets(iterable: Iterable<Transaction<ItemType>>):
            Map<Int, TransactionalItemSet<ItemType>> {
        if (configuration.frequentItemSetCount > 0) {
            var result: Map<Int, TransactionalItemSet<ItemType>> = HashMap()
            var currentMinSupport = configuration.maxSupport

            while (currentMinSupport >= configuration.minSupport
                    && result.size < configuration.frequentItemSetCount) {
                val frequentItemSets = frequentItemSetMiner
                        .findFrequentItemSets(iterable, currentMinSupport)

                if (frequentItemSets.size >= result.size) {
                    result = frequentItemSets
                }

                currentMinSupport -= configuration.supportDelta
            }

            return result
        } else {
            return frequentItemSetMiner.findFrequentItemSets(iterable, configuration.minSupport)
        }
    }

}
