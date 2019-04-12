/*
 * Copyright 2017 - 2019 Michael Rapp
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
 * A metric, which measures the conviction of an association rule. By definition, conviction is the
 * ratio of the expected frequency that a rule makes an incorrect prediction, if body and head were
 * independent, over the frequency of incorrect predictions. For example, a conviction of 1.2
 * states, that the items, which are contained in the body and head of a rule, occur together 1.2
 * times as often as if the association between head and body was purely random.
 *
 * @author Michael Rapp
 * @since 1.2.0
 */
class Conviction : Metric {

    override fun evaluate(rule: AssociationRule<*>): Double {
        val numerator = 1 - rule.head.support
        val denominator = 1 - Confidence().evaluate(rule)
        return if (denominator == 0.0) denominator else numerator / denominator
    }

    override fun minValue() = 0.0

    override fun maxValue() = Double.MAX_VALUE

}