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
     * Creates an empty rule set.
     */
    public RuleSet() {
        // TODO: Throw exceptions
        this.rules = new TreeSet<>(
                new AssociationRule.Comparator<ItemType>(new Confidence()).reversed());
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