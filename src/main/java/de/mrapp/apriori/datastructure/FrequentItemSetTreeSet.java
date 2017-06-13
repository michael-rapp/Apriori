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
package de.mrapp.apriori.datastructure;

import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A tree set, which contains frequent item sets.
 *
 * @param <ItemType> The type of the items, which are contained by the frequent item sets
 * @author Michael Rapp
 * @since 1.0.0
 */
public class FrequentItemSetTreeSet<ItemType extends Item> extends
        TreeSet<ItemSet<ItemType>> implements Cloneable {

    /**
     * Creates a new tree set, which contains frequent item sets.
     *
     * @param comparator The comparator, which should be used to sort the set, as as an instance of
     *                   the type {@link Comparator} or null, if the natural ordering should be
     *                   used
     */
    public FrequentItemSetTreeSet(
            @Nullable final Comparator<? super ItemSet<ItemType>> comparator) {
        super(comparator);
    }

    /**
     * Creates and returns a string, which contains information about the frequent item set, which
     * have been found by the algorithm.
     *
     * @param <T>              The type of the items, which are contained by the item sets
     * @param frequentItemSets A collection, which contains the frequent item sets, as an instance
     *                         of the type {@link Collection} or null, if no frequent item sets have
     *                         been found by the algorithm
     * @return A string, which contains information about the given frequent item sets, as a {@link
     * String}. The string may neither be null, nor empty
     */
    @NotNull
    public static <T> String formatFrequentItemSets(
            @NotNull final Collection<? extends ItemSet<T>> frequentItemSets) {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(2);
        Iterator<? extends ItemSet<T>> iterator = frequentItemSets.iterator();
        stringBuilder.append("[");

        while (iterator.hasNext()) {
            ItemSet<T> itemSet = iterator.next();
            stringBuilder.append(itemSet.toString());
            stringBuilder.append(" (support = ");
            stringBuilder.append(decimalFormat.format(itemSet.getSupport()));
            stringBuilder.append(")");

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public FrequentItemSetTreeSet<ItemType> clone() {
        FrequentItemSetTreeSet<ItemType> clone = new FrequentItemSetTreeSet<>(comparator());
        clone.addAll(this);
        return clone;
    }

    @Override
    public final String toString() {
        return formatFrequentItemSets(this);
    }

}