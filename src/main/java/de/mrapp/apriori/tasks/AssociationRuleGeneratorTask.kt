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
package de.mrapp.apriori.tasks

import de.mrapp.apriori.Apriori.Configuration
import de.mrapp.apriori.Item
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.RuleSet
import de.mrapp.apriori.Sorting
import de.mrapp.apriori.modules.AssociationRuleGenerator
import de.mrapp.apriori.modules.AssociationRuleGeneratorModule

/**
 * A task, which tries to generate a specific number of association rules.
 *
 * @param    ItemType                 The type of the items, which are processed by the algorithm
 * @property configuration            The configuration that is used by the task
 * @property associationRuleGenerator The association rule generator that is used by the task
 * @author Michael Rapp
 * @since 1.0.0
 */
class AssociationRuleGeneratorTask<ItemType : Item> @JvmOverloads constructor(
        configuration: Configuration,
        private val associationRuleGenerator: AssociationRuleGenerator<ItemType> = AssociationRuleGeneratorModule()) :
        AbstractTask(configuration) {

    /**
     * Tries to generate a specific number of association rules from frequent item sets.
     *
     * @param frequentItemSets A ma that contains all available frequent item sets. The map must
     * store the frequent item sets as values and their hash codes as the corresponding keys
     */
    fun generateAssociationRules(frequentItemSets: Map<Int, ItemSet<ItemType>>): RuleSet<ItemType> {
        if (configuration.ruleCount > 0) {
            var result: RuleSet<ItemType>? = null
            var currentMinConfidence = configuration.maxConfidence

            while (currentMinConfidence >= configuration.minConfidence
                    && (result == null || result.size < configuration.ruleCount)) {
                val ruleSet = associationRuleGenerator
                        .generateAssociationRules(frequentItemSets, currentMinConfidence)

                if (result == null || ruleSet.size >= result.size) {
                    result = ruleSet
                }

                currentMinConfidence -= configuration.confidenceDelta
            }

            return result ?: RuleSet(Sorting.forAssociationRules())
        } else {
            return associationRuleGenerator.generateAssociationRules(frequentItemSets,
                    configuration.minConfidence)
        }
    }

}
