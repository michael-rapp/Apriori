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
import java.util.Set;

/**
 * An association rule of the form Y <- X, which consists of a body X and a head Y. Such rules are
 * given as the result of the Apriori algorithm. Both, the body and the head of an association rule
 * consist of one or several items. These item sets must be distinct. An association rule specifies,
 * that if the items, which are contained by its body, occur in a transaction, the items, which are
 * given in its head, do also occur with a certain probability.
 *
 * @param <ItemType> The type of the items, the association rule's body and head consist of
 * @author Michael Rapp
 * @since 1.0.0
 */
public class AssociationRule<ItemType extends Item> implements Serializable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The body of the association rule.
     */
    private final Set<ItemType> body;

    /**
     * The head of the association rule.
     */
    private final Set<ItemType> head;

    /**
     * The support of the association rule.
     */
    private final double support;

    /**
     * The confidence of the association rule.
     */
    private final double confidence;

    /**
     * Creates a new association rule.
     *
     * @param body       A set, which contains the items, which are contained by the association
     *                   rule's body, as an instance of the type {@link Set}. The set may neither be
     *                   null, nor empty
     * @param head       A set, which contains the items, which are contained by the association
     *                   rule's head, as an instance of the type {@link Set}. The set may neither be
     *                   null, nor empty
     * @param support    The support of the association rule as a {@link Double} value. The support
     *                   must be at least 0 and at maximum 1
     * @param confidence The confidence of the association rule as a {@link Double} value. The
     *                   confidence must be at least 0 and at maximum 1
     */
    public AssociationRule(@NotNull final Set<ItemType> body, @NotNull final Set<ItemType> head,
                           final double support,
                           final double confidence) {
        // TODO: Throw exceptions
        this.body = body;
        this.head = head;
        this.support = support;
        this.confidence = confidence;
    }

    /**
     * Returns the body of the association rule.
     *
     * @return A set, which contains the items, which are contained by the association rule's body,
     * as an instance of the type {@link Set}. The set may neither be null, nor empty
     */
    @NotNull
    public final Set<ItemType> getBody() {
        return body;
    }

    /**
     * Returns the head of the association rule.
     *
     * @return A set, which contains the items, which are contained by the association rule's head,
     * as an instance of the type {@link Set}. The set may neither be null nor empty
     */
    @NotNull
    public final Set<ItemType> getHead() {
        return head;
    }

    /**
     * Returns the support of the association rule. By definition, "support" measures the percentage
     * of transactions for which the body and head of the rule is true.
     *
     * @return The support of the association rule as a {@link Double} value. The support must be at
     * least 0 and at maximum 1
     */
    public final double getSupport() {
        return support;
    }

    /**
     * Returns the confidence of the association rule. By definition, "confidence" measures the
     * percentage of transactions for which the head of the rule is true, among all transaction for
     * which the body is true.
     *
     * @return The confidence of the association rule as a {@link Double} value. The confidence must
     * be at least 0 and at maximum 1
     */
    public final double getConfidence() {
        return confidence;
    }

    @Override
    public final String toString() {
        return head.toString() + " <- " + body.toString();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + body.hashCode();
        result = prime * result + head.hashCode();
        long tempSupport = Double.doubleToLongBits(support);
        result = prime * result + (int) (tempSupport ^ (tempSupport >>> 32));
        long tempConfidence = Double.doubleToLongBits(confidence);
        result = prime * result + (int) (tempConfidence ^ (tempConfidence >>> 32));
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
        return body.equals(other.body) && head.equals(other.head) && support == other.support &&
                confidence == other.confidence;
    }

}