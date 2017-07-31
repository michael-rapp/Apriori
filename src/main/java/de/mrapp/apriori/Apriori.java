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

import de.mrapp.apriori.datastructure.TransactionalItemSet;
import de.mrapp.apriori.tasks.AssociationRuleGeneratorTask;
import de.mrapp.apriori.tasks.FrequentItemSetMinerTask;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

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
    public static class Configuration implements Serializable, Cloneable {

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
         * The minimum support, which should initially be used, when trying to find a specific
         * number of frequent item sets.
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
         * Returns the minimum support, which must at least be reached by an item set to be
         * considered frequent.
         *
         * @return The minimum support, which must at least be reached by an item set to be
         * considered frequent, as a {@link Double} value
         */
        public double getMinSupport() {
            return minSupport;
        }

        /**
         * Sets the minimum support, which must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set, as a {@link Double} value. The
         *                   support must at least be 0 and at maximum the maximum support
         */
        protected void setMinSupport(final double minSupport) {
            ensureAtLeast(minSupport, 0, "The minimum support must be at least 0");
            ensureAtMaximum(minSupport, maxSupport,
                    "The minimum support must be at maximum " + maxSupport);
            this.minSupport = minSupport;
        }

        /**
         * Returns the minimum support, which should initially be used, when trying to find a
         * specific number of frequent item sets.
         *
         * @return The minimum support, which should initially be used, when trying to find a
         * specific number of frequent item sets, as a {@link Double} value
         */
        public double getMaxSupport() {
            return maxSupport;
        }

        /**
         * Sets the minimum support, which should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set, as a {@link Double} value. The
         *                   support must be at least the minimum support
         */
        protected void setMaxSupport(final double maxSupport) {
            ensureAtMaximum(maxSupport, 1, "The maximum support must be at maximum 1");
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
        public double getSupportDelta() {
            return supportDelta;
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set, as a {@link Double} value. The value
         *                     must be greater than 0
         */
        protected void setSupportDelta(final double supportDelta) {
            ensureGreater(supportDelta, 0, "The support delta must be greater than 0");
            this.supportDelta = supportDelta;
        }

        /**
         * Returns the number of frequent item sets, the Apriori algorithm should try to find.
         *
         * @return The number of frequent item sets, the Apriori algorithm should try to find, as an
         * {@link Integer} value
         */
        public int getFrequentItemSetCount() {
            return frequentItemSetCount;
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, as an
         *                             {@link Integer} value or 0, if the Apriori algorithm should
         *                             not try to find a specific number of frequent item sets
         */
        protected void setFrequentItemSetCount(final int frequentItemSetCount) {
            ensureAtLeast(frequentItemSetCount, 0,
                    "The number of frequent item sets must be at least 0");
            this.frequentItemSetCount = frequentItemSetCount;
        }

        /**
         * Returns, whether association rules should be generated, or not.
         *
         * @return True, if association rules should be generated, false otherwise
         */
        public boolean isGeneratingRules() {
            return generateRules;
        }

        /**
         * Sets, whether association rules should be generated, or not.
         *
         * @param generateRules True, if association rules should be generated, false otherwise
         */
        protected void setGenerateRules(final boolean generateRules) {
            this.generateRules = generateRules;
        }

        /**
         * Returns the minimum confidence, which must at least be reached by association rules.
         *
         * @return The minimum confidence, which must at least be reached by association rules, as a
         * {@link Double} value
         */
        public double getMinConfidence() {
            return minConfidence;
        }

        /**
         * Sets the minimum confidence, which must at least be reached by association rules.
         *
         * @param minConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum the maximum confidence
         */
        protected void setMinConfidence(final double minConfidence) {
            ensureAtLeast(minConfidence, 0, "The minimum confidence must be at least 0");
            ensureAtMaximum(minConfidence, maxConfidence,
                    "The minimum confidence must be at maximum " + maxConfidence);
            this.minConfidence = minConfidence;
        }

        /**
         * Returns the minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @return The minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules, as a {@link Double} value
         */
        public double getMaxConfidence() {
            return maxConfidence;
        }

        /**
         * Sets the minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @param maxConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum 1
         */
        protected void setMaxConfidence(final double maxConfidence) {
            ensureAtMaximum(maxConfidence, 1, "The max confidence must be at maximum 1");
            ensureAtLeast(maxConfidence, minConfidence,
                    "The max confidence must be at least " + minConfidence);
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
        public double getConfidenceDelta() {
            return confidenceDelta;
        }

        /**
         * Sets the value, the minimum confidence should be decreased by after each iteration, when
         * trying to generate a specific number of association rules.
         *
         * @param confidenceDelta The value, which should be set, as a {@link Double} value. The
         *                        value must be greater than 0
         */
        protected void setConfidenceDelta(final double confidenceDelta) {
            ensureGreater(confidenceDelta, 0, "The confidence delta must be greater than 0");
            this.confidenceDelta = confidenceDelta;
        }

        /**
         * Returns the number of association rules, the Apriori algorithm should try to generate.
         *
         * @return The number of association rules, the Apriori algorithm should try to generate, as
         * an {@link Integer} value
         */
        public int getRuleCount() {
            return ruleCount;
        }

        /**
         * Sets the number of association rule, the Apriori algorithm should try to generate.
         *
         * @param ruleCount The number of association rules, which should be set, as an {@link
         *                  Integer} value or 0, if the Apriori algorithm should not try to generate
         *                  a specific number of association rules
         */
        protected void setRuleCount(final int ruleCount) {
            ensureAtLeast(ruleCount, 0, "The rule count must be at least 0");
            this.ruleCount = ruleCount;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public final Configuration clone() {
            Configuration clone = new Configuration();
            clone.minSupport = minSupport;
            clone.maxSupport = maxSupport;
            clone.supportDelta = supportDelta;
            clone.frequentItemSetCount = frequentItemSetCount;
            clone.generateRules = generateRules;
            clone.minConfidence = minConfidence;
            clone.maxConfidence = maxConfidence;
            clone.confidenceDelta = confidenceDelta;
            clone.ruleCount = ruleCount;
            return clone;
        }

        @Override
        public final String toString() {
            return "[minSupport=" + minSupport + ", maxSupport=" + maxSupport +
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

    /**
     * An abstract base class for all builders, which allow to configure and create instances of the
     * class {@link Apriori}.
     *
     * @param <ItemType> The type of the items, which are processed by the algorithm
     */
    private static abstract class AbstractBuilder<ItemType extends Item> {

        /**
         * The configuration, which is configured by the builder.
         */
        protected final Configuration configuration;

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}.
         *
         * @param minSupport The minimum support, which must at least be reached by an item set to
         *                   be considered frequent, as a {@link Double value}. The support must be
         *                   at least 0 and at maximum 1
         */
        private AbstractBuilder(final double minSupport) {
            configuration = new Configuration();
            configuration.setMinSupport(minSupport);
        }

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}.
         *
         * @param frequentItemSetCount The number of frequent item sets, the Apriori algorithm
         *                             should try to find, as an {@link Integer} value or 0, if the
         *                             algorithm should not try to find a specific number of
         *                             frequent item sets
         */
        private AbstractBuilder(final int frequentItemSetCount) {
            configuration = new Configuration();
            configuration.setFrequentItemSetCount(frequentItemSetCount);
        }

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}, by copying the properties of another builder.
         *
         * @param builder The builder, which should be copied, as an instance of the class {@link
         *                AbstractBuilder}. The builder may not be null
         */
        private AbstractBuilder(@NotNull final AbstractBuilder<ItemType> builder) {
            ensureNotNull(builder, "The builder may not be null");
            configuration = builder.configuration.clone();
        }

        /**
         * Creates and returns the Apriori algorithm, which has been configured by the builder.
         *
         * @return The Apriori algorithm, which has been created, as an instance of the class {@link
         * Apriori}. The Apriori algorithm may not be null
         */
        @NotNull
        public final Apriori<ItemType> create() {
            return new Apriori<>(configuration);
        }

    }

    /**
     * A builder, which allows to configure and create instances of the class {@link Apriori}.
     *
     * @param <ItemType> The type of the items, which are processed by the algorithm
     */
    public static class Builder<ItemType extends Item> extends AbstractBuilder<ItemType> {

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}.
         *
         * @param minSupport The minimum support, which must at least be reached by an item set to
         *                   be considered frequent, as a {@link Double value}. The support must be
         *                   at least 0 and at maximum 1
         */
        public Builder(final double minSupport) {
            super(minSupport);
        }

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}.
         *
         * @param frequentItemSetCount The number of frequent item sets, the Apriori algorithm
         *                             should try to find, as an {@link Integer} value or 0, if the
         *                             algorithm should not try to find a specific number of
         *                             frequent item sets
         */
        public Builder(final int frequentItemSetCount) {
            super(frequentItemSetCount);
        }

        /**
         * Sets the minimum support, which must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set, as a {@link Double} value. The
         *                   support must at least be 0 and at maximum 1
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * Builder}. The builder may not be null
         */
        @NotNull
        public final Builder<ItemType> minSupport(final double minSupport) {
            configuration.setMinSupport(minSupport);
            return this;
        }


        /**
         * Sets the minimum support, which should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set, as a {@link Double} value. The
         *                   support must be at least the minimum support
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * Builder}. The builder may not be null
         */
        @NotNull
        public final Builder<ItemType> maxSupport(final double maxSupport) {
            configuration.setMaxSupport(maxSupport);
            return this;
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set, as a {@link Double} value. The value
         *                     must be greater than 0 and less than the maximum support
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * Builder}. The builder may not be null
         */
        @NotNull
        public final Builder<ItemType> supportDelta(final double supportDelta) {
            configuration.setSupportDelta(supportDelta);
            return this;
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, as an
         *                             {@link Integer} value or 0, if the Apriori algorithm should
         *                             not try to find a specific number of frequent item sets
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * Builder}. The builder may not be null
         */
        @NotNull
        public final Builder<ItemType> frequentItemSetCount(final int frequentItemSetCount) {
            configuration.setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        /**
         * Enables to generate association rules.
         *
         * @param minConfidence The minimum confidence, which must at least be reached by
         *                      association rules, as a {@link Double} value. The confidence must at
         *                      least be 0 and at maximum 1
         * @return The builder, which allows to configure the generation of association rules, as an
         * instance of the class {@link RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> generateRules(final double minConfidence) {
            return new RuleGeneratorBuilder<>(this, minConfidence);
        }

        /**
         * Enables to generate association rules.
         *
         * @param ruleCount The number of association rules, the Apriori algorithm should try to
         *                  generate, as an {@link Integer} value or 0, if the algorithm should not
         *                  try to generate a specific number of association rules
         * @return The builder, which allows to configure the generation of association rules, as an
         * instance of the class {@link RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> generateRules(final int ruleCount) {
            return new RuleGeneratorBuilder<ItemType>(this, ruleCount);
        }

    }

    /**
     * A builder, which allows to configure and create instances of the class {@link Apriori}, which
     * are configured to generate association rules.
     *
     * @param <ItemType> The type of the items, which are processed by the algorithm
     */
    public static class RuleGeneratorBuilder<ItemType extends Item> extends
            AbstractBuilder<ItemType> {

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}, which are configured to generate association rules.
         *
         * @param builder       The builder, which has previously been used to configure the Apriori
         *                      algorithm, as an instance of the class {@link AbstractBuilder}. The
         *                      builder may not be null
         * @param minConfidence The minimum confidence, which must at least be reached by
         *                      association rules, as a {@link Double value}. The confidence must be
         *                      greater than the minimum confidence and at maximum 1
         */
        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final double minConfidence) {
            super(builder);
            configuration.setGenerateRules(true);
            minConfidence(minConfidence);
        }

        /**
         * Creates a new builder, which allows to configure and create instances of the class {@link
         * Apriori}, which are configured to generate association rules.
         *
         * @param builder   The builder, which has previously been used to configure the Apriori
         *                  algorithm, as an instance of the class {@link AbstractBuilder}. The
         *                  builder may not be null
         * @param ruleCount The number of association rules, the Apriori algorithm should try to
         *                  generate, as an {@link Integer} value or 0, if the algorithm should not
         *                  try to generate a specific number of association rules
         */
        private RuleGeneratorBuilder(@NotNull final AbstractBuilder<ItemType> builder,
                                     final int ruleCount) {
            super(builder);
            configuration.setGenerateRules(true);
            ruleCount(ruleCount);
        }

        /**
         * Sets the minimum support, which must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set, as a {@link Double} value. The
         *                   support must at least be 0 and at maximum 1
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> minSupport(final double minSupport) {
            configuration.setMinSupport(minSupport);
            return this;
        }

        /**
         * Sets the minimum support, which should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set, as a {@link Double} value. The
         *                   support must be at least the minimum support
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxSupport(final double maxSupport) {
            configuration.setMaxSupport(maxSupport);
            return this;
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set, as a {@link Double} value. The value
         *                     must be greater than 0 and less than the maximum support
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> supportDelta(final double supportDelta) {
            configuration.setSupportDelta(supportDelta);
            return this;
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, as an
         *                             {@link Integer} value or 0, if the Apriori algorithm should
         *                             not try to find a specific number of frequent item sets
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> frequentItemSetCount(
                final int frequentItemSetCount) {
            configuration.setFrequentItemSetCount(frequentItemSetCount);
            return this;
        }

        /**
         * Sets the minimum confidence, which must at least be reached by association rules.
         *
         * @param minConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum 1
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> minConfidence(final double minConfidence) {
            configuration.setMinConfidence(minConfidence);
            return this;
        }

        /**
         * Sets the minimum confidence, which should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @param maxConfidence The confidence, which should be set, as a {@link Double} value. The
         *                      confidence must be at least 0 and at maximum 1
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> maxConfidence(final double maxConfidence) {
            configuration.setMaxConfidence(maxConfidence);
            return this;
        }

        /**
         * Sets the value, the minimum confidence should be decreased by after each iteration, when
         * trying to generate a specific number of association rules.
         *
         * @param confidenceDelta The value, which should be set, as a {@link Double} value. The
         *                        value must be greater than 0 and less than the maximum confidence
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
        @NotNull
        public final RuleGeneratorBuilder<ItemType> confidenceDelta(final double confidenceDelta) {
            configuration.setConfidenceDelta(confidenceDelta);
            return this;
        }

        /**
         * Sets the number of association rule, the Apriori algorithm should try to generate.
         *
         * @param ruleCount The number of association rules, which should be set, as an {@link
         *                  Integer} value or 0, if the Apriori algorithm should not try to generate
         *                  a specific number of association rules
         * @return The builder, this method has been called upon, as an instance of the class {@link
         * RuleGeneratorBuilder}. The builder may not be null
         */
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
     * The task, which is used to find frequent item sets.
     */
    private final FrequentItemSetMinerTask<ItemType> frequentItemSetMinerTask;

    /**
     * The task, which is used to generate association rules.
     */
    private final AssociationRuleGeneratorTask<ItemType> associationRuleGeneratorTask;

    /**
     * Creates a new implementation of the Apriori algorithm for mining frequent item sets and
     * (optionally) generating association rules.
     *
     * @param configuration The configuration, which should be used by the apriori algorithm, as an
     *                      instance of the class {@link Configuration}. The configuration may not
     *                      be null
     */
    protected Apriori(@NotNull final Configuration configuration) {
        this(configuration, new FrequentItemSetMinerTask<>(configuration),
                new AssociationRuleGeneratorTask<>(configuration));
    }

    /**
     * Creates a new implementation of the Apriori algorithm for mining frequent item sets and
     * (optionally) generating association rules.
     *
     * @param configuration                The configuration, which should be used by the apriori
     *                                     algorithm, as an instance of the class {@link
     *                                     Configuration}. The configuration may not be null
     * @param frequentItemSetMinerTask     The task, which should be used to find frequent item
     *                                     sets, as an instance of the class {@link
     *                                     FrequentItemSetMinerTask}. The task may not be null
     * @param associationRuleGeneratorTask The task, which should be used to generate association
     *                                     rules, as an instance of the class {@link
     *                                     AssociationRuleGeneratorTask}. The task may not be null
     */
    protected Apriori(@NotNull final Configuration configuration,
                      @NotNull final FrequentItemSetMinerTask<ItemType> frequentItemSetMinerTask,
                      @NotNull final AssociationRuleGeneratorTask<ItemType> associationRuleGeneratorTask) {
        ensureNotNull(configuration, "The configuration may not be null");
        ensureNotNull(frequentItemSetMinerTask, "The frequent item set miner task may not be null");
        ensureNotNull(associationRuleGeneratorTask,
                "The association rule generator task may not be null");
        this.configuration = configuration;
        this.frequentItemSetMinerTask = frequentItemSetMinerTask;
        this.associationRuleGeneratorTask = associationRuleGeneratorTask;
    }

    /**
     * Returns the configuration, which is used by the Apriori algorithm.
     *
     * @return The configuration, which is used by the Apriori algorithm, as an instance of the
     * class {@link Configuration}. The configuration may not be null
     */
    @NotNull
    public final Configuration getConfiguration() {
        return configuration;
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
        ensureNotNull(iterator, "The iterator may not be null");
        LOGGER.info("Starting Apriori algorithm");
        long startTime = System.currentTimeMillis();
        Map<Integer, TransactionalItemSet<ItemType>> frequentItemSets = frequentItemSetMinerTask
                .findFrequentItemSets(iterator);
        RuleSet<ItemType> ruleSet = null;

        if (configuration.isGeneratingRules()) {
            ruleSet = associationRuleGeneratorTask.generateAssociationRules(frequentItemSets);
        }

        FrequentItemSets<ItemType> sortedItemSets = new FrequentItemSets<>(
                Comparator.reverseOrder());
        frequentItemSets.values().forEach(x -> sortedItemSets.add(new ItemSet<>(x)));
        long endTime = System.currentTimeMillis();
        Output<ItemType> output = new Output<>(configuration, startTime, endTime, sortedItemSets,
                ruleSet);
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", output.getRuntime());
        return output;
    }

}