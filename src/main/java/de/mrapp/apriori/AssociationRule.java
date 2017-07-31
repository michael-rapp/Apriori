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

import static de.mrapp.util.Condition.*;

/**
 * An association rule of the form X -&gt; Y, which consists of a body X and a head Y. Both, the
 * body and the head of an association rule consist of one or several items. These item sets must be
 * distinct. An association rule specifies, that if the items, which are contained by its body,
 * occur in a transaction, the items, which are given in its head, do also occur with a certain
 * probability.
 *
 * @param <ItemType> The type of the items, the association rule's body and head consist of
 * @author Michael Rapp
 * @since 1.0.0
 */
public class AssociationRule<ItemType extends Item> implements Serializable, Cloneable {

    /**
     * A comparator, which allows to compare the heuristic values of two association rules according
     * to a certain metric or operator.
     */
    public static class Comparator implements java.util.Comparator<AssociationRule<?>> {

        /**
         * The operator, which is used to calculate the heuristic values of rules.
         */
        private final Operator operator;

        /**
         * The tie-breaking strategy, which is used to tie-break between two rules, whose heuristic
         * values are equal.
         */
        private final TieBreaker tieBreaker;

        /**
         * Creates a new comparator, which allows to compare the heuristic values of two association
         * rules according to a certain operator.
         *
         * @param operator   The operator, which should be used to calculate the heuristic values of
         *                   rules, as an instance of the type {@link Operator}. The operator may
         *                   not be null
         * @param tieBreaker The tie-breaking strategy, which should be used, as an instance of the
         *                   class {@link TieBreaker}. The tie-breaking strategy may not be null
         */
        public Comparator(@NotNull final Operator operator, final TieBreaker tieBreaker) {
            ensureNotNull(operator, "The operator may not be null");
            ensureNotNull(tieBreaker, "The tie-breaking strategy may not be null");
            this.operator = operator;
            this.tieBreaker = tieBreaker;
        }

        @Override
        public final int compare(final AssociationRule<?> o1, final AssociationRule<?> o2) {
            double h1 = operator.evaluate(o1);
            double h2 = operator.evaluate(o2);
            return h1 > h2 ? 1 : (h1 == h2 ? tieBreaker.tieBreak(o1, o2) : -1);
        }

    }

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The body of the association rule.
     */
    private final ItemSet<ItemType> body;

    /**
     * The head of the association rule.
     */
    private final ItemSet<ItemType> head;

    /**
     * The support of the association rule.
     */
    private final double support;

    /**
     * Creates a new association rule.
     *
     * @param body    An item set, which contains the items, which are contained by the association
     *                rule's body, as an instance of the class {@link ItemSet}. The item set may not
     *                be null
     * @param head    An item set, which contains the items, which are contained by the association
     *                rule's head, as an instance of the class {@link ItemSet}. The item set may no
     * @param support The support of the association rule as a {@link Double} value. The support
     *                must be at least 0 and at maximum 1
     */
    public AssociationRule(@NotNull final ItemSet<ItemType> body,
                           @NotNull final ItemSet<ItemType> head,
                           final double support) {
        ensureNotNull(body, "The body may not be null");
        ensureNotNull(head, "The head may not be null");
        ensureAtLeast(support, 0, "The support must be at least 0");
        ensureAtMaximum(support, 1, "The support must be at maximum 1");
        this.body = body;
        this.head = head;
        this.support = support;
    }

    /**
     * Returns the body of the association rule.
     *
     * @return An item set, which contains the items, which are contained by the association rule's
     * body, as an instance of the class {@link ItemSet}. The item set may not be null
     */
    @NotNull
    public final ItemSet<ItemType> getBody() {
        return body;
    }

    /**
     * Returns the head of the association rule.
     *
     * @return An item set, which contains the items, which are contained by the association rule's
     * head, as an instance of the class {@link ItemSet}. The item set may neither be null nor
     * empty
     */
    @NotNull
    public final ItemSet<ItemType> getHead() {
        return head;
    }

    /**
     * Returns the support of the association rule.
     *
     * @return The support of the association rule as a {@link Double} value. The support must be at
     * least 0 and at maximum 1
     */
    public final double getSupport() {
        return support;
    }

    @Override
    public final AssociationRule<ItemType> clone() {
        return new AssociationRule<>(body.clone(), head.clone(), support);
    }

    @Override
    public final String toString() {
        return body.toString() + " -> " + head.toString();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + body.hashCode();
        result = prime * result + head.hashCode();
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
        AssociationRule other = (AssociationRule) obj;
        return body.equals(other.body) && head.equals(other.head);
    }

}