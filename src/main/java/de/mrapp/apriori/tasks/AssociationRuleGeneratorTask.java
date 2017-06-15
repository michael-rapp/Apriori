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
package de.mrapp.apriori.tasks;

import de.mrapp.apriori.Apriori.Configuration;
import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.RuleSet;
import de.mrapp.apriori.modules.AssociationRuleGenerator;
import de.mrapp.apriori.modules.AssociationRuleGeneratorModule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A task, which tries to generate a specific number of association rules.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class AssociationRuleGeneratorTask<ItemType extends Item> extends
        AbstractTask<ItemType> implements
        AssociationRuleGenerator<ItemType> {

    /**
     * Creates a new task, which tries to generate a specific number of association rules.
     *
     * @param configuration The configuration, which should be used by the task, as an instance of
     *                      the class {@link Configuration}. The configuration may not be null
     */
    public AssociationRuleGeneratorTask(@NotNull final Configuration configuration) {
        super(configuration);
    }

    @NotNull
    @Override
    public final RuleSet<ItemType> generateAssociationRules(
            @NotNull final Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets) {
        AssociationRuleGenerator<ItemType> associationRuleGenerator;

        if (getConfiguration().getRuleCount() > 0) {
            RuleSet<ItemType> result = new RuleSet<>();
            double currentMinConfidence = getConfiguration().getMaxConfidence();

            while (currentMinConfidence >= getConfiguration().getMinConfidence() &&
                    result.size() < getConfiguration().getFrequentItemSetCount()) {
                associationRuleGenerator = new AssociationRuleGeneratorModule<>(
                        currentMinConfidence);
                RuleSet<ItemType> ruleSet = associationRuleGenerator
                        .generateAssociationRules(frequentItemSets);

                if (ruleSet.size() >= result.size()) {
                    result = ruleSet;
                }

                currentMinConfidence -= getConfiguration().getSupportDelta();
            }

            return result;
        } else {
            associationRuleGenerator = new AssociationRuleGeneratorModule<>(
                    getConfiguration().getMinConfidence());
            return associationRuleGenerator.generateAssociationRules(frequentItemSets);
        }
    }

}