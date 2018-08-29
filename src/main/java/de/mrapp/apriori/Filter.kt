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

import de.mrapp.util.Condition.ensureAtLeast
import de.mrapp.util.Condition.ensureAtMaximum
import java.util.function.Predicate

/**
 * A filter, which can be applied to item sets or association rules.
 *
 * @param T The type of the items, the filter applies to
 * @author Michael Rapp
 * @since 1.2.0
 */
interface Filter<T> : Predicate<T> {

    companion object {

        /**
         * Returns a filter, which applies to item sets. By default, no item sets are filtered at all.
         */
        fun forItemSets() = ItemSetFilter(Predicate { _ -> true }, null)

        /**
         * Returns a filter, which applies to association rules. By default, no association rules are
         * filtered at all.
         */
        fun forAssociationRules() = AssociationRuleFilter(Predicate { _ -> true }, null)

    }

    /**
     * An abstract base class for all filters.
     *
     * @param    T          The type of the items, the filter applies to
     * @param    FilterType The type of the filter
     * @property predicate  The predicate that should be used to test items
     * @property parent     The parent of the filter. When applied to an item, all parents of the
     *                      filter are tested
     */
    abstract class AbstractFilter<T, FilterType : Filter<T>>(protected val predicate: Predicate<T>,
                                                             protected val parent: FilterType? = null) : Filter<T>

    /**
     * A filter, which applies to item sets.
     */
    class ItemSetFilter(predicate: Predicate<ItemSet<*>>, parent: ItemSetFilter? = null) :
            AbstractFilter<ItemSet<*>, ItemSetFilter>(predicate, parent) {

        /**
         * Sets the minimum and maximum support, which must be reached by an item set to be accepted
         * by the filter.
         *
         * @param minSupport The minimum support, which should be set. The minimum support must be
         *                   at least 0
         * @param maxSupport The maximum support, which should be set. The maximum support must be
         *                   at least the minimum support and at maximum
         */
        @JvmOverloads
        fun bySupport(minSupport: Double, maxSupport: Double = 1.0): ItemSetFilter {
            ensureAtLeast(minSupport, 0.0, "The minimum support must be at least 0")
            ensureAtMaximum(minSupport, 1.0, "The minimum support must be at least 1")
            ensureAtMaximum(maxSupport, 1.0, "The maximum support must be at least 1")
            ensureAtLeast(maxSupport, minSupport,
                    "The maximum support must be at least the minimum support")
            return ItemSetFilter(Predicate { x -> x.support in minSupport..maxSupport }, this)
        }

        /**
         * Sets the minimum and maximum size, an item set must have to be accepted by the filter.
         *
         * @param minSize The minimum size, which should be set. The minimum size must be at least 0
         * @param maxSize The maximum size, which should be set. The maximum size must be at least
         *                the minimum size
         */
        @JvmOverloads
        fun bySize(minSize: Int, maxSize: Int = Integer.MAX_VALUE): ItemSetFilter {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0")
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size")
            return ItemSetFilter(Predicate { x -> x.size in minSize..maxSize }, this)
        }

        override fun test(t: ItemSet<*>): Boolean {
            return predicate.test(t) && (parent?.test(t) ?: true)
        }

    }

    /**
     * A filter, which applies to association rules.
     */
    class AssociationRuleFilter(predicate: Predicate<AssociationRule<*>>,
                                parent: AssociationRuleFilter? = null) :
            AbstractFilter<AssociationRule<*>, AssociationRuleFilter>(predicate, parent) {

        /**
         * Sets the minimum and maximum performance according to a specific metric, which must be
         * reached by an association rule to be accepted by the filter.
         *
         * @param operator       The operator, which should be used to calculate the performance of
         *                       an association rule
         * @param minPerformance The minimum performance, which should be set. The minimum
         *                       performance must be at least 0
         * @param maxPerformance The maximum performance, which should be set. The maximum
         *                       performance must be at least the minimum performance
         */
        @JvmOverloads
        fun byOperator(operator: Operator, minPerformance: Double,
                       maxPerformance: Double = Double.MAX_VALUE): AssociationRuleFilter {
            ensureAtLeast(minPerformance, 0.0, "The minimum performance must be at least 0")
            ensureAtLeast(maxPerformance, minPerformance,
                    "The maximum performance must be at least the minimum performance")
            return AssociationRuleFilter(Predicate { x ->
                operator.evaluate(x) in minPerformance..maxPerformance
            }, this)
        }

        /**
         * Sets the minimum and maximum number of items an association rule's body and head must
         * contain to be accepted by the filter.
         *
         * @param minSize The minimum number of items, which should be set. The minimum number of
         *                items must be at least 0
         * @param maxSize The maximum number of items, which should be set. The maximum number of
         *                items must be at least the minimum number of items
         */
        @JvmOverloads
        fun bySize(minSize: Int, maxSize: Int = Integer.MAX_VALUE): AssociationRuleFilter {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0")
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size")
            return AssociationRuleFilter(Predicate { x ->
                (x.body.size + x.head.size) in minSize..maxSize
            }, this)
        }

        /**
         * Sets the minimum and maximum size, an association rule's body must have to be accepted by
         * the filter.
         *
         * @param minSize The minimum size, which should be set. The minimum size must be at least 0
         * @param maxSize The maximum size, which should be set. The maximum size must be at least
         *                the minimum size
         */
        @JvmOverloads
        fun byBodySize(minSize: Int, maxSize: Int = Integer.MAX_VALUE): AssociationRuleFilter {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0")
            ensureAtLeast(maxSize, minSize, "The minimum size must be at least the minimum size")
            return AssociationRuleFilter(Predicate { x ->
                x.body.size in minSize..maxSize
            }, this)
        }

        /**
         * Sets the minimum and maximum size, an association rule's head must have to be accepted by
         * the filter.
         *
         * @param minSize The minimum size, which should be set, as an [Integer] value. The
         * minimum size must be at least 0
         * @param maxSize The maximum size, which should be set, as an [Integer] value. The
         * maximum size must be at least the minimum size
         * @return The filter, this method has been called upon, as an instance of the class [ ]. The filter may not be null
         */
        @JvmOverloads
        fun byHeadSize(minSize: Int, maxSize: Int = Integer.MAX_VALUE): AssociationRuleFilter {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0")
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size")
            return AssociationRuleFilter(Predicate { x ->
                x.head.size in minSize..maxSize
            }, this)
        }

        override fun test(t: AssociationRule<*>): Boolean {
            return predicate.test(t) && (parent?.test(t) ?: true)
        }

    }

}
