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
package de.mrapp.apriori

import de.mrapp.apriori.datastructure.Filterable
import de.mrapp.apriori.datastructure.Sortable
import de.mrapp.util.datastructure.SortedArraySet
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import java.util.function.Predicate

/**
 * A sorted set, which contains frequent item sets.
 *
 * @param ItemType The type of the items, which are contained by the frequent item sets
 * @author Michael Rapp
 * @since 1.2.0
 */
class FrequentItemSets<ItemType : Item> : SortedArraySet<ItemSet<ItemType>>,
        Sortable<FrequentItemSets<ItemType>, ItemSet<*>>,
        Filterable<FrequentItemSets<ItemType>, ItemSet<*>>, Serializable {

    companion object {

        /**
         * Creates and returns a string, which contains information about the frequent item sets,
         * which have been found by the algorithm.
         *
         * @param T                The type of the items that are contained by the item sets
         * @param frequentItemSets A collection that contains the frequent item sets or null, if no
         *                         frequent item sets have been found by the algorithm
         */
        fun <T> formatFrequentItemSets(frequentItemSets: Collection<ItemSet<T>>): String {
            val stringBuilder = StringBuilder()
            val decimalFormat = DecimalFormat()
            decimalFormat.minimumFractionDigits = 1
            decimalFormat.maximumFractionDigits = 2
            val iterator = frequentItemSets.iterator()
            stringBuilder.append("[")

            while (iterator.hasNext()) {
                val itemSet = iterator.next()
                stringBuilder.append(itemSet.toString())
                stringBuilder.append(" (support = ")
                stringBuilder.append(decimalFormat.format(itemSet.support))
                stringBuilder.append(")")

                if (iterator.hasNext()) {
                    stringBuilder.append(",\n")
                }
            }

            stringBuilder.append("]")
            return stringBuilder.toString()
        }
    }

    /**
     * Creates a new sorted set, which contains frequent item sets.
     *
     * @param comparator The comparator, which should be used to sort the set or null, if the
     *                   natural ordering should be used
     */
    constructor(comparator: Comparator<in ItemSet<ItemType>>?) : super(comparator)

    /**
     * Creates a new sorted set, which contains frequent item sets.
     *
     * @param itemSets   A collection that contains the item sets, which should be added to the
     *                   sorted set
     * @param comparator The comparator, which should be used to sort the set or null, if the
     *                   natural ordering should be used
     */
    constructor(itemSets: Collection<ItemSet<ItemType>>,
                comparator: Comparator<in ItemSet<ItemType>>?) : super(itemSets, comparator)

    override fun sort(comparator: Comparator<in ItemSet<*>>?): FrequentItemSets<ItemType> {
        return FrequentItemSets(this, comparator)
    }

    override fun filter(predicate: Predicate<in ItemSet<*>>): FrequentItemSets<ItemType> {
        val filteredFrequentItemSets = FrequentItemSets(comparator())

        for (itemSet in this) {
            if (predicate.test(itemSet)) {
                filteredFrequentItemSets.add(itemSet)
            }
        }

        return filteredFrequentItemSets
    }

    override fun toString() = formatFrequentItemSets(this)

}
