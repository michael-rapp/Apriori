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
package de.mrapp.apriori.operators;

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.Metric;
import de.mrapp.apriori.Operator;
import de.mrapp.util.datastructure.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

import static de.mrapp.util.Condition.*;

/**
 * An operator, which allows to average the heuristic values, which are calculated by applying
 * multiple metrics to a rule, according to the harmonic mean operation.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public class HarmonicMean implements Operator {

    /**
     * A collection, which contains the metrics, which have been added to the harmonic mean
     * operator.
     */
    private final Collection<Pair<Metric, Double>> metrics;

    /**
     * Creates a new harmonic mean operator.
     */
    public HarmonicMean() {
        this.metrics = new LinkedList<>();
    }

    /**
     * Adds a new metric to the harmonic mean operator.
     *
     * @param metric The metric, which should be added, as an instance of the type {@link Metric}.
     *               The metric may not be null
     * @return The harmonic mean operator, this method has been called upon, as an instance of the
     * class {@link HarmonicMean}. The operator may not be null
     */
    @NotNull
    public final HarmonicMean add(@NotNull final Metric metric) {
        return add(metric, 1);
    }

    /**
     * Adds a new metric to the harmonic mean operator.
     *
     * @param metric The metric, which should be added, as an instance of the type {@link Metric}.
     *               The metric may not be null
     * @param weight The weight of the metric, which should be added, as a {@link Double} value. The
     *               weight must be greater than 0
     * @return The harmonic mean operator, this method has been called upon, as an instance of the
     * class {@link HarmonicMean}. The operator may not be null
     */
    @NotNull
    public final HarmonicMean add(@NotNull final Metric metric, final double weight) {
        ensureNotNull(metric, "The metric may not be null");
        ensureGreater(weight, 0, "The weight must be greater than 0");
        metrics.add(Pair.create(metric, weight));
        return this;
    }

    @Override
    public final double evaluate(@NotNull final AssociationRule rule) {
        ensureNotNull(rule, "The rule may not be null");
        ensureNotEmpty(metrics, "No metrics added", IllegalStateException.class);
        double numerator = 0;
        double denominator = 0;

        for (Pair<Metric, Double> pair : metrics) {
            Metric metric = pair.first;
            double heuristicValue = metric.evaluate(rule);
            double weight = pair.second;
            numerator += weight;
            denominator += weight / heuristicValue;
        }

        return denominator > 0 ? numerator / denominator : 0;
    }

}