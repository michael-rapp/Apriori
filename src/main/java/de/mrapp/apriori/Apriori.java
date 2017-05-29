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

import de.mrapp.apriori.modules.AssociationRuleGenerator;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

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

    /**
     * The SL4J logger, which is used by the algorithm.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Apriori.class);

    /**
     * The module, which is used to search for frequent item sets.
     */
    private final FrequentItemSetMiner<ItemType> frequentItemSetMiner;

    /**
     * The module, which is used to generate association rules.
     */
    private final AssociationRuleGenerator<ItemType> associationRuleGenerator;

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
        this.frequentItemSetMiner = new FrequentItemSetMiner<>(minSupport);
        this.associationRuleGenerator = new AssociationRuleGenerator<>(minConfidence);
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
        LOGGER.info("Starting Apriori algorithm");
        long startTime = System.currentTimeMillis();
        Map<Integer, ItemSet<ItemType>> frequentItemSets = frequentItemSetMiner
                .findFrequentItemSets(iterator);
        RuleSet<ItemType> ruleSet = associationRuleGenerator
                .generateAssociationRules(frequentItemSets);
        long runtime = System.currentTimeMillis() - startTime;
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", runtime);
        return ruleSet;
    }

}