/*
 * Copyright 2017 - 2019 Michael Rapp
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

import de.mrapp.util.Condition

import java.io.Serializable
import java.util.*

/**
 * An item set, which may contain several items. An item set may not contain duplicates. The
 * contained items are ordered according to the implementation of their [Comparable.compareTo]
 * method.
 *
 * @param ItemType The type of the items, which are contained by the item set
 * @author Michael Rapp
 * @since 1.0.0
 */
open class ItemSet<ItemType> : SortedSet<ItemType>, Comparable<ItemSet<ItemType>>, Serializable {

    /**
     * A sorted set, which contains the items, which are contained by the item set.
     */
    private val items: SortedSet<ItemType>

    /**
     * The support of the item set.
     */
    var support: Double = 0.0
        set(support) {
            Condition.ensureAtLeast(support, 0.0, "The support must be at least 0")
            Condition.ensureAtMaximum(support, 1.0, "The support must be at least 1")
            field = support
        }

    /**
     * Creates an empty item set.
     */
    constructor() {
        this.items = TreeSet()
        this.support = 0.0
    }

    /**
     * Creates a new item set by copying another [itemSet].
     */
    constructor(itemSet: ItemSet<ItemType>) {
        this.items = TreeSet(itemSet.items)
        this.support = itemSet.support
    }

    override val size: Int
        get() = items.size

    override fun comparator(): Comparator<in ItemType>? {
        return items.comparator()
    }

    override fun subSet(fromElement: ItemType, toElement: ItemType): SortedSet<ItemType> {
        return items.subSet(fromElement, toElement)
    }

    override fun headSet(toElement: ItemType): SortedSet<ItemType> {
        return items.headSet(toElement)
    }

    override fun tailSet(fromElement: ItemType): SortedSet<ItemType> {
        return items.tailSet(fromElement)
    }

    override fun first(): ItemType {
        return items.first()
    }

    override fun last(): ItemType {
        return items.last()
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override operator fun contains(element: ItemType): Boolean {
        return items.contains(element)
    }

    override fun iterator(): MutableIterator<ItemType> {
        return items.iterator()
    }

    override fun add(element: ItemType): Boolean {
        return items.add(element)
    }

    override fun remove(element: ItemType): Boolean {
        return items.remove(element)
    }

    override fun containsAll(elements: Collection<ItemType>): Boolean {
        return items.containsAll(elements)
    }

    override fun addAll(elements: Collection<ItemType>): Boolean {
        return items.addAll(elements)
    }

    override fun retainAll(elements: Collection<ItemType>): Boolean {
        return items.retainAll(elements)
    }

    override fun removeAll(elements: Collection<ItemType>): Boolean {
        return items.removeAll(elements)
    }

    override fun clear() {
        items.clear()
    }

    override fun compareTo(other: ItemSet<ItemType>) = support.compareTo(other.support)

    override fun toString() = items.toString()

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + items.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as ItemSet<*>?
        return items == another!!.items
    }

}
