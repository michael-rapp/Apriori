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
package de.mrapp.apriori

import de.mrapp.apriori.metrics.Confidence
import de.mrapp.util.Condition
import java.util.*

/**
 * A sorting, which specifies how item sets or association rules should be sorted.
 *
 * @param T The type of the items, the sorting applies to
 * @author Michael Rapp
 * @since 1.2.0
 */
interface Sorting<T> : Comparator<T> {

    companion object {

        /**
         * Creates and returns a sorting for item sets.
         */
        fun forItemSets() = ItemSetSorting()

        /**
         * Creates and returns a sorting for association rules.
         */
        fun forAssociationRules() = AssociationRuleSorting()

    }

    /**
     * Contains all possible orders, which can be used for sorting.
     */
    enum class Order {

        /**
         * If the item sets or association rules should be sorted in ascending order.
         */
        ASCENDING,

        /**
         * If the item sets or association rules should be sorted in descending order.
         */
        DESCENDING

    }

    /**
     * An abstract base class for all sortings.
     *
     * @param T           The type of the items, the sorting applies to
     * @param SortingType The type of the sorting
     */
    abstract class AbstractSorting<T, SortingType : Sorting<T>>(
            protected var order: Order = Order.DESCENDING,
            protected var tieBreaker: Comparator<T>? = null) : Sorting<T> {

        @Suppress("UNCHECKED_CAST")
        protected fun self() = this as SortingType

        /**
         * Sets the [order], which should be used for sorting.
         */
        fun withOrder(order: Order): SortingType {
            Condition.ensureNotNull(order, "The order may not be null")
            this.order = order
            return self()
        }

        /**
         * Sets the [tieBreaker] that should be used.
         */
        fun withTieBreaking(tieBreaker: Comparator<T>?): SortingType {
            this.tieBreaker = tieBreaker
            return self()
        }

    }

    /**
     * A sorting, which applies to item sets.
     */
    class ItemSetSorting : AbstractSorting<ItemSet<*>, ItemSetSorting>() {

        override fun compare(o1: ItemSet<*>, o2: ItemSet<*>): Int {
            var result = o1.support.compareTo(o2.support)

            if (result == 0 && tieBreaker != null) {
                result = tieBreaker!!.compare(o1, o2)
            }

            return if (order == Order.ASCENDING) result else result * -1
        }

    }

    /**
     * A sorting, which applies to association rules.
     */
    class AssociationRuleSorting(private var operator: Operator? = null) :
            AbstractSorting<AssociationRule<*>, AssociationRuleSorting>() {

        init {
            byOperator(Confidence())
        }

        /**
         * Sets the [operator], the association rules should be sorted by.
         */
        fun byOperator(operator: Operator?): AssociationRuleSorting {
            this.operator = operator
            return self()
        }

        override fun compare(o1: AssociationRule<*>, o2: AssociationRule<*>): Int {
            var result = operator?.let { it.evaluate(o1).compareTo(it.evaluate(o2)) }
                    ?: o1.compareTo(o2)

            if (result == 0 && tieBreaker != null) {
                result = tieBreaker!!.compare(o1, o2)
            }

            return if (order == Order.ASCENDING) result else result * -1
        }

    }

}