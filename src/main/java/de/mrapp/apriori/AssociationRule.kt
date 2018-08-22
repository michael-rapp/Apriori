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

import de.mrapp.util.Condition.ensureAtLeast
import de.mrapp.util.Condition.ensureAtMaximum
import java.io.Serializable

/**
 * An association rule of the form X -&gt; Y, which consists of a body X and a head Y. Both, the
 * body and the head of an association rule consist of one or several items. These item sets must be
 * distinct. An association rule specifies, that if the items, which are contained by its body,
 * occur in a transaction, the items, which are given in its head, do also occur with a certain
 * probability.
 *
 * @param ItemType   The type of the items, the association rule's body and head consist of
 * @property body    The body of the association rule
 * @property head    The head of the association rule
 * @property support The support of the association rule
 * @author Michael Rapp
 * @since 1.0.0
 */
data class AssociationRule<ItemType : Item>(val body: ItemSet<ItemType>,
                                            val head: ItemSet<ItemType>,
                                            val support: Double) : Comparable<AssociationRule<*>>, Serializable {

    init {
        ensureAtLeast(support, 0.0, "The support must be at least 0")
        ensureAtMaximum(support, 1.0, "The support must be at maximum 1")
    }

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param items An array, which contains the items, which should be checked
     */
    @SafeVarargs
    fun covers(vararg items: ItemType): Boolean {
        return covers(items.iterator())
    }

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param items An iterable, which allows to iterate the items, which should be checked
     */
    fun covers(items: Iterable<ItemType>): Boolean = covers(items.iterator())

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param iterator An iterator, which allows to iterate the items, which should be checked
     */
    fun covers(iterator: Iterator<ItemType>): Boolean {
        for (bodyItem in body) {
            var contains = false

            while (iterator.hasNext()) {
                if (bodyItem == iterator.next()) {
                    contains = true
                    break
                }
            }

            if (!contains) {
                return false
            }
        }

        return true
    }

    override fun compareTo(other: AssociationRule<*>) = support.compareTo(other.support)

}
