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
package de.mrapp.apriori.operators

import de.mrapp.apriori.AssociationRule
import de.mrapp.apriori.Metric
import de.mrapp.apriori.Operator
import de.mrapp.util.Condition.ensureGreater
import de.mrapp.util.Condition.ensureNotEmpty
import de.mrapp.util.datastructure.Pair
import java.util.*

/**
 * An operator, which allows to average the heuristic values, which are calculated by applying
 * multiple metrics to a rule, according to the harmonic mean operation.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
class HarmonicMean : Operator {

    /**
     * A collection that contains the metrics, which have been added to the harmonic mean
     * operator.
     */
    private val metrics: MutableList<Pair<Metric, Double>> = LinkedList()

    /**
     * Adds a new [metric] to the harmonic mean operator.
     *
     * @param weight The weight of the metric, which should be added. The weight must be greater
     *               than 0
     */
    @JvmOverloads
    fun add(metric: Metric, weight: Double = 1.0): HarmonicMean {
        ensureGreater(weight, 0.0, "The weight must be greater than 0")
        metrics.add(Pair.create(metric, weight))
        return this
    }

    override fun evaluate(rule: AssociationRule<*>): Double {
        ensureNotEmpty(metrics, "No metrics added", IllegalStateException::class.java)
        var numerator = 0.0
        var denominator = 0.0

        for ((metric, weight) in metrics) {
            val heuristicValue = metric!!.evaluate(rule)
            numerator += weight!!
            denominator += weight / heuristicValue
        }

        return if (denominator > 0) numerator / denominator else 0.0
    }

}
