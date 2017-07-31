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

import de.mrapp.apriori.metrics.Confidence;
import de.mrapp.apriori.metrics.Leverage;
import de.mrapp.apriori.metrics.Lift;
import de.mrapp.apriori.metrics.Support;
import de.mrapp.util.datastructure.SortedArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import static de.mrapp.util.Condition.ensureGreater;
import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class RuleSet<ItemType extends Item> extends
        SortedArraySet<AssociationRule<ItemType>> implements Serializable, Cloneable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty rule set.
     *
     * @param comparator The comparator, which should be used to sort the set, as as an instance of
     *                   the type {@link Comparator} or null, if the natural ordering should be
     *                   used
     */
    public RuleSet(@Nullable final Comparator<? super AssociationRule<ItemType>> comparator) {
        super(comparator);
    }

    /**
     * Creates a new rule set.
     *
     * @param rules      A collection, which contains the rules, which should be added to the rule
     *                   set, as an instance of the type {@link Collection} or an empty collection,
     *                   if no rules should be added
     * @param comparator The comparator, which should be used to sort the set, as as an instance of
     *                   the type {@link Comparator} or null, if the natural ordering should be
     *                   used
     */
    protected RuleSet(@NotNull final Collection<AssociationRule<ItemType>> rules,
                      @Nullable final Comparator<? super AssociationRule<ItemType>> comparator) {
        super(rules, comparator);
    }

    /**
     * Sorts the rules, which are contained by the rule set, by their heuristic values according to
     * a specific operator or metric in descending order. By default, no tie-breaking strategy is
     * used.
     *
     * @param operator The operator, which should be used to calculate the heuristic values of the
     *                 rules, as an instance of the type {@link Operator}. The operator may not be
     *                 null
     * @return A new rule set, which contains the sorted rules, as an instance of the class {@link
     * RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> sort(@NotNull final Operator operator) {
        return sort(operator, new TieBreaker());
    }

    /**
     * Sorts the rules, which are contained by the rule set, by their heuristic values according to
     * a specific operator or metric in descending order.
     *
     * @param operator   The operator, which should be used to calculate the heuristic values of the
     *                   rules, as an instance of the type {@link Operator}. The operator may not be
     *                   null
     * @param tieBreaker The tie-breaking strategy, which should be used, as an instance of the
     *                   class {@link TieBreaker}. The tie-breaking strategy may not be null
     * @return A new rule set, which contains the sorted rules, as an instance of the class {@link
     * RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> sort(@NotNull final Operator operator,
                                        @NotNull final TieBreaker tieBreaker) {
        ensureNotNull(operator, "The operator may not be null");
        ensureNotNull(tieBreaker, "The tie-breaking strategy may not be null");
        return new RuleSet<>(this, new AssociationRule.Comparator(operator, tieBreaker).reversed());
    }

    /**
     * Filters all rules, which are contained by the rule set, whose heuristic value according to a
     * specific operator or metric is greater or equal than a specific threshold. By default, no
     * tie-breaking strategy is used.
     *
     * @param operator  The operator, which should be used to calculate the heuristic value of the
     *                  rules, as an instance of the type {@link Metric}. The metric may not be
     *                  null
     * @param threshold The threshold, which must be reached by the filtered rules, as a {@link
     *                  Double} value. The threshold must be greater than 0
     * @return A new rule set, which contains the rules, which reach the given threshold, as an
     * instance of the class {@link RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> filter(@NotNull final Operator operator,
                                          final double threshold) {
        return filter(operator, threshold, new TieBreaker());
    }

    /**
     * Filters all rules, which are contained by the rule set, whose heuristic value according to a
     * specific operator or metric is greater or equal than a specific threshold.
     *
     * @param operator   The operator, which should be used to calculate the heuristic value of the
     *                   rules, as an instance of the type {@link Metric}. The metric may not be
     *                   null
     * @param threshold  The threshold, which must be reached by the filtered rules, as a {@link
     *                   Double} value. The threshold must be greater than 0
     * @param tieBreaker The tie-breaking strategy, which should be used, as an instance of the
     *                   class {@link TieBreaker}. The tie-breaking strategy may not be null
     * @return A new rule set, which contains the rules, which reach the given threshold, as an
     * instance of the class {@link RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> filter(@NotNull final Operator operator,
                                          final double threshold,
                                          @NotNull final TieBreaker tieBreaker) {
        ensureNotNull(operator, "The operator may not be null");
        ensureGreater(threshold, 0, "The threshold must be greater than 0");
        ensureNotNull(tieBreaker, "The tie-breaking strategy may not be null");
        RuleSet<ItemType> filteredRuleSet = new RuleSet<>(
                new AssociationRule.Comparator(operator, tieBreaker).reversed());

        for (AssociationRule<ItemType> rule : this) {
            double heuristicValue = operator.evaluate(rule);

            if (heuristicValue >= threshold) {
                filteredRuleSet.add(rule);
            }
        }

        return filteredRuleSet;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public final RuleSet<ItemType> clone() {
        return new RuleSet<>(this, comparator());
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
            stringBuilder.append(decimalFormat.format(new Support().evaluate(rule)));
            stringBuilder.append(", confidence = ");
            stringBuilder.append(decimalFormat.format(new Confidence().evaluate(rule)));
            stringBuilder.append(", lift = ");
            stringBuilder.append(decimalFormat.format(new Lift().evaluate(rule)));
            stringBuilder.append(", leverage = ");
            stringBuilder.append(decimalFormat.format(new Leverage().evaluate(rule)));
            stringBuilder.append(")");

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}