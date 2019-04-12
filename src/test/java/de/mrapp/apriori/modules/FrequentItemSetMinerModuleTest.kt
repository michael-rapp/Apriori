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
package de.mrapp.apriori.modules

import de.mrapp.apriori.AbstractDataTest
import de.mrapp.apriori.DataIterator
import de.mrapp.apriori.NamedItem
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [FrequentItemSetMinerModule].
 *
 * @author Michael Rapp
 */
class FrequentItemSetMinerModuleTest : AbstractDataTest() {

    private fun testFindFrequentItemSets(fileName: String,
                                         minSupport: Double,
                                         actualFrequentItemSets: Array<Array<String>>,
                                         actualSupports: DoubleArray) {
        val inputFile = getInputFile(fileName)
        val frequentItemSetMiner = FrequentItemSetMinerModule<NamedItem>()
        val frequentItemSets = frequentItemSetMiner
                .findFrequentItemSets(Iterable { DataIterator(inputFile) }, minSupport)
        var frequentItemSetCount = 0

        for ((key, itemSet) in frequentItemSets) {
            for ((index, item) in itemSet.withIndex()) {
                assertEquals(actualFrequentItemSets[frequentItemSetCount][index], item.name)
            }

            assertEquals(actualSupports[frequentItemSetCount], itemSet.support)
            assertEquals(itemSet.hashCode().toLong(), key.toLong())
            frequentItemSetCount++
        }

        assertEquals(actualFrequentItemSets.size.toLong(), frequentItemSetCount.toLong())
    }

    @Test
    fun testFindFrequentItemSets1() {
        testFindFrequentItemSets(AbstractDataTest.INPUT_FILE_1, 0.5,
                AbstractDataTest.FREQUENT_ITEM_SETS_1, AbstractDataTest.SUPPORTS_1)
    }

    @Test
    fun testFindFrequentItemSets2() {
        testFindFrequentItemSets(AbstractDataTest.INPUT_FILE_2, 0.25,
                AbstractDataTest.FREQUENT_ITEM_SETS_2, AbstractDataTest.SUPPORTS_2)
    }

    @Test
    fun testFindFrequentItemSets3() {
        testFindFrequentItemSets(AbstractDataTest.INPUT_FILE_3, 0.5,
                AbstractDataTest.FREQUENT_ITEM_SETS_3, AbstractDataTest.SUPPORTS_3)
    }

    @Test
    fun testFindFrequentItemSets4() {
        testFindFrequentItemSets(AbstractDataTest.INPUT_FILE_4, 0.5,
                AbstractDataTest.FREQUENT_ITEM_SETS_4, AbstractDataTest.SUPPORTS_4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFindFrequentItemSetsThrowsExceptionWhenMinSupportIsLessThanZero() {
        val inputFile = getInputFile(AbstractDataTest.INPUT_FILE_1)
        FrequentItemSetMinerModule<NamedItem>().findFrequentItemSets(
                Iterable { DataIterator(inputFile) }, -0.1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFindFrequentItemSetsThrowsExceptionWhenMinSupportIsGreaterThanOne() {
        val inputFile = getInputFile(AbstractDataTest.INPUT_FILE_1)
        FrequentItemSetMinerModule<NamedItem>().findFrequentItemSets(
                Iterable { DataIterator(inputFile) }, 1.1)
    }

}
