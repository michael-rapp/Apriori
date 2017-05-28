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

import de.mrapp.apriori.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 * An implementation of the Apriori association rule learning algorithm for mining frequent item
 * sets. The algorithm processes a set of transactions, of which each one consists of multiple
 * items, in order to learn association rules.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Apriori<ItemType extends Item> {

    private class ItemSet implements Iterable<ItemType> {

        private final SortedSet<ItemType> items;

        private Map<Integer, Transaction<ItemType>> transactions;

        private double support;

        ItemSet() {
            this.items = new TreeSet<>();
            this.transactions = new HashMap<>();
            this.support = -1;
        }

        ItemSet(@NotNull final ItemSet itemSet) {
            // TODO: Throw exceptions
            this.items = new TreeSet<>(itemSet.items);
            this.transactions = new HashMap<>(itemSet.transactions);
            this.support = itemSet.support;
        }

        @NotNull
        @Override
        public Iterator<ItemType> iterator() {
            return items.iterator();
        }

        @Override
        public String toString() {
            return items.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + items.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ItemSet other = (ItemSet) obj;
            return items.equals(other.items);
        }

    }

    /**
     * The SL4J logger, which is used by the algorithm.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Apriori.class);

    /**
     * The support, which must be reached by the association rules, which are learned by the
     * algorithm, at least.
     */
    private final double minSupport;

    /**
     * The confidence, which must be reached by the association rules, which are learned by the
     * algorithm, at least.
     */
    private final double minConfidence;

    // TODO: Comment
    private Map<Integer, ItemSet> findFrequentItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        Map<Integer, ItemSet> frequentItemSets = new HashMap<>();
        int k = 1;
        Pair<Collection<ItemSet>, Integer> pair = findInitialItemSets(iterator);
        Collection<ItemSet> candidates = pair.first;
        int transactionCount = pair.second;

        while (!candidates.isEmpty()) {
            LOGGER.trace("k = {}", k);
            LOGGER.trace("C_{} = {}", k, candidates);
            List<ItemSet> frequentCandidates = filterFrequentItemSets(candidates, transactionCount,
                    k);
            LOGGER.trace("S_{} = {}", k, frequentCandidates);
            candidates = combineItemSets(frequentCandidates, k);
            frequentCandidates.forEach(x -> frequentItemSets.put(x.hashCode(), x));
            k++;
        }

        return frequentItemSets;
    }

    // TODO: Comment
    private Pair<Collection<ItemSet>, Integer> findInitialItemSets(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        Map<Integer, ItemSet> itemSets = new HashMap<>();
        int transactionCount = 0;
        Transaction<ItemType> transaction;

        while ((transaction = iterator.next()) != null) {
            for (ItemType item : transaction) {
                ItemSet itemSet = new ItemSet();
                itemSet.items.add(item);
                ItemSet previous = itemSets.putIfAbsent(itemSet.hashCode(), itemSet);

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

    // TODO: Comment
    private Collection<ItemSet> combineItemSets(@NotNull final List<ItemSet> itemSets,
                                                final int k) {
        Set<ItemSet> combinedItemSets = new HashSet<>(
                IntStream.range(1, itemSets.size()).reduce(0, (x, y) -> (x + y)), 1);

        for (int i = 0; i < itemSets.size(); i++) {
            for (int j = i + 1; j < itemSets.size(); j++) {
                ItemSet itemSet1 = itemSets.get(i);
                ItemSet itemSet2 = itemSets.get(j);
                Iterator<ItemType> iterator1 = itemSet1.iterator();
                Iterator<ItemType> iterator2 = itemSet2.iterator();
                int index = 0;
                boolean valid = true;

                while (index < k - 1 && iterator1.hasNext() && iterator2.hasNext()) {
                    ItemType item1 = iterator1.next();
                    ItemType item2 = iterator2.next();

                    if (!item1.equals(item2)) {
                        valid = false;
                        break;
                    }

                    index++;
                }

                if (valid) {
                    ItemSet combinedItemSet = new ItemSet(itemSet1);
                    combinedItemSet.items.addAll(itemSet2.items);
                    combinedItemSet.transactions.putAll(itemSet2.transactions);
                    combinedItemSets.add(combinedItemSet);
                }
            }
        }

        return combinedItemSets;
    }

    // TODO: Comment
    private List<ItemSet> filterFrequentItemSets(@NotNull final Collection<ItemSet> itemSets,
                                                 final int transactionCount, final int k) {
        List<ItemSet> frequentCandidates = new ArrayList<>(itemSets.size());

        for (ItemSet candidate : itemSets) {
            if (k > 1) {
                Map<Integer, Transaction<ItemType>> transactions = new HashMap<>(
                        candidate.transactions.size());

                for (Map.Entry<Integer, Transaction<ItemType>> entry : candidate.transactions
                        .entrySet()) {
                    Transaction<ItemType> transaction = entry.getValue();
                    int count = 0;

                    for (ItemType item : transaction) {
                        // TODO: What if transaction contains the same item multiple times!?
                        if (candidate.items.contains(item)) {
                            count++;
                        }
                    }

                    if (count >= candidate.items.size()) {
                        transactions.put(entry.getKey(), transaction);
                    }
                }

                candidate.transactions = transactions;
            }

            int occurrences = candidate.transactions.size();
            double support = Metrics.calculateSupport(transactionCount, occurrences);
            candidate.support = support;

            if (support >= minSupport) {
                frequentCandidates.add(candidate);
            }
        }

        return frequentCandidates;
    }

    // TODO: Comment
    private RuleSet<ItemType> generateAssociationRules(
            @NotNull final Map<Integer, ItemSet> frequentItemSets) {
        Set<AssociationRule<ItemType>> rules = new HashSet<>();

        for (ItemSet itemSet : frequentItemSets.values()) {
            if (itemSet.items.size() > 1) {
                refineRule(frequentItemSets, itemSet, rules, itemSet, null);
            }
        }

        return new RuleSet<>(rules);
    }

    // TODO: Comment
    private void refineRule(@NotNull final Map<Integer, ItemSet> frequentItemSets,
                            @NotNull final ItemSet itemSet,
                            @NotNull final Set<AssociationRule<ItemType>> rules,
                            @NotNull final ItemSet bodyItemSet,
                            @Nullable final ItemSet headItemSet) {
        for (ItemType item : bodyItemSet.items) {
            ItemSet head = headItemSet != null ? new ItemSet(headItemSet) : new ItemSet();
            head.items.add(item);
            ItemSet body = new ItemSet(bodyItemSet);
            body.items.remove(item);
            body.support = frequentItemSets.get(body.hashCode()).support;
            double support = itemSet.support;
            double confidence = Metrics.calculateConfidence(body.support, support);

            if (confidence >= minConfidence) {
                head.support = frequentItemSets.get(head.hashCode()).support;
                double lift = support / (body.support * head.support);
                double leverage = support - (body.support * head.support);
                AssociationRule<ItemType> rule = new AssociationRule<>(body.items,
                        head.items, support, confidence, lift, leverage);
                rules.add(rule);

                if (body.items.size() > 1) {
                    refineRule(frequentItemSets, itemSet, rules, body, head);
                }
            }
        }
    }

    // TODO: Comment
    private String printFrequentItemSets(@NotNull final Map<Integer, ItemSet> frequentItemSets) {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(2);
        Iterator<ItemSet> iterator = frequentItemSets.values().iterator();
        stringBuilder.append("[");

        while (iterator.hasNext()) {
            ItemSet itemSet = iterator.next();
            stringBuilder.append(itemSet.toString());
            stringBuilder.append(" (support = ");
            stringBuilder.append(decimalFormat.format(itemSet.support));
            stringBuilder.append(")");

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * Creates a new implementation of the Apriori association rule learning algorithm for mining
     * frequent item sets.
     *
     * @param minSupport    The support, which should be reached by the association rules, which are
     *                      learned by the algorithm, at least as a {@link Double} value. The
     *                      support must be at least 0 and at maximum 1
     * @param minConfidence The confidence, which should be reached by the association rules, which
     *                      are learned by the algorithm, at least as a {@link Double} value. The
     *                      confidence must be at least 0 and at maximum 1
     */
    public Apriori(final double minSupport, final double minConfidence) {
        // TODO: Throw exceptions
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
    }

    /**
     * Returns the support, which must be reached by the association rules, which are learned by the
     * algorithm, at least.
     *
     * @return The support, which must be reached by the association rules, which are learned by the
     * algorithm, at least as a {@link Double} value. The support must be at least 0 and at maximum
     * 1
     */
    public final double getMinSupport() {
        return minSupport;
    }

    /**
     * Returns the confidence, which must be reached by the association rules, which are learned by
     * the algorithm, at least.
     *
     * @return The confidence, which must be reached by the association rules, which are learned by
     * the algorithm, at least as a {@link Double} value. The confidence must be at least 0 and at
     * maximum 1
     */
    public final double getMinConfidence() {
        return minConfidence;
    }

    /**
     * Executes the Apriori algorithm on a specific set of transactions in order to learn
     * association rules, which specify frequent item sets.
     *
     * @param iterator An iterator, which allows to iterate the transactions, as an instance of the
     *                 type {@link Iterable}. The iterable may not be null
     * @return The rule set, which contains the association rules, which have been learned by the
     * algorithm, as an instance of the class {@link RuleSet} or an empty rule set, if no
     * association rules have been learned
     */
    @NotNull
    public final RuleSet<ItemType> execute(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        // TODO: Throw exceptions
        LOGGER.info("Starting Apriori algorithm (minimum support = {}, minimum confidence = {})",
                minSupport, minConfidence);
        long startTime = System.currentTimeMillis();

        LOGGER.debug("Searching for frequent item sets");
        Map<Integer, ItemSet> frequentItemSets = findFrequentItemSets(iterator);
        LOGGER.debug("Found {} frequent item sets", frequentItemSets.size());
        LOGGER.debug("Frequent item sets = {}", printFrequentItemSets(frequentItemSets));

        LOGGER.debug("Generating association rules");
        RuleSet<ItemType> ruleSet = generateAssociationRules(
                frequentItemSets);
        LOGGER.debug("Generated {} association rules", ruleSet.size());
        LOGGER.debug("Rule set = {}", ruleSet);

        long runtime = System.currentTimeMillis() - startTime;
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", runtime);
        return ruleSet;
    }

}