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
package de.mrapp.apriori.metrics

import de.mrapp.apriori.AssociationRule
import de.mrapp.apriori.Metric

/**
 * A metric, which measures the leverage of an association rule. By definition, leverage is the
 * difference between the rule's support and the expected support for the body and head, if they
 * were independent. Furthermore, the leverage of a rule is a lower bound for its support. As a
 * consequence, in contrast to optimizing confidence or lift, optimizing the leverage of a rule
 * guarantees a certain minimum support.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
class Leverage : Metric {

    override fun evaluate(rule: AssociationRule<*>): Double {
        val bodySupport = rule.body.support
        val headSupport = rule.head.support
        return rule.support - bodySupport * headSupport
    }

    override fun minValue() = -Double.MAX_VALUE

    override fun maxValue() = 1.0

}