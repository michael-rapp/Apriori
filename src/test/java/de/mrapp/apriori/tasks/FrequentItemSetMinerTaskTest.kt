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

import de.mrapp.apriori.*
import de.mrapp.apriori.datastructure.TransactionalItemSet
import de.mrapp.apriori.modules.FrequentItemSetMiner
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [FrequentItemSetMinerTask].
 *
 * @author Michael Rapp
 */
class FrequentItemSetMinerTaskTest : AbstractDataTest() {

    /**
     * A class, which implements the interface [FrequentItemSetMiner] for testing purposes.
     */
    private inner class FrequentItemSetMinerMock : FrequentItemSetMiner<NamedItem> {

        internal val minSupports = LinkedList<Double>()

        override fun findFrequentItemSets(
                iterable: Iterable<Transaction<NamedItem>>, minSupport: Double): Map<Int, TransactionalItemSet<NamedItem>> {
            minSupports.add(minSupport)
            return HashMap()
        }

    }

    @Test
    fun testFindFrequentItemSets() {
        val minSupport = 0.5
        val maxSupport = 0.8
        val supportDelta = 0.1
        val configuration = Apriori.Configuration()
        configuration.frequentItemSetCount = 1
        configuration.minSupport = minSupport
        configuration.maxSupport = maxSupport
        configuration.supportDelta = supportDelta
        val frequentItemSetMinerMock = FrequentItemSetMinerMock()
        val frequentItemSetMinerTask = FrequentItemSetMinerTask(configuration, frequentItemSetMinerMock)
        val file = getInputFile(AbstractDataTest.INPUT_FILE_1)
        frequentItemSetMinerTask.findFrequentItemSets(Iterable { DataIterator(file) })
        assertEquals((Math.round((maxSupport - minSupport) / supportDelta) + 1).toFloat(),
                frequentItemSetMinerMock.minSupports.size.toFloat())

        for (i in frequentItemSetMinerMock.minSupports.indices) {
            val support = frequentItemSetMinerMock.minSupports[i]
            val expected = maxSupport - i * supportDelta
            assertTrue(Math.abs(expected - support) < 0.001)
        }
    }

    @Test
    fun testFindFrequentItemSetsWhenFrequentItemSetCountIsZero() {
        val minSupport = 0.5
        val configuration = Apriori.Configuration()
        configuration.frequentItemSetCount = 0
        configuration.minSupport = minSupport
        val frequentItemSetMinerMock = FrequentItemSetMinerMock()
        val frequentItemSetMinerTask = FrequentItemSetMinerTask(configuration, frequentItemSetMinerMock)
        val file = getInputFile(AbstractDataTest.INPUT_FILE_1)
        frequentItemSetMinerTask.findFrequentItemSets(Iterable { DataIterator(file) })
        assertEquals(1, frequentItemSetMinerMock.minSupports.size.toLong())
        assertEquals(minSupport, frequentItemSetMinerMock.minSupports[0])
    }

}