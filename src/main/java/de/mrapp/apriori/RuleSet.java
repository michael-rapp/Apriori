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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class RuleSet<ItemType extends Item> implements
        Iterable<AssociationRule<ItemType>>, Serializable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A set, which contains the rules, the rule set consists of.
     */
    private final Set<AssociationRule<ItemType>> rules;

    /**
     * Creates a new rule set.
     *
     * @param rules A set, which contains the rules, which should be contained by the rule set, as
     *              an instance of the type {@link Set} or an empty set, if the rule set should not
     *              contain any rules
     */
    public RuleSet(@NotNull final Set<AssociationRule<ItemType>> rules) {
        // TODO: Throw exceptions
        this.rules = rules;
    }

    /**
     * Returns the number of rules, which are contained by the rule set.
     *
     * @return The number of rules, which are contained by the rule set, as an {@link Integer} value
     */
    public final int size() {
        return rules.size();
    }

    @NotNull
    @Override
    public final Iterator<AssociationRule<ItemType>> iterator() {
        return rules.iterator();
    }

    @Override
    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(2);
        Iterator<AssociationRule<ItemType>> iterator = iterator();
        stringBuilder.append("[");

        while (iterator.hasNext()) {
            AssociationRule<ItemType> rule = iterator.next();
            stringBuilder.append(rule.toString());
            stringBuilder.append(" (support = ");
            stringBuilder.append(decimalFormat.format(rule.getSupport()));
            stringBuilder.append(", confidence = ");
            stringBuilder.append(decimalFormat.format(rule.getConfidence()));
            stringBuilder.append(", lift = ");
            stringBuilder.append(decimalFormat.format(rule.getLift()));
            stringBuilder.append(", leverage = ");
            stringBuilder.append(decimalFormat.format(rule.getLeverage()));
            stringBuilder.append(")");

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}