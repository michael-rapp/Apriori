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

import java.io.Serializable;
import java.util.*;

/**
 * An implementation of the Apriori algorithm for mining frequent item sets and (optionally)
 * generating association rules. The algorithm processes a set of transactions, of which each one
 * consists of multiple items.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Apriori<ItemType extends Item> {

    public static class Configuration implements Serializable {

        /**
         * The constant serial version UID.
         */
        private static final long serialVersionUID = 1L;

        private double minSupport;

        private double maxSupport;

        private double supportDelta;

        private int frequentItemSetCount;

        private boolean generateRules;

        private double minConfidence;

        private double maxConfidence;

        private double confidenceDelta;

        private int ruleCount;

        protected Configuration() {
            setMinSupport(0);
            setMaxSupport(1);
            setSupportDelta(0.1);
            setFrequentItemSetCount(0);
            setGenerateRules(false);
            setMinConfidence(0);
            setMaxConfidence(1);
            setConfidenceDelta(0.1);
            setRuleCount(0);
        }

        protected Configuration(@NotNull final Configuration configuration) {
            setMinSupport(configuration.minSupport);
            setMaxSupport(configuration.maxSupport);
            setSupportDelta(configuration.supportDelta);
            setFrequentItemSetCount(configuration.frequentItemSetCount);
            setGenerateRules(configuration.generateRules);
            setMinConfidence(configuration.minConfidence);
            setMaxConfidence(configuration.maxConfidence);
            setConfidenceDelta(configuration.confidenceDelta);
            setRuleCount(configuration.ruleCount);
        }

        protected final void setMinSupport(final double minSupport) {
            // TODO: Throw exceptions
            this.minSupport = minSupport;
        }

        protected final void setMaxSupport(final double maxSupport) {
            // TODO: Throw exceptions
            this.maxSupport = maxSupport;
        }

        protected final void setSupportDelta(final double supportDelta) {
            // TODO: Throw exceptions
            this.supportDelta = supportDelta;
        }

        protected final void setFrequentItemSetCount(final int frequentItemSetCount) {
            // TODO: Throw exceptions
            this.frequentItemSetCount = frequentItemSetCount;
        }

        protected final void setGenerateRules(final boolean generateRules) {
            this.generateRules = generateRules;
        }

        protected final void setMinConfidence(final double minConfidence) {
            // TODO: Throw exceptions
            this.minConfidence = minConfidence;
        }

        protected final void setMaxConfidence(final double maxConfidence) {
            // TODO: Throw exceptions
            this.maxConfidence = maxConfidence;
        }

        protected final void setConfidenceDelta(final double confidenceDelta) {
            // TODO: Throw exceptions
            this.confidenceDelta = confidenceDelta;
        }

        protected final void setRuleCount(final int ruleCount) {
            // TODO: Throw exceptions
            this.ruleCount = ruleCount;
        }

    }

    private static abstract class AbstractBuilder<ItemType extends Item> {

        protected final Configuration configuration;

        private AbstractBuilder(final double minSupport) {
            configuration = new Configuration();
            configuration.setMinSupport(minSupport);
        }

        private AbstractBuilder(final int frequentItemSetCount) {
            configuration = new Configuration();
            configuration.setFrequentItemSetCount(frequentItemSetCount);
        }

        private AbstractBuilder(@NotNull final AbstractBuilder<ItemType> builder) {
            // TODO: Throw exceptions
            configuration = new Configuration(builder.configuration);
        }

        @NotNull
        public final Apriori<ItemType> create() {
            return new Apriori<>(configuration);
        }

    }

    public static class Builder<ItemType extends Item> extends AbstractBuilder<ItemType> {

        public Builder(final int frequentItemSetCount) {
            super(frequentItemSetCount);
        }

        public Builder(final double minSupport) {
            super(minSupport);
        }

        @NotNull
        public final Builder<ItemType> minSupport(final double minSupport) {
            configuration.setMinSupport(minSupport);
            return this;
        }

        @NotNull
        public final Builder<ItemType> maxSupport(final double maxSupport) {
            configuration.setMaxSupport(maxSupport);
            return this;
        }

        @NotNull
        public final Builder<ItemType> supportDelta(final double supportDelta) {
            configuration.setSupportDelta(supportDelta);
            return this;
        }

        @NotNull
        public final Builder<ItemType> frequentItemSetCount(final int frequentItemSetCount) {
            configuration.setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> generateRules(final double minConfidence) {
            return new RuleGeneratorBuilder<>(this, minConfidence);
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> generateRules(final int ruleCount) {
            return new RuleGeneratorBuilder<ItemType>(this, ruleCount);
        }

    }

    public static class RuleGeneratorBuilder<ItemType extends Item> extends
            AbstractBuilder<ItemType> {

        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final double minConfidence) {
            super(builder);
            minConfidence(minConfidence);
        }

        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final int ruleCount) {
            super(builder);
            ruleCount(ruleCount);
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> minSupport(final double minSupport) {
            configuration.setMinSupport(minSupport);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxSupport(final double maxSupport) {
            configuration.setMaxSupport(maxSupport);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> supportDelta(final double supportDelta) {
            configuration.setSupportDelta(supportDelta);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> frequentItemSetCount(
                final int frequentItemSetCount) {
            configuration.setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> minConfidence(final double minConfidence) {
            configuration.setMinConfidence(minConfidence);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxConfidence(final double maxConfidence) {
            configuration.setMaxConfidence(maxConfidence);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> confidenceDelta(final double confidenceDelta) {
            configuration.setConfidenceDelta(confidenceDelta);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> ruleCount(final int ruleCount) {
            configuration.setRuleCount(ruleCount);
            return this;
        }

    }

    /**
     * The SL4J logger, which is used by the algorithm.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Apriori.class);

    /**
     * The configuration, which is used by the algorithm.
     */
    private final Configuration configuration;

    /**
     * Creates a new implementation of the Apriori algorithm for mining frequent item sets.
     *
     * @param configuration The configuration, which should be used by the apriori algorithm, as an
     *                      instance of the class {@link Configuration}. The configuration may not
     *                      be null
     */
    private Apriori(@NotNull final Configuration configuration) {
        // TODO: Throw exceptions
        this.configuration = configuration;
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
    public final Output<ItemType> execute(
            @NotNull final Iterator<Transaction<ItemType>> iterator) {
        // TODO: Throw exceptions
        LOGGER.info("Starting Apriori algorithm");
        long startTime = System.currentTimeMillis();
        FrequentItemSetMiner<ItemType> frequentItemSetMiner = new FrequentItemSetMiner<>(
                configuration.minSupport);
        Map<Integer, ItemSet<ItemType>> frequentItemSets = frequentItemSetMiner
                .findFrequentItemSets(iterator);
        RuleSet<ItemType> ruleSet = null;

        if (configuration.generateRules) {
            AssociationRuleGenerator<ItemType> associationRuleGenerator = new AssociationRuleGenerator<>(
                    configuration.minConfidence);
            ruleSet = associationRuleGenerator
                    .generateAssociationRules(frequentItemSets);
        }

        SortedSet<ItemSet<ItemType>> sortedItemSets = new TreeSet<>(Comparator.reverseOrder());
        sortedItemSets.addAll(frequentItemSets.values());
        long endTime = System.currentTimeMillis();
        Output<ItemType> output = new Output<>(configuration, startTime, endTime, sortedItemSets,
                ruleSet);
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", output.getRuntime());
        return output;
    }

}