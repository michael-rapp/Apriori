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
import java.util.Arrays;
import java.util.Iterator;

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
public class AssociationRule<ItemType extends Item> implements Comparable<AssociationRule>,
        Serializable, Cloneable {

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

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param <T>   The type of the items, which should be checked
     * @param items An array, which contains the items, which should be checked, as an array of the
     *              generic type T. The array may not be null
     * @return True, if the association rule covers the given items, false otherwise
     */
    @SafeVarargs
    public final <T extends ItemType> boolean covers(@NotNull final T... items) {
        ensureNotNull(items, "The array may not be nul");
        return covers(Arrays.asList(items));
    }

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param items An iterable, which allows to iterate the items, which should be checked, as an
     *              instance of the type {@link Iterable}. The iterable may not be null
     * @return True, if the association rule covers the given items, false otherwise
     */
    public final boolean covers(@NotNull final Iterable<? extends ItemType> items) {
        ensureNotNull(items, "The iterable may not be null");
        return covers(items.iterator());
    }

    /**
     * Returns, whether the association rule covers several items, i.e. that all of the items, which
     * are contained in its body are contained by given items as well.
     *
     * @param iterator An iterator, which allows to iterate the items, which should be checked, as
     *                 an instance of the type {@link Iterator}. The iterator may not be null
     * @return True, if the association rule covers the given items, false otherwise
     */
    public final boolean covers(@NotNull final Iterator<? extends ItemType> iterator) {
        ensureNotNull(iterator, "The iterator may not be null");

        for (ItemType bodyItem : body) {
            boolean contains = false;

            while (iterator.hasNext()) {
                if (bodyItem.equals(iterator.next())) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                return false;
            }
        }

        return true;
    }

    @Override
    public final int compareTo(@NotNull final AssociationRule o) {
        return Double.compare(getSupport(), o.getSupport());
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