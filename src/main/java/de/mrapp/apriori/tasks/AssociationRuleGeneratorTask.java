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
import de.mrapp.apriori.Sorting;
import de.mrapp.apriori.modules.AssociationRuleGenerator;
import de.mrapp.apriori.modules.AssociationRuleGeneratorModule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A task, which tries to generate a specific number of association rules.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class AssociationRuleGeneratorTask<ItemType extends Item> extends
        AbstractTask<ItemType> {

    /**
     * The association rule generator, which is used by the task.
     */
    private final AssociationRuleGenerator<ItemType> associationRuleGenerator;

    /**
     * Creates a new task, which tries to generate a specific number of association rules.
     *
     * @param configuration The configuration, which should be used by the task, as an instance of
     *                      the class {@link Configuration}. The configuration may not be null
     */
    public AssociationRuleGeneratorTask(@NotNull final Configuration configuration) {
        this(configuration, new AssociationRuleGeneratorModule<>());
    }

    /**
     * Creates a new task, which tries to generate a specific number of association rules.
     *
     * @param configuration            The configuration, which should be used by the task, as an
     *                                 instance of the class {@link Configuration}. The
     *                                 configuration may not be null
     * @param associationRuleGenerator The association rule generator, which should be used by the
     *                                 task, as an instance of the type {@link AssociationRuleGenerator}.
     *                                 The association rule generator may not be null
     */
    public AssociationRuleGeneratorTask(@NotNull final Configuration configuration,
                                        @NotNull final AssociationRuleGenerator<ItemType> associationRuleGenerator) {
        super(configuration);
        ensureNotNull(associationRuleGenerator, "The association rule generator may not be null");
        this.associationRuleGenerator = associationRuleGenerator;
    }

    /**
     * Tries to generate a specific number of association rules from frequent item sets.
     *
     * @param frequentItemSets A map, which contains all available frequent item sets, as an
     *                         instance of the type {@link Map} or an empty map, if no frequent item
     *                         sets are available. The map must store the frequent item sets as
     *                         values and their hash codes as the corresponding keys
     * @return A rule set, which contains the association rules, which have been generated, as an
     * instance of the class {@link RuleSet} or an empty rule set, if no association rules have been
     * generated
     */
    @NotNull
    public final RuleSet<ItemType> generateAssociationRules(
            @NotNull final Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets) {
        if (getConfiguration().getRuleCount() > 0) {
            RuleSet<ItemType> result = null;
            double currentMinConfidence = getConfiguration().getMaxConfidence();

            while (currentMinConfidence >= getConfiguration().getMinConfidence() &&
                    (result == null || result.size() < getConfiguration().getRuleCount())) {
                RuleSet<ItemType> ruleSet = associationRuleGenerator
                        .generateAssociationRules(frequentItemSets, currentMinConfidence);

                if (result == null || ruleSet.size() >= result.size()) {
                    result = ruleSet;
                }

                currentMinConfidence -= getConfiguration().getConfidenceDelta();
            }

            return result != null ? result : new RuleSet<>(Sorting.forAssociationRules());
        } else {
            return associationRuleGenerator.generateAssociationRules(frequentItemSets,
                    getConfiguration().getMinConfidence());
        }
    }

}