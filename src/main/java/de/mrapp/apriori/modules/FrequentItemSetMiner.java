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
package de.mrapp.apriori.modules;

import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Transaction;
import de.mrapp.apriori.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 * A module, which allows to find all frequent item sets, which occur in a data set. The data set
 * must contain multiple transactions of which each one consists of multiple items. Item sets are
 * considered frequent, if their support is greater or equal than a specific threshold.
 *
 * In order to search for frequent item sets efficiently, the algorithm exploits the
 * anti-monotonicity property of the support metric, which states, that an item set can only be
 * frequent, if all of its subsets are frequent as well. The other way around, the supersets of an
 * infrequent item set are also infrequent. Furthermore, the items must be sortable (e.g. by their
 * names) in order to generate possible candidates in an efficient way.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class FrequentItemSetMiner<ItemType extends Item> {

    /**
     * An extension of the class {@link ItemSet}, which allows to store the transactions, the item
     * set occurs in.
     *
     * @param <T> The type of the items, which are contained by the item set
     */
    private class InternalItemSet<T extends Item> extends ItemSet<T> {

        /**
         * A map, which contains the transactions, the item set occurs in. The transactions are
         * mapped to unique ids.
         */
        private Map<Integer, Transaction<ItemType>> transactions;

        /**
         * Creates an empty item set.
         */
        InternalItemSet() {
            super();
            this.transactions = new HashMap<>();
        }

        /**
         * Creates a new item set by copying another item set.
         *
         * @param itemSet The item set, which should be copied, as an instance of the class {@link
         *                InternalItemSet}. The item set may not be null
         */
        InternalItemSet(@NotNull final InternalItemSet<T> itemSet) {
            super(itemSet);
            this.transactions = new HashMap<>(itemSet.transactions);
        }

    }

    /**
     * The SLF4J logger, which is used by the module.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FrequentItemSetMiner.class);

    /**
     * The support, which must at least be reached by item sets to be considered frequent.
     */
    private final double minSupport;

    /**
     * Generates and returns item sets, which contain only one item.
     *
     * @param iterator An iterator, which allows to iterate the transactions of the data set, which
     *                 should be processed by the algorithm, as an instance of the type {@link
     *                 Iterator}. The iterator may not be null
     * @return A pair, which contains the generated item sets, as well as the number of
     * transactions, which have been iterated, as an instance of the class {@link Pair}. The pair
     * may not be null
     */
    @NotNull
    private Pair<Collection<InternalItemSet<ItemType>>, Integer> generateInitialItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        Map<Integer, InternalItemSet<ItemType>> itemSets = new HashMap<>();
        int transactionCount = 0;
        Transaction<ItemType> transaction;

        while ((transaction = iterator.next()) != null) {
            for (ItemType item : transaction) {
                InternalItemSet<ItemType> itemSet = new InternalItemSet<>();
                itemSet.add(item);
                InternalItemSet<ItemType> previous = itemSets
                        .putIfAbsent(itemSet.hashCode(), itemSet);

                if (previous != null) {
                    previous.transactions.put(transactionCount, transaction);
                } else {
                    itemSet.transactions.put(transactionCount, transaction);
                }
            }

            transactionCount++;
        }

        return Pair.create(itemSets.values(), transactionCount);
    }

    /**
     * Removes the item sets, which are not frequent, from a specific collection. Calculating the
     * support of the item sets requires to access the data set.
     *
     * @param itemSets         A collection, which contains the item sets, which should be filtered,
     *                         as an instance of the type {@link Collection} or an empty collection,
     *                         if no item sets are available
     * @param transactionCount The total number of transactions, which are contained in the data
     *                         set, as an {@link Integer} value
     * @param k                The length of the item sets, which should be filtered, as an {@link
     *                         Integer} value
     * @return A list, which contains the item sets, which are frequent, as an instance of the type
     * {@link List} or an empty list, if no item sets are frequent
     */
    @NotNull
    private List<InternalItemSet<ItemType>> filterFrequentItemSets(
            @NotNull final Collection<InternalItemSet<ItemType>> itemSets,
            final int transactionCount, final int k) {
        List<InternalItemSet<ItemType>> frequentCandidates = new ArrayList<>(itemSets.size());

        for (InternalItemSet<ItemType> candidate : itemSets) {
            if (k > 1) {
                Set<Map.Entry<Integer, Transaction<ItemType>>> transactionSet = candidate.transactions
                        .entrySet();
                Map<Integer, Transaction<ItemType>> transactions = new HashMap<>(
                        transactionSet.size());

                for (Map.Entry<Integer, Transaction<ItemType>> entry : transactionSet) {
                    Transaction<ItemType> transaction = entry.getValue();
                    int count = 0;

                    for (ItemType item : transaction) {
                        // TODO: What if transaction contains the same item multiple times!?
                        if (candidate.contains(item)) {
                            count++;
                        }
                    }

                    if (count >= candidate.size()) {
                        transactions.put(entry.getKey(), transaction);
                    }
                }

                candidate.transactions = transactions;
            }

            int occurrences = candidate.transactions.size();
            double support = calculateSupport(transactionCount, occurrences);
            candidate.setSupport(support);

            if (support >= minSupport) {
                frequentCandidates.add(candidate);
            }
        }

        return frequentCandidates;
    }

    /**
     * Creates item sets of the length k + 1 by combining item sets of the length k. Two item sets
     * are only combined, if the first k - 1 items of both item sets are equal. Because the items,
     * which are contained by item sets, are sorted, this enables to efficiently generate all
     * possible candidates, without generating any duplicates.
     *
     * @param itemSets A list, which contains the item sets, which should be combined in order to
     *                 create new item sets, as an instance of the type {@link List} or an empty
     *                 list, if no item sets are available
     * @param k        The length of the given item sets as an {@link Integer} value
     * @return A collection, which contains the item sets, which have been created, as an instance
     * of the type {@link Collection} or an empty collection, if no item sets have been created
     */
    @NotNull
    private Collection<InternalItemSet<ItemType>> combineItemSets(
            @NotNull final List<InternalItemSet<ItemType>> itemSets,
            final int k) {
        Set<InternalItemSet<ItemType>> combinedItemSets = new HashSet<>(
                IntStream.range(1, itemSets.size()).reduce(0, (x, y) -> (x + y)), 1);

        for (int i = 0; i < itemSets.size(); i++) {
            for (int j = i + 1; j < itemSets.size(); j++) {
                InternalItemSet<ItemType> itemSet1 = itemSets.get(i);
                InternalItemSet<ItemType> itemSet2 = itemSets.get(j);
                Iterator<ItemType> iterator1 = itemSet1.iterator();
                Iterator<ItemType> iterator2 = itemSet2.iterator();
                ItemType itemToAdd = null;
                int index = 0;

                while (iterator1.hasNext() && iterator2.hasNext()) {
                    ItemType item2 = iterator2.next();

                    if (index < k - 1) {
                        if (!item2.equals(iterator1.next())) {
                            itemToAdd = null;
                            break;
                        }
                    } else {
                        itemToAdd = item2;
                    }

                    index++;
                }

                if (itemToAdd != null) {
                    InternalItemSet<ItemType> combinedItemSet = new InternalItemSet<>(itemSet1);
                    combinedItemSet.add(itemToAdd);
                    combinedItemSet.transactions.putAll(itemSet2.transactions);
                    combinedItemSets.add(combinedItemSet);
                }
            }
        }

        return combinedItemSets;
    }

    /**
     * Creates and returns a string, which contains information about the frequent item set, which
     * have been found by the algorithm.
     *
     * @param frequentItemSets A collection, which contains the frequent item sets, as an instance
     *                         of the type {@link Collection} or null, if no frequent item sets have
     *                         been found by the algorithm
     * @return A string, which contains information about the given frequent item sets, as a {@link
     * String}. The string may neither be null, nor empty
     */
    @NotNull
    private String formatFrequentItemSets(
            @NotNull final Collection<ItemSet<ItemType>> frequentItemSets) {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(2);
        Iterator<ItemSet<ItemType>> iterator = frequentItemSets.iterator();
        stringBuilder.append("[");

        while (iterator.hasNext()) {
            ItemSet<ItemType> itemSet = iterator.next();
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

    /**
     * Calculates and returns the support of an item.
     *
     * @param transactions The total number of available transactions as an {@link Integer} value.
     *                     The number of transactions must be at least 0
     * @param occurrences  The number of transactions, the item occurs in, as an {@link Integer}
     *                     value. The number of transactions must be at least 0
     * @return The support, which has been calculated, as a {@link Double} value
     */
    private double calculateSupport(final int transactions, final int occurrences) {
        return transactions > 0 ? (double) occurrences / (double) transactions : 0;
    }

    /**
     * Creates a new module, which allows to find all frequent item sets, which occur in a data set.
     *
     * @param minSupport The support, which must at least be reached by item sets to be considered
     *                   frequent, as a {@link Double} value. The support must be at least 0 and at
     *                   maximum 1
     */
    public FrequentItemSetMiner(final double minSupport) {
        // TODO: Throw exceptions
        this.minSupport = minSupport;
    }

    /**
     * Returns the support, which must at least be reached by item sets to be considered frequent.
     *
     * @return The support, which must at least be reached by item sets to be considered frequent,
     * as a {@link Double} value. The support must be at least 0 and at maximum 1
     */
    public final double getMinSupport() {
        return minSupport;
    }

    /**
     * Searches for frequent item sets.
     *
     * @param iterator An iterator, which allows to iterate the transactions of the data set, which
     *                 should be processed by the algorithm, as an instance of the type {@link
     *                 Iterator}. The iterator may not be null
     * @return A map, which contains the frequent item sets, which have been found, as an instance
     * of the type {@link Map} or an empty map, if no frequent item sets have been found. The map
     * stores instances of the class {@link ItemSet} as values and their hash codes as the
     * corresponding keys
     */
    @NotNull
    public final Map<Integer, ItemSet<ItemType>> findFrequentItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        // TODO: Throw exceptions
        LOGGER.debug("Searching for frequent item sets");
        Map<Integer, ItemSet<ItemType>> frequentItemSets = new HashMap<>();
        int k = 1;
        Pair<Collection<InternalItemSet<ItemType>>, Integer> pair = generateInitialItemSets(
                iterator);
        Collection<InternalItemSet<ItemType>> candidates = pair.first;
        int transactionCount = pair.second;

        while (!candidates.isEmpty()) {
            LOGGER.trace("k = {}", k);
            LOGGER.trace("C_{} = {}", k, candidates);
            List<InternalItemSet<ItemType>> frequentCandidates = filterFrequentItemSets(candidates,
                    transactionCount,
                    k);
            LOGGER.trace("S_{} = {}", k, frequentCandidates);
            candidates = combineItemSets(frequentCandidates, k);
            frequentCandidates.forEach(x -> frequentItemSets.put(x.hashCode(), x));
            k++;
        }

        LOGGER.debug("Found {} frequent item sets", frequentItemSets.size());
        LOGGER.debug("Frequent item sets = {}", formatFrequentItemSets(frequentItemSets.values()));
        return frequentItemSets;
    }

}