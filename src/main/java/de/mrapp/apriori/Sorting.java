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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A sorting, which specifies how item sets or association rules should be sorted.
 *
 * @param <T> The type of the items, the sorting applies to
 * @author Michael Rapp
 * @since 1.2.0
 */
public abstract class Sorting<T> implements Comparator<T> {

    /**
     * An abstract base class for all sortings.
     *
     * @param <T>           The type of the items, the sorting applies to
     * @param <SortingType> The type of the sorting
     */
    private static abstract class AbstractSorting<T, SortingType extends Sorting<T>> extends
            Sorting<T> {

        /**
         * The order, which is used for sorting.
         */
        protected Order order;

        /**
         * The comparator, which is used to perform tie-breaking.
         */
        protected Comparator<T> tieBreaker;

        /**
         * Returns a reference to the sorting itself.
         *
         * @return A reference to the sorting itself, implicitly casted to the generic type
         * SortingType
         */
        @SuppressWarnings("unchecked")
        protected SortingType self() {
            return (SortingType) this;
        }

        /**
         * Creates a new sorting, which specifies how item sets or association rules should be
         * sorted.
         */
        private AbstractSorting() {
            withOrder(Order.DESCENDING);
            withTieBreaking(null);
        }

        /**
         * Sets the order, which should be used for sorting.
         *
         * @param order The order, which should be set, as a value of the enum {@link Order}. The
         *              order may either be {@link Order#ASCENDING} or {@link Order#DESCENDING}
         * @return The sorting, this method has been called upon, as an instance of the generic type
         * SortingType. The sorting may not be null
         */
        @NotNull
        public SortingType withOrder(@NotNull final Order order) {
            ensureNotNull(order, "The order may not be null");
            this.order = order;
            return self();
        }

        /**
         * Sets the tie-breaking strategy, which should be used.
         *
         * @param comparator The tie-breaking strategy, which should be set, as an instance of the
         *                   type {@link Comparator} or null, if no tie-breaking should be
         *                   performed. It might be an instance of the class {@link TieBreaker}
         * @return The sorting, this method has been called upon, as an instance of the generic type
         * SortingType. The sorting may not be null
         */
        @NotNull
        public SortingType withTieBreaking(@Nullable final Comparator<T> comparator) {
            this.tieBreaker = comparator;
            return self();
        }

    }

    /**
     * A sorting, which applies to item sets.
     */
    public static class ItemSetSorting extends AbstractSorting<ItemSet, ItemSetSorting> {

        @Override
        public final int compare(final ItemSet o1, final ItemSet o2) {
            int result = Double.compare(o1.getSupport(), o2.getSupport());

            if (result == 0 && tieBreaker != null) {
                result = tieBreaker.compare(o1, o2);
            }

            return order == Order.ASCENDING ? result : result * -1;
        }

    }

    /**
     * A sorting, which applies to association rules.
     */
    public static class AssociationRuleSorting extends
            AbstractSorting<AssociationRule, AssociationRuleSorting> {

        /**
         * The operator, the association rules are sorted by.
         */
        private Operator operator;

        /**
         * Creates a new sorting, which applies to association rules.
         */
        private AssociationRuleSorting() {
            byOperator(new Confidence());
        }

        /**
         * Sets the operator, the association rules should be sorted by.
         *
         * @param operator The operator, which should be set, as an instance of the type {@link
         *                 Operator} or null, if the natural ordering should be used
         * @return The sorting, this method has been called upon, as an instance of the class {@link
         * AssociationRuleSorting}. The sorting may not be null
         */
        @NotNull
        public AssociationRuleSorting byOperator(@Nullable final Operator operator) {
            this.operator = operator;
            return self();
        }

        @Override
        public final int compare(final AssociationRule o1, final AssociationRule o2) {
            int result = operator != null ?
                    Double.compare(operator.evaluate(o1), operator.evaluate(o2)) : o1.compareTo(o2);

            if (result == 0 && tieBreaker != null) {
                result = tieBreaker.compare(o1, o2);
            }

            return order == Order.ASCENDING ? result : result * -1;
        }

    }

    /**
     * Contains all possible orders, which can be used for sorting.
     */
    public enum Order {

        /**
         * If the item sets or association rules should be sorted in ascending order.
         */
        ASCENDING,

        /**
         * If the item sets or association rules should be sorted in descending order.
         */
        DESCENDING

    }

    /**
     * Creates and returns a sorting for item sets.
     *
     * @return The sorting, which has been created, as an instance of the class {@link Sorting}. The
     * sorting may not be null
     */
    @NotNull
    public static ItemSetSorting forItemSets() {
        return new ItemSetSorting();
    }

    /**
     * Creates and returns a sorting for association rules.
     *
     * @return The sorting, which has been created, as an instance of the class {@link Sorting}. The
     * sorting may not be null
     */
    @NotNull
    public static AssociationRuleSorting forAssociationRules() {
        return new AssociationRuleSorting();
    }

}