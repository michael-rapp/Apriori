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

import de.mrapp.apriori.datastructure.Filterable;
import de.mrapp.apriori.datastructure.Sortable;
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
import java.util.function.Predicate;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class RuleSet<ItemType extends Item> extends
        SortedArraySet<AssociationRule<ItemType>> implements
        Sortable<RuleSet<ItemType>, AssociationRule>,
        Filterable<RuleSet<ItemType>, AssociationRule>, Serializable, Cloneable {

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

    @NotNull
    @Override
    public final RuleSet<ItemType> sort(@Nullable final Comparator<AssociationRule> comparator) {
        return new RuleSet<>(this, comparator);
    }

    @NotNull
    @Override
    public final RuleSet<ItemType> filter(@NotNull final Predicate<AssociationRule> predicate) {
        ensureNotNull(predicate, "The predicate may not be null");
        RuleSet<ItemType> filteredRuleSet = new RuleSet<>(comparator());

        for (AssociationRule<ItemType> item : this) {
            if (predicate.test(item)) {
                filteredRuleSet.add(item);
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