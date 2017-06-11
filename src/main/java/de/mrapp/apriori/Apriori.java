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
import de.mrapp.apriori.tasks.FrequentItemSetMinerTask;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static de.mrapp.util.Condition.*;

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

    /**
     * An configuration of the Apriori algorithm.
     */
    public static class Configuration implements Serializable {

        /**
         * The constant serial version UID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The minimum support, which must at least be reached by item sets to be considered
         * frequent.
         */
        private double minSupport;

        /**
         * The minimum support, which should initially be used, when trying to find
         * a specific number of frequent item sets.
         */
        private double maxSupport;

        /**
         * The value, the minimum support should be decreased by after each iteration, when trying
         * to find a specific number of frequent item sets.
         */
        private double supportDelta;

        /**
         * The number of frequent item sets the Apriori algorithm should try to find.
         */
        private int frequentItemSetCount;

        /**
         * True, if association rules should be generated, false otherwise.
         */
        private boolean generateRules;

        /**
         * The minimum confidence, which must at least be reached by association rules.
         */
        private double minConfidence;

        /**
         * The minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         */
        private double maxConfidence;

        /**
         * The value, the minimum confidence should be decreased by, when trying to generate a
         * specific number of association rules.
         */
        private double confidenceDelta;

        /**
         * The number of association rules the Apriori algorithm should try to to generate.
         */
        private int ruleCount;

        /**
         * Creates a new configuration of the Apriori algorithm with default values.
         */
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

        /**
         * Creates a new configuration of the Apriori algorithm by copying an existing one.
         *
         * @param configuration The configuration, which should be copied, as an instance of the
         *                      class {@link Configuration}. The configuration may not be null
         */
        protected Configuration(@NotNull final Configuration configuration) {
            ensureNotNull(configuration, "The configuration may not be null");
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

        /**
         * Returns the minimum support, which must at least be reached by an item set to be
         * considered frequent.
         *
         * @return The minimum support, which must at least be reached by an item set to be
         * considered frequent, as a {@link Double} value
         */
        public final double getMinSupport() {
            return minSupport;
        }

        /**
         * Sets the minimum support, which must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set, as a {@link Double} value. The
         *                   support must at least be 0 and at maximum 1
         */
        protected final void setMinSupport(final double minSupport) {
            ensureAtLeast(minSupport, 0, "The minimum support must at least be 0");
            ensureAtMaximum(minSupport, 1, "The minimum support must at least be 1");
            this.minSupport = minSupport;
        }

        /**
         * Returns the minimum support, which should initially be used, when trying to find a
         * specific number of frequent item sets.
         *
         * @return The minimum support, which should initially be used, when trying to find a
         * specific number of frequent item sets, as a {@link Double} value
         */
        public final double getMaxSupport() {
            return maxSupport;
        }

        /**
         * Sets the minimum support, which should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set, as a {@link Double} value. The
         *                   support must be at least the minimum support
         */
        protected final void setMaxSupport(final double maxSupport) {
            ensureAtLeast(maxSupport, minSupport, "The maximum support must be at least" +
                    minSupport);
            this.maxSupport = maxSupport;
        }

        /**
         * Returns the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @return The value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets, as a {@link Double} value
         */
        public final double getSupportDelta() {
            return supportDelta;
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set, as a {@link Double} value. The value
         *                     must be greater than 0 and less than the maximum support
         */
        protected final void setSupportDelta(final double supportDelta) {
            ensureGreater(supportDelta, 0, "The support delta must be greater than 0");
            ensureSmaller(supportDelta, maxSupport,
                    "The support delta must be less than " + maxSupport);
            this.supportDelta = supportDelta;
        }

        /**
         * Returns the number of frequent item sets, the Apriori algorithm should try to find.
         *
         * @return The number of frequent item sets, the Apriori algorithm should try to find, as an
         * {@link Integer} value
         */
        public final int getFrequentItemSetCount() {
            return frequentItemSetCount;
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, as an
         *                             {@link Integer} value or 0, if the Apriori algorithm should
         *                             not try to find a specific number of frequent item sets
         */
        protected final void setFrequentItemSetCount(final int frequentItemSetCount) {
            ensureAtLeast(frequentItemSetCount, 0,
                    "The number of frequent item sets must be at least 0");
            this.frequentItemSetCount = frequentItemSetCount;
        }

        /**
         * Returns, whether association rules should be generated, or not.
         *
         * @return True, if association rules should be generated, false otherwise
         */
        public final boolean isGeneratingRules() {
            return generateRules;
        }

        /**
         * Sets, whether association rules should be generated, or not.
         *
         * @param generateRules True, if association rules should be generated, false otherwise
         */
        protected final void setGenerateRules(final boolean generateRules) {
            this.generateRules = generateRules;
        }

        /**
         * Returns the minimum confidence, which must at least be reached by association rules.
         *
         * @return The minimum confidence, which must at least be reached by association rules, as a
         * {@link Double} value
         */
        public final double getMinConfidence() {
            return minConfidence;
        }

        /**
         * Sets the minimum confidence, which must at least be reached by association rules.
         *
         * @param minConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum 1
         */
        protected final void setMinConfidence(final double minConfidence) {
            ensureAtLeast(minConfidence, 0, "The minimum confidence must be at least 0");
            ensureAtMaximum(minConfidence, 1, "The minimum confidence must be at least 1");
            this.minConfidence = minConfidence;
        }

        /**
         * Returns the minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @return The minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules, as a {@link Double} value
         */
        public final double getMaxConfidence() {
            return maxConfidence;
        }

        /**
         * Sets the minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @param maxConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum 1
         */
        protected final void setMaxConfidence(final double maxConfidence) {
            ensureAtLeast(maxConfidence, 0, "The max confidence must be at least 0");
            ensureAtMaximum(maxConfidence, 1, "The max confidence must be at least 1");
            this.maxConfidence = maxConfidence;
        }

        /**
         * Returns the value, the minimum confidence should be decreased by after each iteration,
         * when trying to generate a specific number of association rules.
         *
         * @return The value, the minimum confidence should be decreased by after each iteration,
         * when trying to generate a specific number of association rules, as a {@link Double}
         * value
         */
        public final double getConfidenceDelta() {
            return confidenceDelta;
        }

        /**
         * Sets the value, the minimum confidence should be decreased by after each iteration, when
         * trying to generate a specific number of association rules.
         *
         * @param confidenceDelta The value, which should be set, as a {@link Double} value. The
         *                        value must be greater than 0 and less than the maximum confidence
         */
        protected final void setConfidenceDelta(final double confidenceDelta) {
            ensureGreater(confidenceDelta, 0, "The confidence delta must be greater than 0");
            ensureSmaller(confidenceDelta, maxConfidence,
                    "The confidence delta must be less than " + maxSupport);
            this.confidenceDelta = confidenceDelta;
        }

        /**
         * Returns the number of association rules, the Apriori algorithm should try to generate.
         *
         * @return The number of association rules, the Apriori algorithm should try to generate, as
         * an {@link Integer} value
         */
        public final int getRuleCount() {
            return ruleCount;
        }

        /**
         * Sets the number of association rule, the Apriori algorithm should try to generate.
         *
         * @param ruleCount The number of association rules, which should be set, as an {@link
         *                  Integer} value or 0, if the Apriori algorithm should not try to generate
         *                  a specific number of association rules
         */
        protected final void setRuleCount(final int ruleCount) {
            ensureAtLeast(ruleCount, 0, "The rule count must be at least 0");
            this.ruleCount = ruleCount;
        }

        @Override
        public final String toString() {
            return "Configuration [minSupport=" + minSupport + ", maxSupport=" + maxSupport +
                    ", supportDelta=" + supportDelta + ", frequentItemSetCount=" +
                    frequentItemSetCount + ", generateRules=" + generateRules + ", minConfidence=" +
                    minConfidence + ", maxConfidence=" + maxConfidence + ", confidenceDelta=" +
                    confidenceDelta + ", ruleCount=" + ruleCount + "]";
        }

        @Override
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            long tempMinSupport = Double.doubleToLongBits(minSupport);
            result = prime * result + (int) (tempMinSupport ^ (tempMinSupport >>> 32));
            long tempMaxSupport = Double.doubleToLongBits(maxSupport);
            result = prime * result + (int) (tempMaxSupport ^ (tempMaxSupport >>> 32));
            long tempSupportDelta = Double.doubleToLongBits(supportDelta);
            result = prime * result + (int) (tempSupportDelta ^ (tempSupportDelta >>> 32));
            result = prime * result + frequentItemSetCount;
            result = prime * result + (generateRules ? 1231 : 1237);
            long tempMinConfidence = Double.doubleToLongBits(minConfidence);
            result = prime * result + (int) (tempMinConfidence ^ (tempMinConfidence >>> 32));
            long tempMaxConfidence = Double.doubleToLongBits(maxConfidence);
            result = prime * result + (int) (tempMaxConfidence ^ (tempMaxConfidence >>> 32));
            long tempConfidenceDelta = Double.doubleToLongBits(confidenceDelta);
            result = prime * result + (int) (tempConfidenceDelta ^ (tempConfidenceDelta >>> 32));
            result = prime * result + ruleCount;
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
            Configuration other = (Configuration) obj;
            return minSupport == other.minSupport && maxSupport == other.maxSupport &&
                    supportDelta == other.supportDelta &&
                    frequentItemSetCount == other.frequentItemSetCount &&
                    generateRules == other.generateRules && minConfidence == other.minConfidence &&
                    maxConfidence == other.maxConfidence &&
                    confidenceDelta == other.confidenceDelta && ruleCount == other.ruleCount;
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
        FrequentItemSetMinerTask<ItemType> frequentItemSetMinerTask = new FrequentItemSetMinerTask<>(
                configuration);
        Map<Integer, ItemSet<ItemType>> frequentItemSets = frequentItemSetMinerTask
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