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
package de.mrapp.apriori.tasks

import de.mrapp.apriori.Apriori
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.NamedItem
import de.mrapp.apriori.RuleSet
import de.mrapp.apriori.modules.AssociationRuleGenerator
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [AssociationRuleGeneratorTask].
 *
 * @author Michael Rapp
 */
class AssociationRuleGeneratorTaskTest {

    /**
     * A class that implements the interface [AssociationRuleGenerator] for testing purposes.
     */
    private inner class AssociationRuleGeneratorMock : AssociationRuleGenerator<NamedItem> {

        internal val minConfidences = LinkedList<Double>()

        override fun generateAssociationRules(
                frequentItemSets: Map<Int, ItemSet<NamedItem>>,
                minConfidence: Double): RuleSet<NamedItem> {
            minConfidences.add(minConfidence)
            return RuleSet(null)
        }

    }

    @Test
    fun testGenerateAssociationRules() {
        val minConfidence = 0.5
        val maxConfidence = 0.8
        val confidenceDelta = 0.1
        val configuration = Apriori.Configuration()
        configuration.ruleCount = 1
        configuration.minConfidence = minConfidence
        configuration.maxConfidence = maxConfidence
        configuration.confidenceDelta = confidenceDelta
        val associationRuleGeneratorMock = AssociationRuleGeneratorMock()
        val associationRuleGeneratorTask = AssociationRuleGeneratorTask(configuration,
                associationRuleGeneratorMock)
        associationRuleGeneratorTask.generateAssociationRules(HashMap())
        assertEquals((Math.round((maxConfidence - minConfidence) / confidenceDelta) + 1).toFloat(),
                associationRuleGeneratorMock.minConfidences.size.toFloat())

        for (i in associationRuleGeneratorMock.minConfidences.indices) {
            val support = associationRuleGeneratorMock.minConfidences[i]
            val expected = maxConfidence - i * confidenceDelta
            assertTrue(Math.abs(expected - support) < 0.001)
        }
    }

    @Test
    fun testGenerateAssociationRulesWhenRuleCountIsZero() {
        val minConfidence = 0.5
        val configuration = Apriori.Configuration()
        configuration.ruleCount = 0
        configuration.minConfidence = minConfidence
        val associationRuleGeneratorMock = AssociationRuleGeneratorMock()
        val associationRuleGeneratorTask = AssociationRuleGeneratorTask(configuration,
                associationRuleGeneratorMock)
        associationRuleGeneratorTask.generateAssociationRules(HashMap())
        assertEquals(1, associationRuleGeneratorMock.minConfidences.size.toLong())
        assertEquals(minConfidence, associationRuleGeneratorMock.minConfidences[0])
    }

}
