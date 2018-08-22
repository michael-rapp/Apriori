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
package de.mrapp.apriori.modules

import de.mrapp.apriori.Item
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.Transaction
import de.mrapp.apriori.datastructure.TransactionalItemSet

/**
 * Defines the interface, a class, which allows to find frequent item sets, must implement.
 *
 * @param ItemType The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
interface FrequentItemSetMiner<ItemType : Item> {

    /**
     * Searches for frequent item sets.
     *
     * @param iterable   An iterable, which allows to iterate the transactions of the data set,
     *                   which should be processed by the algorithm
     * @param minSupport The minimum support, which must at least be reached by an item set to be
     *                   considered frequent. The support must be at least 0 and at maximum 1
     * @return A map, which contains the frequent item sets, which have been found. The map stores
     * instances of the class [ItemSet] as values and their hash codes as the corresponding keys
     */
    fun findFrequentItemSets(iterable: Iterable<Transaction<ItemType>>,
                             minSupport: Double): Map<Int, TransactionalItemSet<ItemType>>

}
