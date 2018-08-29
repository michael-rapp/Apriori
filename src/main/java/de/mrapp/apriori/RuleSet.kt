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
package de.mrapp.apriori

import de.mrapp.apriori.datastructure.Filterable
import de.mrapp.apriori.datastructure.Sortable
import de.mrapp.apriori.metrics.Confidence
import de.mrapp.apriori.metrics.Leverage
import de.mrapp.apriori.metrics.Lift
import de.mrapp.apriori.metrics.Support
import de.mrapp.util.datastructure.SortedArraySet
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import java.util.function.Predicate

/**
 * A rule set, which contains multiple association rules. The rules, which are contained by a rule
 * set, are unordered by default.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
class RuleSet<ItemType : Item> : SortedArraySet<AssociationRule<ItemType>>,
        Sortable<RuleSet<ItemType>, AssociationRule<*>>,
        Filterable<RuleSet<ItemType>, AssociationRule<*>>,
        Serializable {

    /**
     * Creates an empty rule set.
     *
     * @param comparator The comparator, which should be used to sort the set or null, if the
     *                   natural ordering should be used
     */
    constructor(comparator: Comparator<in AssociationRule<ItemType>>?) : super(comparator)

    /**
     * Creates a new rule set.
     *
     * @param rules      A collection, which contains the rules, which should be added to the rule
     *                   set
     * @param comparator The comparator, which should be used to sort the set or null, if the
     *                   natural ordering should be used
     */
    constructor(rules: Collection<AssociationRule<ItemType>>,
                comparator: Comparator<in AssociationRule<ItemType>>?) : super(rules, comparator)

    override fun sort(comparator: Comparator<in AssociationRule<*>>?): RuleSet<ItemType> {
        return RuleSet(this, comparator)
    }

    override fun filter(predicate: Predicate<in AssociationRule<*>>): RuleSet<ItemType> {
        val filteredRuleSet = RuleSet(comparator())

        for (item in this) {
            if (predicate.test(item)) {
                filteredRuleSet.add(item)
            }
        }

        return filteredRuleSet
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        val decimalFormat = DecimalFormat()
        decimalFormat.minimumFractionDigits = 1
        decimalFormat.maximumFractionDigits = 2
        val iterator = iterator()
        stringBuilder.append("[")

        while (iterator.hasNext()) {
            val rule = iterator.next()
            stringBuilder.append(rule.toString())
            stringBuilder.append(" (support = ")
            stringBuilder.append(decimalFormat.format(Support().evaluate(rule)))
            stringBuilder.append(", confidence = ")
            stringBuilder.append(decimalFormat.format(Confidence().evaluate(rule)))
            stringBuilder.append(", lift = ")
            stringBuilder.append(decimalFormat.format(Lift().evaluate(rule)))
            stringBuilder.append(", leverage = ")
            stringBuilder.append(decimalFormat.format(Leverage().evaluate(rule)))
            stringBuilder.append(")")

            if (iterator.hasNext()) {
                stringBuilder.append(",\n")
            }
        }

        stringBuilder.append("]")
        return stringBuilder.toString()
    }

}
