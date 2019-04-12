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
package de.mrapp.apriori.datastructure

import de.mrapp.apriori.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [FrequentItemSets].
 *
 * @author Michael Rapp
 */
class FrequentItemSetsTest {

    @Test
    fun testConstructor() {
        val frequentItemSets = FrequentItemSets<NamedItem>(reverseOrder())
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("a"))
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.add(NamedItem("b"))
        itemSet2.support = 0.6
        frequentItemSets.add(itemSet1)
        frequentItemSets.add(itemSet2)
        assertEquals(2, frequentItemSets.size.toLong())
        assertEquals(itemSet2, frequentItemSets.first())
        assertEquals(itemSet1, frequentItemSets.last())
    }

    @Test
    fun testFormatFrequentItemSets() {
        val item1 = NamedItem("a")
        val item2 = NamedItem("b")
        val support1 = 0.3
        val support2 = 0.7
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(item1)
        itemSet1.support = support1
        val itemSet2 = ItemSet<NamedItem>()
        itemSet2.add(item2)
        itemSet2.support = support2
        val frequentItemSets = FrequentItemSets<NamedItem>(reverseOrder())
        frequentItemSets.add(itemSet1)
        frequentItemSets.add(itemSet2)
        assertEquals("[" + itemSet2 + " (support = " + support2 + "),\n" + itemSet1 +
                " (support = " + support1 + ")]",
                FrequentItemSets.formatFrequentItemSets(frequentItemSets))
    }

    @Test
    fun testFormatFrequentItemSetsIfEmpty() {
        val frequentItemSets = FrequentItemSets<NamedItem>(reverseOrder())
        assertEquals("[]", FrequentItemSets.formatFrequentItemSets(frequentItemSets))
    }

    @Test
    fun testSort() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("a"))
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("b"))
        itemSet2.add(NamedItem("c"))
        itemSet2.support = 0.4
        val frequentItemSets = FrequentItemSets<NamedItem>(Sorting.forItemSets())
        frequentItemSets.add(itemSet1)
        frequentItemSets.add(itemSet2)
        assertEquals(itemSet1, frequentItemSets.first())
        assertEquals(itemSet2, frequentItemSets.last())
        val sorting = Sorting.forItemSets().withOrder(Sorting.Order.ASCENDING)
        val sortedFrequentItemSets = frequentItemSets.sort(sorting)
        assertEquals(itemSet2, sortedFrequentItemSets.first())
        assertEquals(itemSet1, sortedFrequentItemSets.last())
    }

    @Test
    fun testFilter() {
        val itemSet1 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("a"))
        itemSet1.support = 0.5
        val itemSet2 = ItemSet<NamedItem>()
        itemSet1.add(NamedItem("b"))
        itemSet2.add(NamedItem("c"))
        itemSet2.support = 0.4
        val frequentItemSets = FrequentItemSets<NamedItem>(Sorting.forItemSets())
        frequentItemSets.add(itemSet1)
        frequentItemSets.add(itemSet2)
        assertEquals(itemSet1, frequentItemSets.first())
        assertEquals(itemSet2, frequentItemSets.last())
        val filter = Filter.forItemSets().bySize(2)
        val filteredFrequentItemSets = frequentItemSets.filter(filter)
        assertEquals(1, filteredFrequentItemSets.size.toLong())
        assertEquals(itemSet1, filteredFrequentItemSets.first())
    }

}