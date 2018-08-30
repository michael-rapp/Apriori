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
package de.mrapp.apriori.modules

import de.mrapp.apriori.AbstractDataTest
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.NamedItem
import de.mrapp.apriori.metrics.Confidence
import de.mrapp.apriori.metrics.Leverage
import de.mrapp.apriori.metrics.Lift
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [AssociationRuleGeneratorModule].
 *
 * @author Michael Rapp
 */
class AssociationRuleGeneratorModuleTest : AbstractDataTest() {

    private fun createFrequentItemSets(
            frequentItemSets: Array<Array<String>>, supports: DoubleArray): Map<Int, ItemSet<NamedItem>> {
        val map = HashMap<Int, ItemSet<NamedItem>>()

        for ((index, frequentItemSet) in frequentItemSets.withIndex()) {
            val itemSet = ItemSet<NamedItem>()
            val support = supports[index]
            itemSet.support = support

            for (item in frequentItemSet) {
                val namedItem = NamedItem(item)
                itemSet.add(namedItem)
            }

            map[itemSet.hashCode()] = itemSet
        }

        return map
    }

    /**
     * Tests the functionality of the method, which allows to generate association rules from
     * frequent item sets.
     *
     * @param frequentItemSets  The frequent item sets, the association rules should be generated
     * from, as a two-dimensional [String] array. The array may not
     * be null
     * @param supports          The supports of the frequent item sets, the association rules should
     * be generated from, as a [Double] array. The array may not be
     * null
     * @param minConfidence     The confidence, association rules must at least reach to be
     * considered "interesting", as a [Double] value
     * @param actualRules       The actual rules, which can be generated from the frequent item
     * sets, as a [String] array. The array may not be null
     * @param actualSupports    The actual supports of the rules, which can be generated from the
     * frequent item sets, as a [Double] array. The array may not be
     * null
     * @param actualConfidences The actual confidences of the rules, which can be generated from the
     * frequent item sets, as a [Double] array. The array may not be
     * null
     * @param actualLifts       The actual lifts of the rules, which can be generated from the
     * frequent item sets, as a [Double] array. The array may not be
     * null
     * @param actualLeverages   The actual leverages of the rules, which can be generated from the
     * frequent item sets, as a [Double] array. The array may not be
     * null
     */
    private fun testGenerateAssociationRules(frequentItemSets: Array<Array<String>>,
                                             supports: DoubleArray,
                                             minConfidence: Double,
                                             actualRules: Array<String>,
                                             actualSupports: DoubleArray,
                                             actualConfidences: DoubleArray,
                                             actualLifts: DoubleArray,
                                             actualLeverages: DoubleArray) {
        val associationRuleGenerator = AssociationRuleGeneratorModule<NamedItem>()
        val map = createFrequentItemSets(
                frequentItemSets, supports)
        val ruleSet = associationRuleGenerator
                .generateAssociationRules(map, minConfidence)
        var ruleCount = 0

        for ((index, rule) in ruleSet.withIndex()) {
            assertEquals(actualRules[index], rule.toString())
            assertTrue(Math.abs(actualSupports[index] - rule.support) < 0.01)
            assertTrue(Math.abs(actualConfidences[index] - Confidence().evaluate(rule)) < 0.01)
            assertTrue(Math.abs(actualLifts[index] - Lift().evaluate(rule)) < 0.01)
            assertTrue(Math.abs(actualLeverages[index] - Leverage().evaluate(rule)) < 0.01)
            ruleCount++
        }

        assertEquals(actualRules.size.toLong(), ruleCount.toLong())
    }

    /**
     * Tests the functionality of the method, which allows to generate association rules, when using
     * the frequent item sets, which are contained by the first input file.
     */
    @Test
    fun testGenerateAssociationRules1() {
        testGenerateAssociationRules(AbstractDataTest.FREQUENT_ITEM_SETS_1, AbstractDataTest.SUPPORTS_1, 1.0, RULES_1,
                RULE_SUPPORTS_1, RULE_CONFIDENCES_1, RULE_LIFTS_1, RULE_LEVERAGES_1)
    }

    /**
     * Tests the functionality of the method, which allows to generate association rules, when using
     * the frequent item sets, which are contained by the second input file.
     */
    @Test
    fun testGenerateAssociationRules2() {
        testGenerateAssociationRules(AbstractDataTest.FREQUENT_ITEM_SETS_2, AbstractDataTest.SUPPORTS_2, 0.75, RULES_2,
                RULE_SUPPORTS_2, RULE_CONFIDENCES_2, RULE_LIFTS_2, RULE_LEVERAGES_2)
    }

    /**
     * Ensures, that an [IllegalArgumentException] is thrown by the method, which allows to
     * generate association rules, if the minimum confidence, which is passed as a parameter, is
     * less than 0.
     */
    @Test(expected = IllegalArgumentException::class)
    fun testGenerateAssociationRulesThrowsExceptionWhenMinConfidenceIsLessThanZero() {
        AssociationRuleGeneratorModule<NamedItem>().generateAssociationRules(HashMap(), -0.1)
    }

    /**
     * Ensures, that an [IllegalArgumentException] is thrown by the method, which allows to
     * generate association rules, if the minimum confidence, which is passed as a parameter, is
     * greater than 1.
     */
    @Test(expected = IllegalArgumentException::class)
    fun testGenerateAssociationRulesThrowsExceptionWhenMinConfidenceIsGreaterThanOne() {
        AssociationRuleGeneratorModule<NamedItem>().generateAssociationRules(HashMap(), 1.1)
    }

    companion object {

        /**
         * The association rules, which result from the frequent item sets, which are contained by the
         * first input file.
         */
        private val RULES_1 = arrayOf("[milk] -> [coffee]", "[bread] -> [sugar]", "[coffee] -> [milk]", "[coffee, sugar] -> [milk]", "[milk, sugar] -> [coffee]")

        /**
         * The supports of the rules, which result from the frequent item sets, which are contained by
         * the first input file.
         */
        private val RULE_SUPPORTS_1 = doubleArrayOf(0.75, 0.5, 0.75, 0.5, 0.5)

        /**
         * The confidences of the rules, which result from the frequent item sets, which are contained
         * by the first input file.
         */
        private val RULE_CONFIDENCES_1 = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0)

        /**
         * The lifts of the rules, which result from the frequent item sets, which are contained by
         * the first input file.
         */
        private val RULE_LIFTS_1 = doubleArrayOf(1.33, 1.33, 1.33, 1.33, 1.33)

        /**
         * The leverages of the rules, which result from the frequent item sets, which are contained by
         * the first input file.
         */
        private val RULE_LEVERAGES_1 = doubleArrayOf(0.19, 0.12, 0.19, 0.12, 0.12)


        /**
         * The association rules, which result from the frequent item sets, which are contained by the
         * second input file.
         */
        private val RULES_2 = arrayOf("[beer] -> [chips]", "[beer, wine] -> [chips]", "[chips, wine] -> [beer]")

        /**
         * The supports of the rules, which result from the frequent item sets, which are contained by
         * the second input file.
         */
        private val RULE_SUPPORTS_2 = doubleArrayOf(0.5, 0.25, 0.25)

        /**
         * The confidences of the rules, which result from the frequent item sets, which are contained
         * by the second input file.
         */
        private val RULE_CONFIDENCES_2 = doubleArrayOf(1.0, 1.0, 1.0)

        /**
         * The lifts of the rules, which result from the frequent item sets, which are contained by
         * the first second file.
         */
        private val RULE_LIFTS_2 = doubleArrayOf(1.33, 1.33, 2.0)

        /**
         * The leverages of the rules, which result from the frequent item sets, which are contained by
         * the first second file.
         */
        private val RULE_LEVERAGES_2 = doubleArrayOf(0.12, 0.06, 0.12)
    }

}