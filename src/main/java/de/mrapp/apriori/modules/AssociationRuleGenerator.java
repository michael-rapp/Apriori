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

import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.RuleSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Defines the interface, a class, which allows to generate association rules, must implement.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface AssociationRuleGenerator<ItemType extends Item> {

    /**
     * Generates association rules from frequent item sets.
     *
     * @param frequentItemSets A map, which contains all available frequent item sets, as an
     *                         instance of the type {@link Map} or an empty map, if no frequent item
     *                         sets are available. The map must store the frequent item sets as
     *                         values and their hash codes as the corresponding keys
     * @param minConfidence    The minimum confidence, which must at least be reached by association
     *                         rules, as a {@link Double} value. The confidence must be at least 0
     *                         and at maximum 1
     * @return A rule set, which contains the association rules, which have been generated, as an
     * instance of the class {@link RuleSet} or an empty rule set, if no association rules have been
     * generated
     */
    @NotNull
    RuleSet<ItemType> generateAssociationRules(
            @NotNull Map<Integer, ? extends ItemSet<ItemType>> frequentItemSets,
            double minConfidence);

}