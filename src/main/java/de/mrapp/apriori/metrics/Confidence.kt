/*
 * Copyright 2017 - 2018 Michael Rapp
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
package de.mrapp.apriori.metrics

import de.mrapp.apriori.AssociationRule
import de.mrapp.apriori.Metric

/**
 * A metric, which measures the confidence of an association rule. By definition, confidence
 * measures the percentage of transactions for which the head of the rule is true, among all
 * transaction for which the body is true.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
class Confidence : Metric {

    override fun evaluate(rule: AssociationRule<*>): Double {
        val bodySupport = rule.body.support
        return if (bodySupport > 0) rule.support / bodySupport else 0.0
    }

    override fun minValue() = 0.0

    override fun maxValue() = 1.0

}
