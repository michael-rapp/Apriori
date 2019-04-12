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

import de.mrapp.apriori.tasks.AssociationRuleGeneratorTask
import de.mrapp.apriori.tasks.FrequentItemSetMinerTask
import de.mrapp.util.Condition.ensureAtLeast
import de.mrapp.util.Condition.ensureAtMaximum
import de.mrapp.util.Condition.ensureGreater
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * An implementation of the Apriori algorithm for mining frequent item sets and (optionally)
 * generating association rules. The algorithm processes a set of transactions, of which each one
 * consists of multiple items.
 *
 * @param    ItemType                     The type of the items, which are processed by the
 *                                        algorithm
 * @property configuration                The configuration that is used by the algorithm
 * @property frequentItemSetMinerTask     The task that is used to find frequent item sets
 * @property associationRuleGeneratorTask The task that is used to generate association rules
 * @author Michael Rapp
 * @since 1.0.0
 */
class Apriori<ItemType : Item> internal constructor(
        val configuration: Apriori.Configuration,
        private val frequentItemSetMinerTask: FrequentItemSetMinerTask<ItemType>,
        private val associationRuleGeneratorTask: AssociationRuleGeneratorTask<ItemType>) {

    companion object {

        /**
         * The SL4J logger, which is used by the algorithm.
         */
        private val LOGGER = LoggerFactory.getLogger(Apriori::class.java)

    }

    /**
     * The configuration of the Apriori algorithm.
     *
     * @property minSupport           The minimum support, which must at least be reached by item
     *                                sets to be considered frequent
     * @property maxSupport           The minimum support, which is initially used, when trying to
     *                                find a specific number of frequent item sets
     * @property supportDelta         The value, the minimum support is decreased by after each
     *                                iteration, when trying to find a specific number of frequent
     *                                item sets
     * @property frequentItemSetCount The number of frequent item sets the Apriori algorithm tries
     *                                to find
     * @property generateRules        True, if association rules are generated, false otherwise
     * @property minConfidence        The minimum confidence, which must at least be reached by
     *                                association rules
     * @property maxConfidence        The minimum confidence, which is initially used, when trying
     *                                to generate a specific number of association rules
     * @property confidenceDelta      The value, the minimum confidence is decreased by, when trying
     *                                to generate a specific number of association rules
     * @property ruleCount            The number of association rules the Apriori algorithm tries to
     *                                generate
     */
    data class Configuration(var minSupport: Double = 0.0,
                             var maxSupport: Double = 1.0,
                             var supportDelta: Double = 0.1,
                             var frequentItemSetCount: Int = 0,
                             var generateRules: Boolean = false,
                             var minConfidence: Double = 0.0,
                             var maxConfidence: Double = 1.0,
                             var confidenceDelta: Double = 0.1,
                             var ruleCount: Int = 0) : Serializable

    /**
     * An abstract base class for all builders that allow to configure and create instances of the
     * class [Apriori].
     *
     * @param ItemType The type of the items, which are processed by the algorithm
     */
    abstract class AbstractBuilder<ItemType : Item> {

        /**
         * The configuration that is configured by the builder.
         */
        protected val configuration: Configuration

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori].
         *
         * @param minSupport The minimum support that must at least be reached by an item set to be
         *                   considered frequent
         */
        protected constructor(minSupport: Double) {
            ensureAtLeast(minSupport, 0.0, "The minimum support must be at least 0")
            ensureAtMaximum(minSupport, 1.0, "The minimum support must at maximum 1")
            configuration = Configuration(minSupport = minSupport)
        }

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori].
         *
         * @param frequentItemSetCount The number of frequent item sets, the Apriori algorithm
         *                             should try to find, or 0, if the algorithm should not try to
         *                             find a specific number of frequent item sets
         */
        protected constructor(frequentItemSetCount: Int) {
            ensureAtLeast(frequentItemSetCount, 0,
                    "The number of frequent item sets must be at least 0")
            configuration = Configuration(frequentItemSetCount = frequentItemSetCount)
        }

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori], by copying the properties of another [builder].
         */
        protected constructor(builder: AbstractBuilder<ItemType>) {
            configuration = builder.configuration.copy()
        }

        /**
         * Creates and returns the Apriori algorithm, which has been configured by the builder.
         */
        fun create(): Apriori<ItemType> = Apriori(configuration)

    }

    /**
     * A builder that allows to configure and create instances of the class [Apriori].
     *
     * @param ItemType The type of the items, which are processed by the algorithm
     */
    class Builder<ItemType : Item> : AbstractBuilder<ItemType> {

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori].
         *
         * @param minSupport The minimum support that must at least be reached by an item set to be
         *                   considered frequent
         */
        constructor(minSupport: Double) : super(minSupport)

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori].
         *
         * @param frequentItemSetCount The number of frequent item sets, the Apriori algorithm
         *                             should try to find, or 0, if the algorithm should not try to
         *                             find a specific number of frequent item sets
         */
        constructor(frequentItemSetCount: Int) : super(frequentItemSetCount)

        /**
         * Sets the minimum support that must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set. The support must at least be 0 and at
         *                   maximum 1
         */
        fun minSupport(minSupport: Double): Builder<ItemType> {
            ensureAtLeast(minSupport, 0.0, "The minimum support must be at least 0")
            ensureAtMaximum(minSupport, 1.0, "The minimum support must be at maximum 1")
            configuration.minSupport = minSupport
            return this
        }


        /**
         * Sets the minimum support that should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set. The support must be at least the
         *                   minimum support
         */
        fun maxSupport(maxSupport: Double): Builder<ItemType> {
            ensureAtMaximum(maxSupport, 1.0, "The maximum support must be at maximum 1")
            ensureAtLeast(maxSupport, configuration.minSupport,
                    "The maximum support must be at least ${configuration.minSupport}")
            configuration.maxSupport = maxSupport
            return this
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set. The value must be greater than 0
         */
        fun supportDelta(supportDelta: Double): Builder<ItemType> {
            ensureGreater(supportDelta, 0.0, "The support delta must be greater than 0")
            configuration.supportDelta = supportDelta
            return this
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, or 0,
         *                             if the Apriori algorithm should not try to find a specific
         *                             number of frequent item sets
         */
        fun frequentItemSetCount(frequentItemSetCount: Int): Builder<ItemType> {
            ensureAtLeast(frequentItemSetCount, 0,
                    "The number of frequent item sets must be at least 0")
            configuration.frequentItemSetCount = frequentItemSetCount
            return this
        }

        /**
         * Enables to generate association rules.
         *
         * @param minConfidence The minimum confidence, which must at least be reached by
         *                      association rules. The confidence must at  least be 0 and at maximum
         *                      1
         */
        fun generateRules(minConfidence: Double): RuleGeneratorBuilder<ItemType> {
            return RuleGeneratorBuilder(this, minConfidence)
        }

        /**
         * Enables to generate association rules.
         *
         * @param ruleCount The number of association rules, the Apriori algorithm should try to
         *                  generate, or 0, if the algorithm should not try to generate a specific
         *                  number of association rules
         */
        fun generateRules(ruleCount: Int): RuleGeneratorBuilder<ItemType> {
            return RuleGeneratorBuilder(this, ruleCount)
        }

    }

    /**
     * A builder that allows to configure and create instances of the class [Apriori], which are
     * configured to generate association rules.
     *
     * @param ItemType The type of the items, which are processed by the algorithm
     */
    class RuleGeneratorBuilder<ItemType : Item> : AbstractBuilder<ItemType> {

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori], which are configured to generate association rules.
         *
         * @param builder       The builder, which has previously been used to configure the Apriori
         *                      algorithm
         * @param minConfidence The minimum confidence, which must at least be reached by
         *                      association rules. The confidence must be greater than the minimum
         *                      confidence and at maximum 1
         */
        internal constructor(builder: AbstractBuilder<ItemType>, minConfidence: Double) :
                super(builder) {
            configuration.generateRules = true
            minConfidence(minConfidence)
        }

        /**
         * Creates a new builder that allows to configure and create instances of the class
         * [Apriori], which are configured to generate association rules.
         *
         * @param builder   The builder, which has previously been used to configure the Apriori
         *                  algorithm
         * @param ruleCount The number of association rules, the Apriori algorithm should try to
         *                  generate, or 0, if the algorithm should not try to generate a specific
         *                  number of association rules
         */
        internal constructor(builder: AbstractBuilder<ItemType>, ruleCount: Int) : super(builder) {
            configuration.generateRules = true
            ruleCount(ruleCount)
        }

        /**
         * Sets the minimum support that must at least be reached by an item set to be considered
         * frequent.
         *
         * @param minSupport The support, which should be set. The support must at least be 0 and at
         *                   maximum 1
         */
        fun minSupport(minSupport: Double): RuleGeneratorBuilder<ItemType> {
            ensureAtLeast(minSupport, 0.0, "The minimum support must be at least 0")
            ensureAtMaximum(minSupport, configuration.maxSupport,
                    "The minimum support must be at maximum ${configuration.maxSupport}")
            configuration.minSupport = minSupport
            return this
        }

        /**
         * Sets the minimum support that should initially be used, when trying to find a specific
         * number of frequent item sets.
         *
         * @param maxSupport The support, which should be set. The support must be at least the
         *                   minimum support
         */
        fun maxSupport(maxSupport: Double): RuleGeneratorBuilder<ItemType> {
            ensureAtMaximum(maxSupport, 1.0, "The maximum support must be at maximum 1")
            ensureAtLeast(maxSupport, configuration.minSupport,
                    "The maximum support must be at least ${configuration.minSupport}")
            configuration.maxSupport = maxSupport
            return this
        }

        /**
         * Sets the value, the minimum support should be decreased by after each iteration, when
         * trying to find a specific number of frequent item sets.
         *
         * @param supportDelta The value, which should be set. The value must be greater than 0
         */
        fun supportDelta(supportDelta: Double): RuleGeneratorBuilder<ItemType> {
            ensureGreater(supportDelta, 0.0, "The support delta must be greater than 0")
            configuration.supportDelta = supportDelta
            return this
        }

        /**
         * Sets the number of of frequent item sets, the Apriori algorithm should try to find.
         *
         * @param frequentItemSetCount The number of frequent item sets, which should be set, or 0,
         *                             if the Apriori algorithm should not try to find a specific
         *                             number of frequent item sets
         */
        fun frequentItemSetCount(frequentItemSetCount: Int): RuleGeneratorBuilder<ItemType> {
            ensureAtLeast(frequentItemSetCount, 0,
                    "The number of frequent item sets must be at least 0")
            configuration.frequentItemSetCount = frequentItemSetCount
            return this
        }

        /**
         * Sets the minimum confidence that must at least be reached by association rules.
         *
         * @param minConfidence The confidence, which should be set. The confidence must be at least
         *                      0 and at maximum 1
         */
        fun minConfidence(minConfidence: Double): RuleGeneratorBuilder<ItemType> {
            ensureAtLeast(minConfidence, 0.0, "The minimum confidence must be at least 0")
            ensureAtMaximum(minConfidence, configuration.maxConfidence,
                    "The minimum confidence must be at maximum ${configuration.maxConfidence}")
            configuration.minConfidence = minConfidence
            return this
        }

        /**
         * Sets the minimum confidence that should initially be used, when trying to generate a
         * specific number of association rules.
         *
         * @param maxConfidence The confidence, which should be set. The confidence must be at least
         *                      0 and at maximum 1
         */
        fun maxConfidence(maxConfidence: Double): RuleGeneratorBuilder<ItemType> {
            ensureAtMaximum(maxConfidence, 1.0, "The max confidence must be at maximum 1")
            ensureAtLeast(maxConfidence, configuration.minConfidence,
                    "The max confidence must be at least ${configuration.minConfidence}")
            configuration.maxConfidence = maxConfidence
            return this
        }

        /**
         * Sets the value, the minimum confidence should be decreased by after each iteration, when
         * trying to generate a specific number of association rules.
         *
         * @param confidenceDelta The value, which should be set. The value must be greater than 0
         */
        fun confidenceDelta(confidenceDelta: Double): RuleGeneratorBuilder<ItemType> {
            ensureGreater(confidenceDelta, 0.0, "The confidence delta must be greater than 0")
            configuration.confidenceDelta = confidenceDelta
            return this
        }

        /**
         * Sets the number of association rule, the Apriori algorithm should try to generate.
         *
         * @param ruleCount The number of association rules, which should be set, or 0, if the
         *                  Apriori algorithm should not try to generate a specific number of
         *                  association rules
         */
        fun ruleCount(ruleCount: Int): RuleGeneratorBuilder<ItemType> {
            ensureAtLeast(ruleCount, 0, "The rule count must be at least 0")
            configuration.ruleCount = ruleCount
            return this
        }

    }

    internal constructor(configuration: Configuration) :
            this(configuration, FrequentItemSetMinerTask(configuration),
                    AssociationRuleGeneratorTask(configuration))

    /**
     * Executes the Apriori algorithm on a specific set of transactions in order to learn
     * association rules, which specify frequent item sets.
     *
     * @param iterable An iterable, which allows to iterate the transactions
     * @return The rule set that contains the association rules, which have been learned by the
     * algorithm, or an empty rule set, if no association rules have been learned
     */
    fun execute(iterable: Iterable<Transaction<ItemType>>): Output<ItemType> {
        LOGGER.info("Starting Apriori algorithm...")
        val startTime = System.currentTimeMillis()
        val frequentItemSets = frequentItemSetMinerTask.findFrequentItemSets(iterable)
        var ruleSet: RuleSet<ItemType>? = null

        if (configuration.generateRules) {
            ruleSet = associationRuleGeneratorTask.generateAssociationRules(frequentItemSets)
        }

        val sortedItemSets = FrequentItemSets<ItemType>(reverseOrder())
        frequentItemSets.values.forEach { x -> sortedItemSets.add(ItemSet(x)) }
        val endTime = System.currentTimeMillis()
        val output = Output(configuration, startTime, endTime, sortedItemSets, ruleSet)
        LOGGER.info("Apriori algorithm terminated after {} milliseconds", output.runtime)
        return output
    }

}
