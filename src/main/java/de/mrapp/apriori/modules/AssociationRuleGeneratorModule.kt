/*
 * Copyright 2017 - 2018 Michael Rapp
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
package de.mrapp.apriori.modules

import de.mrapp.apriori.*
import de.mrapp.apriori.metrics.Confidence
import de.mrapp.util.Condition.ensureAtLeast
import de.mrapp.util.Condition.ensureAtMaximum
import org.slf4j.LoggerFactory

/**
 * A module, which allows to generate association rules from frequent item sets. An association rule
 * specifies, that if certain items occur in a transaction, other items do also occur with a certain
 * probability. Among all possible association rules, which can be generated from the frequent item
 * sets, only those, whose "interestingly" according to the confidence metric is greater or equal
 * than a specific threshold, are taken into account.
 *
 * In order to prune the search for association rules, the algorithm exploits the anti-monotonicity
 * property of the confidence metric, which states, that the confidence of the rule A,B -&gt; C is
 * an upper bound to the confidence of a rule A -&gt; B,C. Consequently, the algorithm starts by
 * generating rules, which contain a single item in there heads. Based on those rules, which reach
 * the minimum confidence, additional rules are created by moving items from their bodies to the
 * heads. For each rule said process is continued until the minimum threshold cannot be reached
 * anymore.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
class AssociationRuleGeneratorModule<ItemType : Item> : AssociationRuleGenerator<ItemType> {

    companion object {

        /**
         * The SLF4J logger, which is used by the module.
         */
        private val LOGGER = LoggerFactory.getLogger(AssociationRuleGeneratorModule::class.java)

    }

    /**
     * Generates association rules from a specific [itemSet] by moving items from a rule's [body]
     * to its [head]. This method is executed recursively until the resulting rule does not reach the
     * minimum confidence anymore.
     */
    private fun generateRules(itemSet: ItemSet<ItemType>,
                              frequentItemSets: Map<Int, ItemSet<ItemType>>,
                              ruleSet: RuleSet<ItemType>,
                              body: ItemSet<ItemType>,
                              head: ItemSet<ItemType>?, minConfidence: Double) {
        for (item in body) {
            val headItemSet = head?.let{ ItemSet(it) } ?: ItemSet()
            headItemSet.add(item)
            val bodyItemSet = ItemSet(body)
            bodyItemSet.remove(item)
            bodyItemSet.support = frequentItemSets[bodyItemSet.hashCode()]!!.support
            headItemSet.support = frequentItemSets[headItemSet.hashCode()]!!.support
            val rule = AssociationRule(bodyItemSet, headItemSet, itemSet.support)
            val confidence = Confidence().evaluate(rule)

            if (confidence >= minConfidence) {
                ruleSet.add(rule)

                if (bodyItemSet.size > 1) {
                    generateRules(itemSet, frequentItemSets, ruleSet, bodyItemSet, headItemSet,
                            minConfidence)
                }
            }
        }
    }

    override fun generateAssociationRules(frequentItemSets: Map<Int, ItemSet<ItemType>>,
                                          minConfidence: Double): RuleSet<ItemType> {
        ensureAtLeast(minConfidence, 0.0, "The minimum confidence must be at least 0")
        ensureAtMaximum(minConfidence, 1.0, "The minimum confidence must be at maximum 1")
        LOGGER.debug("Generating association rules...")
        val ruleSet = RuleSet<ItemType>(Sorting.forAssociationRules())
        frequentItemSets.filterValues { it.size > 1 }.forEach {
            generateRules(it.value, frequentItemSets, ruleSet, it.value, null, minConfidence)
        }
        LOGGER.debug("Generated {} association rules", ruleSet.size)
        LOGGER.debug("Rule set = {}", ruleSet)
        return ruleSet
    }

}