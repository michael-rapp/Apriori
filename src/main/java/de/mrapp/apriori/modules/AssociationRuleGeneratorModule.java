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
package de.mrapp.apriori.modules;

import de.mrapp.apriori.*;
import de.mrapp.apriori.metrics.Confidence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static de.mrapp.util.Condition.*;

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
public class AssociationRuleGeneratorModule<ItemType extends Item> implements
        AssociationRuleGenerator<ItemType> {

    /**
     * The SLF4J logger, which is used by the module.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AssociationRuleGeneratorModule.class);

    /**
     * Generates association rules from a specific item set.
     *
     * @param itemSet          The item set, the association rules should be created from, as an
     *                         instance of the class {@link ItemSet}. The item set may not be null
     * @param frequentItemSets A map, which contains all available frequent item sets, as an
     *                         instance of the type {@link Map} or an empty map, if no frequent item
     *                         sets are available. The map must store the frequent item sets as
     *                         values and their hash codes as the corresponding keys
     * @param ruleSet          The rule set, the generated rules should be added to, as an instance
     *                         of the class {@link RuleSet}. The rule set may not be null
     * @param minConfidence    The minimum confidence, which must at least be reached by association
     *                         rules, as a {@link Double} value. The confidence must be at least 0
     *                         and at maximum 1
     */
    private void generateRules(@NotNull final ItemSet<ItemType> itemSet,
                               @NotNull final Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets,
                               @NotNull final RuleSet<ItemType> ruleSet,
                               final double minConfidence) {
        generateRules(itemSet, frequentItemSets, ruleSet, itemSet, null, minConfidence);
    }

    /**
     * Generates association rules from a specific item set by moving items from a rule's body to
     * its head. This method is executed recursively until the resulting rule does not reach the
     * minimum confidence anymore.
     *
     * @param itemSet          The item set, the association rules should be created from, as an
     *                         instance of the class {@link ItemSet}. The item set may not be null
     * @param frequentItemSets A map, which contains all available frequent item sets, as an
     *                         instance of the type {@link Map} or an empty map, if no frequent item
     *                         sets are available. The map must store the frequent item sets as
     *                         values and their hash codes as the corresponding keys
     * @param ruleSet          The rule set, the generated rules should be added to, as an instance
     *                         of the class {@link RuleSet}. The rule set may not be null
     * @param body             The body, the items, which should be moved to the head, should be
     *                         taken from, as an instance of the class {@link ItemSet}. The body may
     *                         not be null
     * @param head             The head, the items, which are taken from the given body, should be
     *                         moved to, as an instance of the class {@link ItemSet} or null, if an
     *                         empty head should be created
     * @param minConfidence    The minimum confidence, which must at least be reached by association
     *                         rules, as a {@link Double} value. The confidence must be at least 0
     *                         and at maximum 1
     */
    private void generateRules(@NotNull final ItemSet<ItemType> itemSet,
                               @NotNull final Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets,
                               @NotNull final RuleSet<ItemType> ruleSet,
                               @NotNull final ItemSet<ItemType> body,
                               @Nullable final ItemSet<ItemType> head, final double minConfidence) {
        for (ItemType item : body) {
            ItemSet<ItemType> headItemSet = head != null ? head.clone() : new ItemSet<>();
            headItemSet.add(item);
            ItemSet<ItemType> bodyItemSet = body.clone();
            bodyItemSet.remove(item);
            bodyItemSet.setSupport(frequentItemSets.get(bodyItemSet.hashCode()).getSupport());
            headItemSet.setSupport(frequentItemSets.get(headItemSet.hashCode()).getSupport());
            double support = itemSet.getSupport();
            AssociationRule<ItemType> rule = new AssociationRule<>(bodyItemSet,
                    headItemSet, support);
            double confidence = new Confidence().evaluate(rule);

            if (confidence >= minConfidence) {
                ruleSet.add(rule);

                if (bodyItemSet.size() > 1) {
                    generateRules(itemSet, frequentItemSets, ruleSet, bodyItemSet, headItemSet,
                            minConfidence);
                }
            }
        }
    }

    @NotNull
    @Override
    public final RuleSet<ItemType> generateAssociationRules(
            @NotNull final Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets,
            final double minConfidence) {
        ensureNotNull(frequentItemSets, "The frequent item sets may not be null");
        ensureAtLeast(minConfidence, 0, "The minimum confidence must be at least 0");
        ensureAtMaximum(minConfidence, 1, "The minimum confidence must be at maximum 1");
        LOGGER.debug("Generating association rules");
        RuleSet<ItemType> ruleSet = new RuleSet<>(Sorting.forAssociationRules());

        for (ItemSet<ItemType> itemSet : frequentItemSets.values()) {
            if (itemSet.size() > 1) {
                generateRules(itemSet, frequentItemSets, ruleSet, minConfidence);
            }
        }

        LOGGER.debug("Generated {} association rules", ruleSet.size());
        LOGGER.debug("Rule set = {}", ruleSet);
        return ruleSet;
    }

}