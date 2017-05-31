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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class RuleSet<ItemType extends Item> implements SortedSet<AssociationRule<ItemType>>,
        Serializable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A sorted set, which contains the rules, the rule set consists of.
     */
    private final SortedSet<AssociationRule<ItemType>> rules;

    /**
     * Creates a new rule set from a sorted set.
     *
     * @param rules The sorted set, the rule set should be created from, as an instance of the type
     *              {@link SortedSet}. The sorted set may not be null
     */
    private RuleSet(@NotNull final SortedSet<AssociationRule<ItemType>> rules) {
        // TODO: Throw exceptions
        this.rules = rules;
    }

    /**
     * Creates an empty rule set.
     */
    public RuleSet() {
        // TODO: Throw exceptions
        this.rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(new Confidence()).reversed());
    }

    /**
     * Sorts the rules, which are contained by the rule set, by their "interestingly" in descending
     * order according to a specific metric.
     *
     * @param metric The metric, which should be used to measure the "interestingly" of the rules,
     *               as an instance of the type {@link Metric}. The metric may not be null
     * @return A rule set, which contains the sorted rules, as an instance of the class {@link
     * RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> sort(@NotNull final Metric metric) {
        // TODO: Throw exceptions
        SortedSet<AssociationRule<ItemType>> rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(metric).reversed());
        rules.addAll(this);
        return new RuleSet<>(rules);
    }

    /**
     * SortedSet<AssociationRule<ItemType>> rules = new TreeSet<>(
     * new AssociationRule.Comparator<ItemType>(metric).reversed());
     * rules.addAll(this);
     * return new RuleSet<>(rules);
     * Sorts the rules, which are contained by the rule set, by their "interestingly" in descending
     * order according to a specific operator.
     *
     * @param operator The operator, which should be used to average the "interestingly" of the
     *                 rules, which have been calculated according to multiple metrics, as an
     *                 instance of the type {@link Metric}. The metric may not be null
     * @return A rule set, which contains the sorted rules, as an instance of the class {@link
     * RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> sort(@NotNull final Operator operator) {
        // TODO: Throw exceptions
        SortedSet<AssociationRule<ItemType>> rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(operator).reversed());
        rules.addAll(this);
        return new RuleSet<>(rules);
    }

    /**
     * Filters all rules, which are contained by the rule set, whose "interestingly" according to a
     * specific metric is greater or equal than a specific threshold.
     *
     * @param metric    The metric, which should be used to measure the "interestingly" of the
     *                  rules, as an instance of the type {@link Metric}. The metric may not be
     *                  null
     * @param threshold The threshold, which must be reached by the filtered rules, as a {@link
     *                  Double} value. The threshold must be greater than 0
     * @return A rule set, which contains the rules, which reach the given threshold, as an instance
     * of the class {@link RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> filter(@NotNull final Metric metric, final double threshold) {
        // TODO: Throw exceptions
        SortedSet<AssociationRule<ItemType>> rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(metric).reversed());

        for (AssociationRule<ItemType> rule : this) {
            double heuristicValue = metric.evaluate(rule);

            if (heuristicValue >= threshold) {
                rules.add(rule);
            }
        }

        return new RuleSet<>(rules);
    }

    /**
     * Filters all rules, which are contained by the rule set, whose "interestingly" according to a
     * specific operator is greater or equal than a specific threshold.
     *
     * @param operator  The operator, which should be used to average the "interestingly" of the
     *                  rules, which have been calculated according to multiple metrics, as an
     *                  instance of the type {@link Metric}. The metric may not be null
     * @param threshold The threshold, which must be reached by the filtered rules, as a {@link
     *                  Double} value. The threshold must be greater than 0
     * @return A rule set, which contains the rules, which reach the given threshold, as an instance
     * of the class {@link RuleSet}. The rule set may not be null
     */
    @NotNull
    public final RuleSet<ItemType> filter(@NotNull final Operator operator,
                                          final double threshold) {
        // TODO: Throw exceptions
        SortedSet<AssociationRule<ItemType>> rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(operator).reversed());

        for (AssociationRule<ItemType> rule : this) {
            double heuristicValue = operator.average(rule);

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