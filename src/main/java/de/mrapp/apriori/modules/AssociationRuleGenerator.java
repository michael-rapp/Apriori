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

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.RuleSet;
import de.mrapp.apriori.metrics.Confidence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A module, which allows to generate association rules from frequent item sets. An association rule
 * specifies, that if certain items occur in a transaction, other items do also occur with a certain
 * probability. Among all possible association rules, which can be generated from the frequent item
 * sets, only those, whose "interestingly" according to the confidence metric is greater or equal
 * than a specific threshold, are taken into account.
 *
 * In order to prune the search for association rules, the algorithm exploits the anti-monotonicity
 * property of the confidence metric, which states, that the confidence of the rule A,B -> C is an
 * upper bound to the confidence of a rule A -> B,C. Consequently, the algorithm starts by
 * generating rules, which contain a single item in there heads. Based on those rules, which reach
 * the minimum confidence, additional rules are created by moving items from their bodies to the
 * heads. For each rule said process is continued until the minimum threshold cannot be
 * reached anymore.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class AssociationRuleGenerator<ItemType extends Item> {

    /**
     * The SLF4J logger, which is used by the module.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationRuleGenerator.class);

    /**
     * The confidence, which must at least be reached by association rules to be considered
     * "interesting".
     */
    private final double minConfidence;

    /**
     * Generates association rules from a specific item set.
     *
     * @param itemSet          The item set, the association rules should be created from, as an
     *                         instance of the class {@link ItemSet}. The item set may not be null
     * @param frequentItemSets A map, which contains all available frequent item sets, as an
     *                         instance of the type {@link Map} or an empty map, if no frequent item
     *                         sets are available. The map must store the frequent item sets as
     *                         values and their hash codes as the corresponding keys
     * @param rules            The set, the generated rules should be added to, as an instance of
     *                         the type {@link Set}. The set may not be null
     */
    private void generateRules(@NotNull final ItemSet<ItemType> itemSet,
                               @NotNull final Map<Integer, ItemSet<ItemType>> frequentItemSets,
                               @NotNull final Set<AssociationRule<ItemType>> rules) {
        generateRules(itemSet, frequentItemSets, rules, itemSet, null);
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
     * @param rules            The set, the generated rules should be added to, as an instance of
     *                         the type {@link Set}. The set may not be null
     * @param body             The body, the items, which should be moved to the head, should be
     *                         taken from, as an instance of the class {@link ItemSet}. The body may
     *                         not be null
     * @param head             The head, the items, which are taken from the given body, should be
     *                         moved to, as an instance of the class {@link ItemSet} or null, if an
     *                         empty head should be created
     */
    private void generateRules(@NotNull final ItemSet<ItemType> itemSet,
                               @NotNull final Map<Integer, ItemSet<ItemType>> frequentItemSets,
                               @NotNull final Set<AssociationRule<ItemType>> rules,
                               @NotNull final ItemSet<ItemType> body,
                               @Nullable final ItemSet<ItemType> head) {
        for (ItemType item : body) {
            ItemSet<ItemType> headItemSet =
                    head != null ? new ItemSet<>(head) : new ItemSet<>();
            headItemSet.add(item);
            ItemSet<ItemType> bodyItemSet = new ItemSet<>(body);
            bodyItemSet.remove(item);
            bodyItemSet.setSupport(frequentItemSets.get(bodyItemSet.hashCode()).getSupport());
            headItemSet.setSupport(frequentItemSets.get(headItemSet.hashCode()).getSupport());
            double support = itemSet.getSupport();
            AssociationRule<ItemType> rule = new AssociationRule<>(bodyItemSet,
                    headItemSet, support);
            double confidence = new Confidence().evaluate(rule);

            if (confidence >= minConfidence) {
                rules.add(rule);

                if (bodyItemSet.size() > 1) {
                    generateRules(itemSet, frequentItemSets, rules, bodyItemSet, headItemSet);
                }
            }
        }
    }

    /**
     * Creates a new module, which allows to generate association rules from frequent item sets.
     *
     * @param minConfidence The confidence, which must at least be reached by association rules to
     *                      be considered "interesting", as a {@link Double} value. The confidence
     *                      must be at least 0 and at maximum 1
     */
    public AssociationRuleGenerator(final double minConfidence) {
        // TODO: Throw exceptions
        this.minConfidence = minConfidence;
    }

    /**
     * Returns the confidence, which must at least be reached by association rules to be considered
     * "interesting".
     *
     * @return The confidence, which must at least be reached by association rules to be considered
     * "interesting as a {@link Double} value. The confidence must at least be 0 and at maximum 1
     */
    public final double getMinConfidence() {
        return minConfidence;
    }

    /**
     * Generates association rules from frequent item sets.
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
            @NotNull final Map<Integer, ItemSet<ItemType>> frequentItemSets) {
        LOGGER.debug("Generating association rules");
        Set<AssociationRule<ItemType>> rules = new HashSet<>();

        for (ItemSet<ItemType> itemSet : frequentItemSets.values()) {
            if (itemSet.size() > 1) {
                generateRules(itemSet, frequentItemSets, rules);
            }
        }

        RuleSet<ItemType> ruleSet = new RuleSet<>(rules);
        LOGGER.debug("Generated {} association rules", ruleSet.size());
        LOGGER.debug("Rule set = {}", ruleSet);
        return ruleSet;
    }

}