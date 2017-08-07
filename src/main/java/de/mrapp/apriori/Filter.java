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

import java.util.function.Predicate;

import static de.mrapp.util.Condition.*;

/**
 * A filter, which can be applied to item sets or association rules.
 *
 * @param <T> The type of the items, the filter applies to
 * @author Michael Rapp
 * @since 1.2.0
 */
public abstract class Filter<T> implements Predicate<T> {

    /**
     * An abstract base class for all filters.
     *
     * @param <T>          The type of the items, the filter applies to
     * @param <FilterType> The type of the filter
     */
    private static abstract class AbstractFilter<T, FilterType extends Filter<T>> extends
            Filter<T> {

        /**
         * The parent of the filter. When applied to an item, all parents of the filter are tested.
         */
        protected final Predicate<T> parent;

        /**
         * The predicate, which is used to test items.
         */
        protected final Predicate<T> predicate;

        /**
         * Creates a new filter.
         *
         * @param parent    The parent of the filter as an instance of the generic type FilterType
         *                  or null, if no parent is available
         * @param predicate The predicate, which should be used to test items, as an instance of the
         *                  type {@link Predicate}. The predicate may not be null
         */
        private AbstractFilter(@Nullable final FilterType parent,
                               @NotNull final Predicate<T> predicate) {
            ensureNotNull(predicate, "The predicate may not be null");
            this.parent = parent;
            this.predicate = predicate;
        }

    }

    /**
     * A filter, which applies to item sets.
     */
    public static class ItemSetFilter extends AbstractFilter<ItemSet, ItemSetFilter> {

        /**
         * Creates a new filter, which applies to item sets.
         *
         * @param parent    The parent o fhte filter as an instance of the generic type FilterType
         *                  or null, if no parent is available
         * @param predicate The predicate, which should be used to test items, as an instance of the
         *                  type {@link Predicate}. The predicate may not be null
         */
        public ItemSetFilter(@Nullable final ItemSetFilter parent,
                             @NotNull final Predicate<ItemSet> predicate) {
            super(parent, predicate);
        }

        /**
         * Sets the minimum support, which must be reached by an item set to be accepted by the
         * filter.
         *
         * @param minSupport The minimum support, which should be set, as a {@link Double} value.
         *                   The minimum support must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * ItemSetFilter}. The filter may not be null
         */
        @NotNull
        public final ItemSetFilter bySupport(final double minSupport) {
            return bySupport(minSupport, 1);
        }

        /**
         * Sets the minimum and maximum support, which must be reached by an item set to be accepted
         * by the filter.
         *
         * @param minSupport The minimum support, which should be set, as a {@link Double} value.
         *                   The minimum support must be at least 0
         * @param maxSupport The maximum support, which should be set, as a {@link Double} value.
         *                   The maximum support must be at least the minimum support and at maximum
         *                   1
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * ItemSetFilter}. The filter may not be null
         */
        @NotNull
        public final ItemSetFilter bySupport(final double minSupport, final double maxSupport) {
            ensureAtLeast(minSupport, 0, "The minimum support must be at least 0");
            ensureAtMaximum(minSupport, 1, "The minimum support must be at least 1");
            ensureAtMaximum(maxSupport, 1, "The maximum support must be at least 1");
            ensureAtLeast(maxSupport, minSupport,
                    "The maximum support must be at least the minimum support");
            return new ItemSetFilter(this,
                    x -> x.getSupport() >= minSupport && x.getSupport() <= maxSupport);
        }

        /**
         * Sets the minimum size, an item set must have to be accepted by the filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * ItemSetFilter}. The filter may not be null
         */
        @NotNull
        public final ItemSetFilter bySize(final int minSize) {
            return bySize(minSize, Integer.MAX_VALUE);
        }

        /**
         * Sets the minimum and maximum size, an item set must have to be accepted by the filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @param maxSize The maximum size, which should be set, as an {@link Integer} value. The
         *                maximum size must be at least the minimum size
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * ItemSetFilter}. The filter may not be null
         */
        @NotNull
        public final ItemSetFilter bySize(final int minSize, final int maxSize) {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0");
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size");
            return new ItemSetFilter(this, x -> x.size() >= minSize && x.size() <= maxSize);
        }

        @Override
        public final boolean test(@NotNull final ItemSet t) {
            ensureNotNull(t, "The item set may not be null");
            return predicate.test(t) && (parent == null || parent.test(t));
        }

    }

    /**
     * A filter, which applies to association rules.
     */
    public static class AssociationRuleFilter extends
            AbstractFilter<AssociationRule, AssociationRuleFilter> {

        /**
         * Creates a new filter, which applies to association rules.
         *
         * @param parent    The parent of the filter as an instance of the generic type FilterType
         *                  or null, if no parent is available
         * @param predicate The predicate, which should be used to test items, as an instance of the
         *                  type {@link Predicate}. The predicate may not be null
         */
        private AssociationRuleFilter(@Nullable final AssociationRuleFilter parent,
                                      @NotNull final Predicate<AssociationRule> predicate) {
            super(parent, predicate);
        }

        /**
         * Sets the minimum performance according to a specific metric, which must be reached by an
         * association rule to be accepted by the filter.
         *
         * @param operator       The operator, which should be used to calculate the performance of
         *                       an association rule, as an instance of the type {@link Operator}.
         *                       The operator may not be null
         * @param minPerformance The minimum performance, which should be set, as a {@link Double}
         *                       value. The minimum performance must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byOperator(@NotNull final Operator operator,
                                                      final double minPerformance) {
            return byOperator(operator, minPerformance, Double.MAX_VALUE);
        }

        /**
         * Sets the minimum and maximum performance according to a specific metric, which must be
         * reached by an association rule to be accepted by the filter.
         *
         * @param operator       The operator, which should be used to calculate the performance of
         *                       an association rule, as an instance of the type {@link Operator}.
         *                       The operator may not be null
         * @param minPerformance The minimum performance, which should be set, as a {@link Double}
         *                       value. The minimum performance must be at least 0
         * @param maxPerformance The maximum performance, which should be set, as a {@link Double}
         *                       value. The maximum performance must be at least the minimum
         *                       performance
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byOperator(@NotNull final Operator operator,
                                                      final double minPerformance,
                                                      final double maxPerformance) {
            ensureNotNull(operator, "The operator may not be null");
            ensureAtLeast(minPerformance, 0, "The minimum performance must be at least 0");
            ensureAtLeast(maxPerformance, minPerformance,
                    "The maximum performance must be at least the minimum performance");
            return new AssociationRuleFilter(this, x -> {
                double h = operator.evaluate(x);
                return h >= minPerformance && h <= maxPerformance;
            });
        }

        /**
         * Sets the minimum number of items an association rule's body and head must contain to be
         * accepted by the filter.
         *
         * @param minSize The minimum number of items, which should be set, as an {@link Integer}
         *                value. The minimum number of items must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter bySize(final int minSize) {
            return bySize(minSize, Integer.MAX_VALUE);
        }

        /**
         * Sets the minimum and maximum number of items an association rule's body and head must
         * contain to be accepted by the filter.
         *
         * @param minSize The minimum number of items, which should be set, as an {@link Integer}
         *                value. The minimum number of items must be at least 0
         * @param maxSize The maximum number of items, which should be set, as an {@link Integer}
         *                value. The maximum number of items must be at least the minimum number of
         *                items
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter bySize(final int minSize, final int maxSize) {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0");
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size");
            return new AssociationRuleFilter(this, x -> {
                int size = x.getBody().size() + x.getHead().size();
                return size >= minSize && size <= maxSize;
            });
        }

        /**
         * Sets the minimum size, an association rule's body must have to be accepted by the
         * filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byBodySize(final int minSize) {
            return byBodySize(minSize, Integer.MAX_VALUE);
        }

        /**
         * Sets the minimum and maximum size, an association rule's body must have to be accepted by
         * the filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @param maxSize The maximum size, which should be set, as an {@link Integer} value. The
         *                maximum size must be at least the minimum size
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byBodySize(final int minSize, final int maxSize) {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0");
            ensureAtLeast(maxSize, minSize, "The minimum size must be at least the minimum size");
            return new AssociationRuleFilter(this,
                    x -> x.getBody().size() >= minSize && x.getBody().size() <= maxSize);
        }

        /**
         * Sets the minimum size, an association rule's head must have to be accepted by the
         * filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byHeadSize(final int minSize) {
            return byHeadSize(minSize, Integer.MAX_VALUE);
        }

        /**
         * Sets the minimum and maximum size, an association rule's head must have to be accepted by
         * the filter.
         *
         * @param minSize The minimum size, which should be set, as an {@link Integer} value. The
         *                minimum size must be at least 0
         * @param maxSize The maximum size, which should be set, as an {@link Integer} value. The
         *                maximum size must be at least the minimum size
         * @return The filter, this method has been called upon, as an instance of the class {@link
         * AssociationRuleFilter}. The filter may not be null
         */
        @NotNull
        public final AssociationRuleFilter byHeadSize(final int minSize, final int maxSize) {
            ensureAtLeast(minSize, 0, "The minimum size must be at least 0");
            ensureAtLeast(maxSize, minSize, "The maximum size must be at least the minimum size");
            return new AssociationRuleFilter(this,
                    x -> x.getHead().size() >= minSize && x.getHead().size() <= maxSize);
        }

        @Override
        public boolean test(@NotNull final AssociationRule t) {
            ensureNotNull(t, "The association rule may not be null");
            return predicate.test(t) && (parent == null || parent.test(t));
        }

    }

    /**
     * Returns a filter, which applies to item sets. By default, no item sets are filtered at all.
     *
     * @return The filter, which has been created, as an instance of the class {@link Filter}. The
     * filter may not be null
     */
    @NotNull
    public static ItemSetFilter forItemSets() {
        return new ItemSetFilter(null, x -> true);
    }

    /**
     * Returns a filter, which applies to association rules. By default, no association rules are
     * filtered at all.
     *
     * @return The filter, which has been created, as an instance of the class {@link Filter}. The
     * filter may not be null
     */
    @NotNull
    public static AssociationRuleFilter forAssociationRules() {
        return new AssociationRuleFilter(null, x -> true);
    }

}