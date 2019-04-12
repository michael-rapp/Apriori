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
package de.mrapp.apriori

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [Output].
 *
 * @author Michael Rapp
 */
class OutputTest {

    @Test
    fun testConstructor() {
        val configuration = Apriori.Configuration()
        val startTime: Long = 0
        val endTime: Long = 2
        val frequentItemSets = FrequentItemSets<NamedItem>(null)
        frequentItemSets.add(ItemSet())
        val ruleSet = RuleSet<NamedItem>(null)
        ruleSet.add(AssociationRule(ItemSet(), ItemSet(), 0.5))
        val output = Output(configuration, startTime, endTime, frequentItemSets, ruleSet)
        assertEquals(configuration, output.configuration)
        assertEquals(startTime, output.startTime)
        assertEquals(endTime, output.endTime)
        assertEquals(endTime - startTime, output.runtime)
        assertEquals(frequentItemSets, output.frequentItemSets)
        assertEquals(ruleSet, output.ruleSet)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorThrowsExceptionWhenStartTimeIsLessThanZero() {
        Output(Apriori.Configuration(), -1, 0, FrequentItemSets<NamedItem>(null), null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorThrowsExceptionWhenEndTimeIsLessThanStartTime() {
        Output(Apriori.Configuration(), 1, 0, FrequentItemSets<NamedItem>(null), null)
    }

}
