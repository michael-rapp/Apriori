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
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A tie-breaking strategy, which allows to decide, which one of two item sets or association rules
 * should be sorted before the other one, if their heuristic values are equal.
 *
 * @param <T> The type of the items, the tie-breaking strategy applies to
 * @author Michael Rapp
 * @since 1.1.0
 */
public abstract class TieBreaker<T> implements Comparator<T> {

    /**
     * An abstract base class for all tie-breaking strategies.
     *
     * @param <T>              The type of the items, the tie-breaking strategy applies to
     * @param <TieBreakerType> The type of the tie-breaking strategy
     */
    private static abstract class AbstractTieBreaker<T, TieBreakerType extends TieBreaker<T>> extends
            TieBreaker<T> {

        /**
         * The tie-breaking strategy, which is used for tie-breaking before applying this
         * tie-breaking strategy.
         */
        private final TieBreakerType parent;

        /**
         * The comparator, which specifies which one of two item sets or association rules is sorted
         * before the other one, when performing tie-breaking.
         */
        private final Comparator<T> comparator;

        /**
         * Creates a new tie-breaking strategy.
         *
         * @param parent     The tie-breaking strategy, which should be used for tie breaking-before
         *                   applying this tie-breaking strategy, as an instance of the generic type
         *                   TieBreakerType or null, if no parent is available
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking, as an instance of the type {@link Comparator}. The
         *                   comparator may not be null
         */
        private AbstractTieBreaker(@Nullable final TieBreakerType parent,
                                   @NotNull final Comparator<T> comparator) {
            ensureNotNull(comparator, "The tie-breaking comparator may not be null");
            this.parent = parent;
            this.comparator = comparator;
        }

        @Override
        public final int compare(@NotNull final T o1, @NotNull final T o2) {
            if (parent != null) {
                int result = parent.compare(o1, o2);

                if (result != 0) {
                    return result;
                }
            }

            return comparator.compare(o1, o2);
        }

    }

    /**
     * A tie-breaking strategy, which applies to item sets.
     */
    public static class ItemSetTieBreaker extends AbstractTieBreaker<ItemSet, ItemSetTieBreaker> {

        /**
         * Creates a new tie-breaking strategy, which applies to item sets.
         *
         * @param parent     The tie-breaking strategy, which should be used for tie breaking-before
         *                   applying this tie-breaking strategy, as an instance of the generic type
         *                   TieBreakerType or null, if no parent is available
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking, as an instance of the type {@link Comparator}. The
         *                   comparator may not be null
         */
        private ItemSetTieBreaker(@Nullable final ItemSetTieBreaker parent,
                                  @NotNull final Comparator<ItemSet> comparator) {
            super(parent, comparator);
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers small
         * item sets over large ones. I.e., if an item set contains less items than its counterpart,
         * it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link ItemSetTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final ItemSetTieBreaker preferSmall() {
            return new ItemSetTieBreaker(this, (o1, o2) -> Integer.compare(o2.size(), o1.size()));
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers large
         * item sets over small ones. I.e., if an item set contains more items than its counterpart,
         * it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link ItemSetTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final ItemSetTieBreaker preferLarge() {
            return new ItemSetTieBreaker(this, Comparator.comparingInt(ItemSet::size));
        }

        /**
         * Adds an additional custom criteria to the current tie-breaking strategy. The given
         * comparator must return 1, if the first association rule should be preferred, -1, if the
         * second one should be preferred or 0, if no decision could be made.
         *
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking, as an instance of the type {@link Comparator}. The
         *                   comparator may not be null
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link ItemSetTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final ItemSetTieBreaker custom(
                @NotNull final Comparator<ItemSet> comparator) {
            return new ItemSetTieBreaker(this, comparator);
        }

    }

    /**
     * A tie-breaking strategy, which applies to association rules.
     */
    public static class AssociationRuleTieBreaker extends
            AbstractTieBreaker<AssociationRule, AssociationRuleTieBreaker> {

        /**
         * Creates a new tie-breaking strategy, which applies to association rules.
         *
         * @param parent     The tie-breaking strategy, which should be used for tie breaking-before
         *                   applying this tie-breaking strategy, as an instance of the generic type
         *                   TieBreakerType or null, if no parent is available
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking, as an instance of the type {@link Comparator}. The
         *                   comparator may not be null
         */
        private AssociationRuleTieBreaker(@Nullable final AssociationRuleTieBreaker parent,
                                          @NotNull final Comparator<AssociationRule> comparator) {
            super(parent, comparator);
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which performs
         * tie-breaking according to a specific operator. I.e., if an association rule reaches a
         * greater performance according to the given operator, it is sorted before the other one.
         *
         * @param operator The operator, which should be used for tie-breaking, as an instance of
         *                 the type {@link Operator}. The operator may not be null
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker byOperator(@NotNull final Operator operator) {
            ensureNotNull(operator, "The metric may not be null");
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                double h1 = operator.evaluate(o1);
                double h2 = operator.evaluate(o2);
                return Double.compare(h1, h2);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * association rules over complex ones. I.e., if an association rule contains less items
         * than its counterpart in its body and head, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferSimple() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getBody().size() + o1.getHead().size();
                int size2 = o2.getBody().size() + o2.getHead().size();
                return Integer.compare(size2, size1);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * association rules over simple ones. I.e., if an association rule contains more items than
         * its counterpart in its body and head, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferComplex() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getBody().size() + o1.getHead().size();
                int size2 = o2.getBody().size() + o2.getHead().size();
                return Integer.compare(size1, size2);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * bodies over simple ones. I.e., if an association rule contains less items than its
         * counterpart in its body, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferSimpleBody() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getBody().size();
                int size2 = o2.getBody().size();
                return Integer.compare(size2, size1);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * bodies over simple ones. I.e., if an association rule contains more items than its
         * counterpart in its body, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferComplexBody() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getBody().size();
                int size2 = o2.getBody().size();
                return Integer.compare(size1, size2);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
         * heads over simple ones. I.e., if an association rule contains less items than its
         * counterpart in its head, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferSimpleHead() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getHead().size();
                int size2 = o2.getHead().size();
                return Integer.compare(size2, size1);
            });
        }

        /**
         * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
         * heads over simple ones. I.e., if an association rule contains more items than its
         * counterpart in its head, it is sorted before the other one.
         *
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker preferComplexHead() {
            return new AssociationRuleTieBreaker(this, (o1, o2) -> {
                int size1 = o1.getHead().size();
                int size2 = o2.getHead().size();
                return Integer.compare(size1, size2);
            });
        }

        /**
         * Adds an additional custom criteria to the current tie-breaking strategy. The given
         * comparator must return 1, if the first association rule should be preferred, -1, if the
         * second one should be preferred or 0, if no decision could be made.
         *
         * @param comparator The comparator, which specifies which one of two item sets or
         *                   association rules should be sorted before the other one when performing
         *                   tie-breaking, as an instance of the type {@link Comparator}. The
         *                   comparator may not be null
         * @return The tie-breaking strategy, including the added criteria, as an instance of the
         * class {@link AssociationRuleTieBreaker}. The tie breaking-strategy may not be null
         */
        @NotNull
        public final AssociationRuleTieBreaker custom(
                @NotNull final Comparator<AssociationRule> comparator) {
            return new AssociationRuleTieBreaker(this, comparator);
        }

    }

    /**
     * Creates and returns a tie-breaking strategy, which applies to item sets. By default, no
     * tie-breaking is performed at all.
     *
     * @return The tie-breaking strategy, which has been created, as an instance of the class {@link
     * ItemSetTieBreaker}. The tie-breaking strategy may not be null
     */
    @NotNull
    public static ItemSetTieBreaker forItemSets() {
        return new ItemSetTieBreaker(null, (o1, o2) -> 0);
    }

    /**
     * Creates and returns a tie-breaking strategy, which applies to association rules. By default,
     * no tie-breaking is performed at all.
     *
     * @return The tie-breaking strategy, which has been created, as an instance of the class {@link
     * AssociationRuleTieBreaker}. The tie-breaking strategy may not be null
     */
    @NotNull
    public static AssociationRuleTieBreaker forAssociationRules() {
        return new AssociationRuleTieBreaker(null, (o1, o2) -> 0);
    }

}