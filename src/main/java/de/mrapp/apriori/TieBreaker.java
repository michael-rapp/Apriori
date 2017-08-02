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

import java.util.function.BiFunction;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A tie-breaking strategy, which allows to decide, which one of two association rules is considered
 * to be more "interestingly", if their heuristic values are equal.
 *
 * @author Michael Rapp
 * @since 1.1.0
 */
public class TieBreaker {

    /**
     * The tie-breaking strategy, which is used for tie-breaking before applying this tie-breaking
     * strategy.
     */
    private final TieBreaker parent;

    /**
     * The function, which specifies which one of two association rules is considered to be more
     * "interestingly" when performing tie-breaking.
     */
    private final BiFunction<AssociationRule, AssociationRule, Integer> function;

    /**
     * Creates a new tie-breaking strategy. By default, no tie-breaking is performed at all.
     */
    public TieBreaker() {
        this(null, (a, b) -> 0);
    }

    /**
     * Creates a new tie-breaking strategy.
     *
     * @param parent   The tie-breaking strategy, which should be used for tie breaking-before
     *                 applying this tie-breaking strategy, as an instance of the class {@link
     *                 TieBreaker} or null, if no parent is available
     * @param function The function, which specifies which one of two association rules is
     *                 considered to be more "interestingly" when performing tie-breaking, as an
     *                 instance of the type {@link BiFunction}. The tie-breaking function may not be
     *                 null
     */
    private TieBreaker(@Nullable final TieBreaker parent,
                       @NotNull final BiFunction<AssociationRule, AssociationRule, Integer> function) {
        ensureNotNull(function, "The tie-breaking function may not be null");
        this.parent = parent;
        this.function = function;
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which performs tie-breaking
     * according to a specific operator. I.e., if an association rule reaches a greater performance
     * according to the given operator, it is considered to be more "interestingly".
     *
     * @param operator The operator, which should be used for tie-breaking, as an instance of the
     *                 type {@link Operator}. The operator may not be null
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker byOperator(@NotNull final Operator operator) {
        ensureNotNull(operator, "The metric may not be null");
        return new TieBreaker(this, (a, b) -> {
            double h1 = operator.evaluate(a);
            double h2 = operator.evaluate(b);
            return Double.compare(h1, h2);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers simple
     * association rules over complex ones. I.e., if an association rule contains less items than
     * its counterpart in its body and head, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferSimple() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getBody().size() + a.getHead().size();
            int size2 = b.getBody().size() + b.getHead().size();
            return Integer.compare(size2, size1);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
     * association rules over simple ones. I.e., if an association rule contains more items than its
     * counterpart in its body and head, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferComplex() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getBody().size() + a.getHead().size();
            int size2 = b.getBody().size() + b.getHead().size();
            return Integer.compare(size1, size2);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers simple bodies
     * over simple ones. I.e., if an association rule contains less items than its counterpart in
     * its body, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferSimpleBody() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getBody().size();
            int size2 = b.getBody().size();
            return Integer.compare(size2, size1);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers complex
     * bodies over simple ones. I.e., if an association rule contains more items than its
     * counterpart in its body, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferComplexBody() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getBody().size();
            int size2 = b.getBody().size();
            return Integer.compare(size1, size2);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers simple heads
     * over simple ones. I.e., if an association rule contains less items than its counterpart in
     * its head, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferSimpleHead() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getHead().size();
            int size2 = b.getHead().size();
            return Integer.compare(size2, size1);
        });
    }

    /**
     * Adds an additional criteria to the current tie-breaking strategy, which prefers complex heads
     * over simple ones. I.e., if an association rule contains more items than its counterpart in
     * its head, it is considered to be more "interestingly".
     *
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker preferComplexHead() {
        return new TieBreaker(this, (a, b) -> {
            int size1 = a.getHead().size();
            int size2 = b.getHead().size();
            return Integer.compare(size1, size2);
        });
    }

    /**
     * Adds an additional custom criteria to the current tie-breaking strategy. The given
     * tie-breaking function must return 1, if the first association rule should be preferred, -1,
     * if the second one should be preferred or 0, if no decision could be made.
     *
     * @param function The custom tie-breaking function as an instance of the type {@link
     *                 BiFunction}. The tie-breaking function may not be null
     * @return The tie-breaking strategy, including the added criteria, as an instance of the class
     * {@link TieBreaker}. The tie breaking-strategy may not be null
     */
    @NotNull
    public final TieBreaker custom(
            @NotNull final BiFunction<AssociationRule, AssociationRule, Integer> function) {
        return new TieBreaker(this, function);
    }

    /**
     * Decides, which one of two association rules is considered to be more "interestingly"
     * according to the tie-breaking strategy.
     *
     * @param rule1 The first association rule as an instance of the class {@link AssociationRule}.
     *              The association rule may not be null
     * @param rule2 The second association rule as an instance of the class {@link AssociationRule}.
     *              The association rule may not be null
     * @return The decision of the tie-breaking strategy as an {@link Integer} value. A value of 1
     * means, that the first association rule is preferred, -1 means that the second association
     * rule is preferred and 0 means that no decision could be made
     */
    public final int tieBreak(AssociationRule<?> rule1, AssociationRule<?> rule2) {
        if (parent != null) {
            int result = parent.tieBreak(rule1, rule2);

            if (result != 0) {
                return result;
            }
        }

        return function.apply(rule1, rule2);
    }

}