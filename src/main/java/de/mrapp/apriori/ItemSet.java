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
package de.mrapp.apriori;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

import static de.mrapp.util.Condition.*;

/**
 * An item set, which may contain several items. An item set may not contain duplicates. The
 * contained items are ordered according to the implementation of their {@link
 * Comparable#compareTo(Object)} method.
 *
 * @param <ItemType> The type of the items, which are contained by the item set
 * @author Michael Rapp
 * @since 1.0.0
 */
public class ItemSet<ItemType> implements SortedSet<ItemType>, Comparable<ItemSet<ItemType>>,
        Serializable, Cloneable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A sorted set, which contains the items, which are contained by the item set.
     */
    private final SortedSet<ItemType> items;

    /**
     * The support of the item set.
     */
    private double support;

    /**
     * Creates an empty item set.
     */
    public ItemSet() {
        this.items = new TreeSet<>();
        setSupport(0);
    }

    /**
     * Creates a new item set by copying another item set.
     *
     * @param itemSet The item set, which should be copied, as an instance of the class {@link
     *                ItemSet}. The item set may not be null
     */
    public ItemSet(@NotNull final ItemSet<ItemType> itemSet) {
        ensureNotNull(itemSet, "The item set may not be null");
        this.items = new TreeSet<>(itemSet.items);
        setSupport(itemSet.support);
    }

    /**
     * Returns the support of the item set.
     *
     * @return The support of the item set as a {@link Double} value. The support must be at least 0
     * and at maximum 1
     */
    public final double getSupport() {
        return support;
    }

    /**
     * Sets the support of the item set.
     *
     * @param support The support, which should be set, as a {@link Double} value. The support must
     *                be at least 0 and at maximum 1
     */
    public final void setSupport(final double support) {
        ensureAtLeast(support, 0, "The support must be at least 0");
        ensureAtMaximum(support, 1, "The support must be at least 1");
        this.support = support;
    }

    @Nullable
    @Override
    public final Comparator<? super ItemType> comparator() {
        return items.comparator();
    }

    @NotNull
    @Override
    public final SortedSet<ItemType> subSet(final ItemType fromElement, final ItemType toElement) {
        return items.subSet(fromElement, toElement);
    }

    @NotNull
    @Override
    public final SortedSet<ItemType> headSet(final ItemType toElement) {
        return items.headSet(toElement);
    }

    @NotNull
    @Override
    public final SortedSet<ItemType> tailSet(final ItemType fromElement) {
        return items.tailSet(fromElement);
    }

    @Override
    public final ItemType first() {
        return items.first();
    }

    @Override
    public final ItemType last() {
        return items.last();
    }

    @Override
    public final int size() {
        return items.size();
    }

    @Override
    public final boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public final boolean contains(final Object o) {
        return items.contains(o);
    }

    @NotNull
    @Override
    public final Iterator<ItemType> iterator() {
        return items.iterator();
    }

    @NotNull
    @Override
    public final Object[] toArray() {
        return items.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @NotNull
    @Override
    public final <T> T[] toArray(@NotNull final T[] a) {
        return items.toArray(a);
    }

    @Override
    public final boolean add(final ItemType item) {
        return items.add(item);
    }

    @Override
    public final boolean remove(final Object o) {
        return items.remove(o);
    }

    @Override
    public final boolean containsAll(@NotNull final Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public final boolean addAll(@NotNull final Collection<? extends ItemType> c) {
        return items.addAll(c);
    }

    @Override
    public final boolean retainAll(@NotNull final Collection<?> c) {
        return items.retainAll(c);
    }

    @Override
    public final boolean removeAll(@NotNull final Collection<?> c) {
        return items.removeAll(c);
    }

    @Override
    public final void clear() {
        items.clear();
    }

    @Override
    public final int compareTo(@NotNull final ItemSet<ItemType> o) {
        return Double.compare(support, o.getSupport());
    }

    @Override
    public final ItemSet<ItemType> clone() {
        return new ItemSet<>(this);
    }

    @Override
    public final String toString() {
        return items.toString();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + items.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemSet<?> other = (ItemSet<?>) obj;
        return items.equals(other.items);
    }

}