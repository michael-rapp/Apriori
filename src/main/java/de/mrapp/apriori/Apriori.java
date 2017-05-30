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
import de.mrapp.apriori.modules.AssociationRuleGenerator.Filter;
import de.mrapp.apriori.modules.FrequentItemSetMiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

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

    protected static abstract class AbstractBuilder<ItemType extends Item> {

        protected double minSupport;

        protected double maxSupport;

        protected double supportDelta;

        protected int frequentItemSetCount;

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

        final void setFrequentItemSetCount(final int frequentItemSetCount) {
            // TODO: Throw exceptions
            this.frequentItemSetCount = frequentItemSetCount;
        }

        private AbstractBuilder(@NotNull final AbstractBuilder<ItemType> builder) {
            // TODO: Throw exceptions
            setMinSupport(builder.minSupport);
            setMaxSupport(builder.maxSupport);
            setSupportDelta(builder.supportDelta);
            setFrequentItemSetCount(builder.frequentItemSetCount);
        }

        private AbstractBuilder(final int frequentItemSetCount) {
            setMinSupport(0);
            setMaxSupport(1);
            setSupportDelta(0.1);
            setFrequentItemSetCount(frequentItemSetCount);
        }

        private AbstractBuilder(final double minSupport) {
            setMinSupport(minSupport);
            setMaxSupport(1);
            setSupportDelta(0.1);
            setFrequentItemSetCount(0);
        }

        @NotNull
        public abstract Apriori<ItemType> create();

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
            setMinSupport(minSupport);
            return this;
        }

        @NotNull
        public final Builder<ItemType> maxSupport(final double maxSupport) {
            setMaxSupport(maxSupport);
            return this;
        }

        @NotNull
        public final Builder<ItemType> supportDelta(final double supportDelta) {
            setSupportDelta(supportDelta);
            return this;
        }

        @NotNull
        public final Builder<ItemType> frequentItemSetCount(final int frequentItemSetCount) {
            setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> generateRules(final double minConfidence) {
            return new RuleGeneratorBuilder<>(this, minConfidence);
        }

        @NotNull
        @Override
        public final Apriori<ItemType> create() {
            return new Apriori<>(minSupport, maxSupport, supportDelta, frequentItemSetCount);
        }

    }

    public static class RuleGeneratorBuilder<ItemType extends Item> extends
            AbstractBuilder<ItemType> {

        private double minConfidence;

        private double maxConfidence;

        private double confidenceDelta;

        private int ruleCount;

        private Metric sortRulesMetric;

        private Filter ruleFilter;

        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final double minConfidence) {
            super(builder);
            minConfidence(minConfidence);
            maxConfidence(1);
            confidenceDelta(0.1);
            ruleCount(0);
            sortRules(null);
            filterRules(null, 0);
        }

        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final int ruleCount) {
            super(builder);
            minConfidence(0);
            maxConfidence(1);
            confidenceDelta(0.1);
            ruleCount(ruleCount);
            sortRules(null);
            filterRules(null, 0);
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> minSupport(final double minSupport) {
            setMinSupport(minSupport);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxSupport(final double maxSupport) {
            setMaxSupport(maxSupport);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> supportDelta(final double supportDelta) {
            setSupportDelta(supportDelta);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> frequentItemSetCount(
                final int frequentItemSetCount) {
            setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> minConfidence(final double minConfidence) {
            // TODO: Throw exceptions
            this.minConfidence = minConfidence;
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxConfidence(final double maxConfidence) {
            // TODO: Throw exceptions
            this.maxConfidence = maxConfidence;
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> confidenceDelta(final double confidenceDelta) {
            // TODO: Throw exceptions
            this.confidenceDelta = confidenceDelta;
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> ruleCount(final int ruleCount) {
            // TODO: Throw exceptions
            this.ruleCount = ruleCount;
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> sortRules(@Nullable final Metric metric) {
            this.sortRulesMetric = metric;
            return this;
        }

        @NotNull
        public final RuleGeneratorBuilder<ItemType> filterRules(@Nullable final Metric metric,
                                                                final double threshold) {
            this.ruleFilter = metric != null ? new Filter(metric, threshold) : null;
            return this;
        }

        @NotNull
        @Override
        public final Apriori<ItemType> create() {
            return new Apriori<>(minSupport, maxSupport, supportDelta, frequentItemSetCount,
                    minConfidence, maxConfidence, confidenceDelta, ruleCount, sortRulesMetric,
                    ruleFilter);
        }

    }

    /**
     * The SL4J logger, which is used by the algorithm.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Apriori.class);

    private final double minSupport;

    private final double maxSupport;

    private final double supportDelta;

    private final int frequentItemSetCount;

    private final boolean generateRules;

    private final double minConfidence;

    private final double maxConfidence;

    private final double confidenceDelta;

    private final int ruleCount;

    private final Metric sortRulesMetric;

    private final Filter ruleFilter;

    /**
     * Creates a new implementation of the Apriori algorithm for mining frequent item sets.
     *
     * @param minSupport           The support, which should at least be reached by the frequent
     *                             item sets, which are searched by the algorithm, as a {@link
     *                             Double} value. The support must be at least 0 and at maximum 1
     * @param maxSupport           The support, which should at maximum be reached by the frequent
     *                             item sets, which are searched by the algorithm, as a {@link
     *                             Double} value. The support must be at least 0 and at maximum 1.
     *                             Furthermore, it must be at least the minimum support
     * @param supportDelta         The value, the minimum support should be decreased by after each
     *                             iteration, when trying to find a specific number of frequent item
     *                             sets, as a {@link Double} value. The value must be greater than
     *                             0
     * @param frequentItemSetCount The number of frequent item sets, the algorithm should try to
     *                             find, as an {@link Integer} value. If set to a value greater than
     *                             0 and if the number of frequent item sets is not reached after
     *                             executing the algorithm, the algorithm is executed iteratively
     *                             with the minimum support decreased by <code>supportDelta</code>
     *                             until the given number of frequent item sets is found or the
     *                             minimum support reaches 0
     */
    private Apriori(final double minSupport, final double maxSupport, final double supportDelta,
                    final int frequentItemSetCount) {
        // TODO: Throw exceptions
        this.minSupport = minSupport;
        this.maxSupport = maxSupport;
        this.supportDelta = supportDelta;
        this.frequentItemSetCount = frequentItemSetCount;
        this.generateRules = false;
        this.minConfidence = 0;
        this.maxConfidence = 1;
        this.confidenceDelta = 0.1;
        this.ruleCount = 0;
        this.sortRulesMetric = null;
        this.ruleFilter = null;
    }

    private Apriori(final double minSupport, final double maxSupport, final double supportDelta,
                    final int frequentItemSetCount, final double minConfidence,
                    final double maxConfidence, final double confidenceDelta, final int ruleCount,
                    @Nullable final Metric sortRulesMetric, @Nullable final Filter ruleFilter) {
        // TODO: Throw exceptions
        this.minSupport = minSupport;
        this.maxSupport = maxSupport;
        this.supportDelta = supportDelta;
        this.frequentItemSetCount = frequentItemSetCount;
        this.generateRules = true;
        this.minConfidence = minConfidence;
        this.maxConfidence = maxConfidence;
        this.confidenceDelta = confidenceDelta;
        this.ruleCount = ruleCount;
        this.sortRulesMetric = sortRulesMetric;
        this.ruleFilter = ruleFilter;
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
                minSupport);
        Map<Integer, ItemSet<ItemType>> frequentItemSets = frequentItemSetMiner
                .findFrequentItemSets(iterator);
        RuleSet<ItemType> ruleSet = null;

        if (generateRules) {
            AssociationRuleGenerator<ItemType> associationRuleGenerator = new AssociationRuleGenerator<>(
                    minConfidence);
            ruleSet = associationRuleGenerator
                    .generateAssociationRules(frequentItemSets);
        }

        long runtime = System.currentTimeMillis() - startTime;
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", runtime);
        // TODO: Add frequent item sets and rule set to output
        return new Output<>(runtime);
    }

}