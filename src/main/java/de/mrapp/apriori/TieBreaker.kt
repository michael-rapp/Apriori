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

/**
 * A tie-breaking strategy, which allows to decide, which one of two item sets or association rules
 * should be sorted before the other one, if their heuristic values are equal.
 *
 * @param T The type of the items, the tie-breaking strategy applies to
 * @author Michael Rapp
 * @since 1.1.0
 */
interface TieBreaker<T> : Comparator<T> {

    companion object {

        /**
         * Creates and returns a tie-breaking strategy, which applies to item sets. By default, no
         * tie-breaking is performed at all.
         */
        fun forItemSets() = ItemSetTieBreaker(Comparator { _, _ -> 0 })

        /**
         * Creates and returns a tie-breaking strategy, which applies to association rules. By
         * default, no tie-breaking is performed at all.
         */
        fun forAssociationRules() = AssociationRuleTieBreaker(Comparator { _, _ -> 0 })

    }

    /**
     * An abstract base class for all tie-breaking strategies.
     *
     * @param    T              The type of the items, the tie-breaking strategy applies to
     * @param    TieBreakerType The type of the tie-breaking strategy
     * @property parent         The tie-breaking strategy, which is used for tie-breaking
     *                          before applying this tie-breaking strategy
     * @property comparator     The comparator, which specifies which one of two item sets or
     *                          association rules should be sorted before the other one when
     *                          performing tie-breaking
     */
    abstract class AbstractTieBreaker<T, TieBreakerType : TieBreaker<T>>(
            private val comparator: Comparator<T>,
            private val parent: TieBreakerType? = null) : TieBreaker<T> {

        override fun compare(o1: T, o2: T): Int {
            if (parent != null) {
                val result = parent.compare(o1, o2)

                if (result != 0) {
                    return result
                }
            }

            return comparator.compare(o1, o2)
        }

    }

    /**
     * A tie-breaking strategy, which applies to item sets.
     */
    class ItemSetTieBreaker(comparator: Comparator<ItemSet<*>>, parent: ItemSetTieBreaker? = null) :
            AbstractTieBreaker<ItemSet<*>, ItemSetTieBreaker>(comparator, parent) {

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers small
         * item sets over large ones. I.e., if an item set contains less items than its counterpart,
         * it is sorted before the other one.
         */
        fun preferSmall() =
                ItemSetTieBreaker(Comparator { o1, o2 -> o2.size.compareTo(o1.size) }, this)

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers large
         * item sets over small ones. I.e., if an item set contains more items than its counterpart,
         * it is sorted before the other one.
         */
        fun preferLarge() =
                ItemSetTieBreaker(Comparator { o1, o2 -> o1.size.compareTo(o2.size) }, this)

        /**
         * Adds an additional custom criteria to the current tie-breaking strategy. The given
         * comparator must return 1, if the first association rule should be preferred, -1, if the
         * second one should be preferred or 0, if no decision could be made.
         *
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when
         *                   performing tie-breaking
         */
        fun custom(comparator: Comparator<ItemSet<*>>) = ItemSetTieBreaker(comparator, this)

    }

    /**
     * A tie-breaking strategy, which applies to association rules.
     */
    class AssociationRuleTieBreaker(comparator: Comparator<AssociationRule<*>>,
                                    parent: AssociationRuleTieBreaker? = null) :
            AbstractTieBreaker<AssociationRule<*>, AssociationRuleTieBreaker>(comparator, parent) {

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which performs
         * tie-breaking according to a specific [operator]. I.e., if an association rule reaches a
         * greater performance according to the given operator, it is sorted before the other one.
         */
        fun byOperator(operator: Operator): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                operator.evaluate(o1).compareTo(operator.evaluate(o2))
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * association rules over complex ones. I.e., if an association rule contains less items
         * than its counterpart in its body and head, it is sorted before the other one.
         */
        fun preferSimple(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                (o2.body.size + o2.head.size).compareTo(o1.body.size + o1.head.size)
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * association rules over simple ones. I.e., if an association rule contains more items than
         * its counterpart in its body and head, it is sorted before the other one.
         */
        fun preferComplex(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                (o1.body.size + o1.head.size).compareTo(o2.body.size + o2.head.size)
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * bodies over complex ones. I.e., if an association rule contains less items than its
         * counterpart in its body, it is sorted before the other one.
         */
        fun preferSimpleBody(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                o2.body.size.compareTo(o1.body.size)
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * bodies over simple ones. I.e., if an association rule contains more items than its
         * counterpart in its body, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class [AssociationRuleTieBreaker]. The tie breaking-strategy may not be null
         */
        fun preferComplexBody(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                o1.body.size.compareTo(o2.body.size)
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * heads over simple ones. I.e., if an association rule contains less items than its
         * counterpart in its head, it is sorted before the other one.
         */
        fun preferSimpleHead(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                o2.head.size.compareTo(o1.head.size)
            }, this)
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * heads over simple ones. I.e., if an association rule contains more items than its
         * counterpart in its head, it is sorted before the other one.
         */
        fun preferComplexHead(): AssociationRuleTieBreaker {
            return AssociationRuleTieBreaker(Comparator { o1, o2 ->
                o1.head.size.compareTo(o2.head.size)
            }, this)
        }

        /**
         * Adds an additional custom criteria to the current tie-breaking strategy. The given
         * comparator must return 1, if the first association rule should be preferred, -1, if the
         * second one should be preferred or 0, if no decision could be made.
         *
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking
         */
        fun custom(comparator: Comparator<AssociationRule<*>>) =
                AssociationRuleTieBreaker(comparator, this)

    }

}
