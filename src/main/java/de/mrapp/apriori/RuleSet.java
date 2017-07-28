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
import java.util.*;

import static de.mrapp.util.Condition.ensureGreater;
import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class RuleSet<ItemType extends Item> implements SortedSet<AssociationRule<ItemType>>,
        Serializable, Cloneable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A sorted set, which contains the rules, the rule set consists of.
     */
    public final SortedSet<AssociationRule<ItemType>> rules;

    /**
     * Creates an empty rule set.
     */
    public RuleSet() {
        this(new SortedArraySet<>(new AssociationRule.Comparator(new Confidence()).reversed()));
    }

    /**
     * Creates a new rule set from a sorted set.
     *
     * @param rules The sorted set, the rule set should be created from, as an instance of the type
     *              {@link SortedSet}. The sorted set may not be null
     */
    protected RuleSet(@NotNull final SortedSet<AssociationRule<ItemType>> rules) {
        ensureNotNull(rules, "The rules may not be null");
        this.rules = rules;
    }

    /**
     * Sorts the rules, which are contained by the rule set, by their heuristic values according to
     * a specific operator or metric in descending order.
     *
     * @param operator The operator, which should be used to calculate the heuristic values of the
     *                 rules, as an instance of the type {@link Operator}. The operator may not be
     *                 null
     * @return A new rule set, which contains the sorted rules, as an instance of the class {@link
     * RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> sort(@NotNull final Operator operator) {
        ensureNotNull(operator, "The operator may not be null");
        SortedSet<AssociationRule<ItemType>> rules = new SortedArraySet<>(
                new AssociationRule.Comparator(operator).reversed());
        rules.addAll(this);
        return new RuleSet<>(rules);
    }

    /**
     * Filters all rules, which are contained by the rule set, whose heuristic value according to a
     * specific operator or metric is greater or equal than a specific threshold.
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
        ensureNotNull(operator, "The operator may not be null");
        ensureGreater(threshold, 0, "The threshold must be greater than 0");
        SortedSet<AssociationRule<ItemType>> rules = new SortedArraySet<>(
                new AssociationRule.Comparator(operator).reversed());

        for (AssociationRule<ItemType> rule : this) {
            double heuristicValue = operator.evaluate(rule);

            if (heuristicValue >= threshold) {
                rules.add(rule);
            }
        }

        return new RuleSet<>(rules);
    }

    @Nullable
    @Override
    public final Comparator<? super AssociationRule<ItemType>> comparator() {
        return rules.comparator();
    }

    @NotNull
    @Override
    public final SortedSet<AssociationRule<ItemType>> subSet(
            final AssociationRule<ItemType> fromElement,
            final AssociationRule<ItemType> toElement) {
        return rules.subSet(fromElement, toElement);
    }

    @NotNull
    @Override
    public final SortedSet<AssociationRule<ItemType>> headSet(
            final AssociationRule<ItemType> toElement) {
        return rules.headSet(toElement);
    }

    @NotNull
    @Override
    public final SortedSet<AssociationRule<ItemType>> tailSet(
            final AssociationRule<ItemType> fromElement) {
        return rules.tailSet(fromElement);
    }

    @Override
    public final AssociationRule<ItemType> first() {
        return rules.first();
    }

    @Override
    public final AssociationRule<ItemType> last() {
        return rules.last();
    }

    @Override
    public final int size() {
        return rules.size();
    }

    @Override
    public final boolean isEmpty() {
        return rules.isEmpty();
    }

    @Override
    public final boolean contains(final Object o) {
        return rules.contains(o);
    }

    @NotNull
    @Override
    public final Iterator<AssociationRule<ItemType>> iterator() {
        return rules.iterator();
    }

    @NotNull
    @Override
    public final Object[] toArray() {
        return rules.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @NotNull
    @Override
    public final <T> T[] toArray(@NotNull final T[] a) {
        return rules.toArray(a);
    }

    @Override
    public final boolean add(final AssociationRule<ItemType> rule) {
        return rules.add(rule);
    }

    @Override
    public final boolean remove(final Object o) {
        return rules.remove(o);
    }

    @Override
    public final boolean containsAll(@NotNull final Collection<?> c) {
        return rules.containsAll(c);
    }

    @Override
    public final boolean addAll(@NotNull final Collection<? extends AssociationRule<ItemType>> c) {
        return rules.addAll(c);
    }

    @Override
    public final boolean retainAll(@NotNull final Collection<?> c) {
        return rules.retainAll(c);
    }

    @Override
    public final boolean removeAll(@NotNull final Collection<?> c) {
        return rules.removeAll(c);
    }

    @Override
    public final void clear() {
        rules.clear();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public final RuleSet<ItemType> clone() {
        SortedSet<AssociationRule<ItemType>> clonedRules = new SortedArraySet<>(rules.comparator());
        clonedRules.addAll(rules);
        return new RuleSet<>(clonedRules);
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

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rules.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RuleSet<?> other = (RuleSet<?>) obj;
        return rules.equals(other.rules);
    }

}