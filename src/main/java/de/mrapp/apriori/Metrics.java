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

/**
 * An utility class, which provides methods for calculating heuristic values according to various
 * metrics.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public final class Metrics {

    /**
     * Creates a new utility class, which provides methods for calculating heuristic values
     * according to various metrics.
     */
    private Metrics() {

    }

    /**
     * Calculates and returns the support of an item.
     *
     * @param transactions The total number of available transactions as an {@link Integer} value.
     *                     The number of transactions must be at least 0
     * @param occurrences  The number of transactions, the item is part of, as an {@link Integer}
     *                     value. The number of transactions must be at least 0
     * @return The support, which has been calculated, as a {@link Double} value. The support must
     * be at least 0 and at maximum 1
     */
    public static double calculateSupport(final int transactions, final int occurrences) {
        // TODO: Throw exceptions
        return transactions > 0 ? (double) occurrences / (double) transactions : 0;
    }

    /**
     * Calculates and returns the confidence of an association rule.
     *
     * @param bodySupport    The support of the item set, the rule's body consists of, as a {@link
     *                       Double} value. The support must be at least 0 and at maximum 1
     * @param overallSupport The support of the item set, which contains all items, which are
     *                       contained by the rule's body and head, as a {@link Double} value. The
     *                       support must be at least 0 and at maximum 1
     * @return The confidence, which has been calculated, as a {@link Double} value. The confidence
     * must be at least 0 and at maximum 1
     */
    public static double calculateConfidence(final double bodySupport,
                                             final double overallSupport) {
        // TODO: Throw Exceptions
        return bodySupport > 0 ? overallSupport / bodySupport : 0;
    }

}