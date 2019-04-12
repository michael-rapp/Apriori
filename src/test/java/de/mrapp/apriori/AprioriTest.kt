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

import de.mrapp.apriori.datastructure.TransactionalItemSet
import de.mrapp.apriori.modules.AssociationRuleGenerator
import de.mrapp.apriori.modules.FrequentItemSetMiner
import de.mrapp.apriori.tasks.AssociationRuleGeneratorTask
import de.mrapp.apriori.tasks.FrequentItemSetMinerTask
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * Tests the functionality of the class [Apriori].
 *
 * @author Michael Rapp
 */
class AprioriTest : AbstractDataTest() {

    @Test
    fun testConfigurationDefaultConstructor() {
        val configuration = Apriori.Configuration()
        assertEquals(0.0, configuration.minSupport)
        assertEquals(1.0, configuration.maxSupport)
        assertEquals(0.1, configuration.supportDelta)
        assertEquals(0, configuration.frequentItemSetCount)
        assertFalse(configuration.generateRules)
        assertEquals(0.0, configuration.minConfidence)
        assertEquals(1.0, configuration.maxConfidence)
        assertEquals(0.1, configuration.confidenceDelta)
        assertEquals(0, configuration.ruleCount)
    }

    @Test
    fun testBuilderWhenNotTryingToFindASpecificNumberOfFrequentItemSets() {
        val minSupport = 0.3
        val maxSupport = 0.8
        val supportDelta = 0.2
        val frequentItemSetCount = 0
        val apriori = Apriori.Builder<NamedItem>(minSupport).maxSupport(maxSupport)
                .supportDelta(supportDelta).frequentItemSetCount(frequentItemSetCount).create()
        val configuration = apriori.configuration
        assertEquals(minSupport, configuration.minSupport)
        assertEquals(maxSupport, configuration.maxSupport)
        assertEquals(supportDelta, configuration.supportDelta)
        assertEquals(frequentItemSetCount, configuration.frequentItemSetCount)
        assertFalse(configuration.generateRules)
    }

    @Test
    fun testBuilderWhenTryingToFindASpecificNumberOfFrequentItemSets() {
        val minSupport = 0.3
        val maxSupport = 0.8
        val supportDelta = 0.2
        val frequentItemSetCount = 2
        val apriori = Apriori.Builder<NamedItem>(frequentItemSetCount).minSupport(minSupport)
                .maxSupport(maxSupport).supportDelta(supportDelta).create()
        val configuration = apriori.configuration
        assertEquals(minSupport, configuration.minSupport)
        assertEquals(maxSupport, configuration.maxSupport)
        assertEquals(supportDelta, configuration.supportDelta)
        assertEquals(frequentItemSetCount, configuration.frequentItemSetCount)
        assertFalse(configuration.generateRules)
    }

    @Test
    fun testBuilderWhenGeneratingAssociationRules() {
        val minSupport = 0.3
        val maxSupport = 0.8
        val supportDelta = 0.2
        val frequentItemSetCount = 2
        val minConfidence = 0.4
        val maxConfidence = 0.8
        val confidenceDelta = 0.2
        val ruleCount = 0
        val apriori = Apriori.Builder<NamedItem>(frequentItemSetCount).generateRules(minConfidence)
                .minSupport(minSupport).maxSupport(maxSupport).supportDelta(supportDelta)
                .frequentItemSetCount(frequentItemSetCount).maxConfidence(maxConfidence)
                .confidenceDelta(confidenceDelta).ruleCount(ruleCount).create()
        val configuration = apriori.configuration
        assertEquals(minSupport, configuration.minSupport)
        assertEquals(maxSupport, configuration.maxSupport)
        assertEquals(supportDelta, configuration.supportDelta)
        assertEquals(frequentItemSetCount, configuration.frequentItemSetCount)
        assertTrue(configuration.generateRules)
        assertEquals(minConfidence, configuration.minConfidence)
        assertEquals(maxConfidence, configuration.maxConfidence)
        assertEquals(confidenceDelta, configuration.confidenceDelta)
        assertEquals(ruleCount, configuration.ruleCount)
    }

    @Test
    fun testBuilderWhenTryingToGenerateASpecificNumberOfAssociationRules() {
        val minSupport = 0.3
        val maxSupport = 0.8
        val supportDelta = 0.2
        val frequentItemSetCount = 2
        val minConfidence = 0.4
        val maxConfidence = 0.8
        val confidenceDelta = 0.2
        val ruleCount = 2
        val apriori = Apriori.Builder<NamedItem>(frequentItemSetCount).generateRules(ruleCount)
                .minSupport(minSupport).maxSupport(maxSupport).supportDelta(supportDelta)
                .frequentItemSetCount(frequentItemSetCount).minConfidence(minConfidence)
                .maxConfidence(maxConfidence).confidenceDelta(confidenceDelta).create()
        val configuration = apriori.configuration
        assertEquals(minSupport, configuration.minSupport)
        assertEquals(maxSupport, configuration.maxSupport)
        assertEquals(supportDelta, configuration.supportDelta)
        assertEquals(frequentItemSetCount, configuration.frequentItemSetCount)
        assertTrue(configuration.generateRules)
        assertEquals(minConfidence, configuration.minConfidence)
        assertEquals(maxConfidence, configuration.maxConfidence)
        assertEquals(confidenceDelta, configuration.confidenceDelta)
        assertEquals(ruleCount, configuration.ruleCount)
    }

    @Test
    fun testConstructorWithConfigurationParameter() {
        val configuration = Apriori.Configuration()
        val apriori = Apriori<NamedItem>(configuration)
        assertEquals(configuration, apriori.configuration)
    }

    @Test
    fun testConstructorWithConfigurationAndTaskParameters() {
        val configuration = Apriori.Configuration()
        val apriori = Apriori(configuration, FrequentItemSetMinerTask(configuration),
                AssociationRuleGeneratorTask<NamedItem>(configuration))
        assertEquals(configuration, apriori.configuration)
    }

    @Test
    fun testExecuteWhenNotGeneratingRules() {
        val configuration = Apriori.Configuration()
        configuration.generateRules = false
        val map = HashMap<Int, TransactionalItemSet<NamedItem>>()
        val itemSet1 = TransactionalItemSet<NamedItem>()
        val item1 = NamedItem("a")
        itemSet1.add(item1)
        itemSet1.support = 1.0
        val itemSet2 = TransactionalItemSet<NamedItem>()
        val item2 = NamedItem("b")
        itemSet2.add(item2)
        itemSet2.support = 0.9
        map[itemSet1.hashCode()] = itemSet1
        map[itemSet2.hashCode()] = itemSet2
        val frequentItemSetMiner = object : FrequentItemSetMiner<NamedItem> {
            override fun findFrequentItemSets(iterable: Iterable<Transaction<NamedItem>>,
                                              minSupport: Double) = map
        }
        val associationRuleGenerator = object : AssociationRuleGenerator<NamedItem> {
            override fun generateAssociationRules(frequentItemSets: Map<Int, ItemSet<NamedItem>>,
                                                  minConfidence: Double) = throw RuntimeException()
        }
        val frequentItemSetMinerTask = FrequentItemSetMinerTask(configuration, frequentItemSetMiner)
        val associationRuleGeneratorTask = AssociationRuleGeneratorTask(configuration,
                associationRuleGenerator)
        val file = getInputFile(AbstractDataTest.INPUT_FILE_1)
        val apriori = Apriori(configuration, frequentItemSetMinerTask, associationRuleGeneratorTask)
        val output = apriori.execute(Iterable { DataIterator(file) })
        assertEquals(configuration, output.configuration)
        assertNull(output.ruleSet)
        val set = output.frequentItemSets
        assertEquals(map.size, set.size)
        assertFalse(set.first() is TransactionalItemSet<*>)
        assertEquals(1, set.first().size)
        assertEquals(item1, set.first().first())
        assertFalse(set.last() is TransactionalItemSet<*>)
        assertEquals(1, set.last().size)
        assertEquals(item2, set.last().first())
    }

    @Test
    fun testExecuteWhenGeneratingRules() {
        val configuration = Apriori.Configuration()
        configuration.generateRules = true
        val map = HashMap<Int, TransactionalItemSet<NamedItem>>()
        val itemSet1 = TransactionalItemSet<NamedItem>()
        val item1 = NamedItem("a")
        itemSet1.add(item1)
        itemSet1.support = 1.0
        val itemSet2 = TransactionalItemSet<NamedItem>()
        val item2 = NamedItem("b")
        itemSet2.add(item2)
        itemSet2.support = 0.9
        map[itemSet1.hashCode()] = itemSet1
        map[itemSet2.hashCode()] = itemSet2
        val ruleSet = RuleSet<NamedItem>(null)
        val associationRule = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.5)
        ruleSet.add(associationRule)
        val frequentItemSetMiner = object : FrequentItemSetMiner<NamedItem> {
            override fun findFrequentItemSets(iterable: Iterable<Transaction<NamedItem>>,
                                              minSupport: Double) = map
        }
        val associationRuleGenerator = object : AssociationRuleGenerator<NamedItem> {
            override fun generateAssociationRules(frequentItemSets: Map<Int, ItemSet<NamedItem>>,
                                                  minConfidence: Double) = ruleSet
        }
        val frequentItemSetMinerTask = FrequentItemSetMinerTask(configuration, frequentItemSetMiner)
        val associationRuleGeneratorTask = AssociationRuleGeneratorTask(configuration,
                associationRuleGenerator)
        val file = getInputFile(AbstractDataTest.INPUT_FILE_1)
        val apriori = Apriori(configuration, frequentItemSetMinerTask, associationRuleGeneratorTask)
        val output = apriori.execute(Iterable { DataIterator(file) })
        assertEquals(configuration, output.configuration)
        assertEquals(ruleSet, output.ruleSet)
        val set = output.frequentItemSets
        assertEquals(map.size, set.size)
        assertFalse(set.first() is TransactionalItemSet<*>)
        assertEquals(1, set.first().size)
        assertEquals(item1, set.first().first())
        assertFalse(set.last() is TransactionalItemSet<*>)
        assertEquals(1, set.last().size)
        assertEquals(item2, set.last().first())
    }

}
