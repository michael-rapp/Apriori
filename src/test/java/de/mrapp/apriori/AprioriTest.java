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
package de.mrapp.apriori;

import de.mrapp.apriori.datastructure.TransactionalItemSet;
import de.mrapp.apriori.tasks.AssociationRuleGeneratorTask;
import de.mrapp.apriori.tasks.FrequentItemSetMinerTask;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link Apriori}.
 *
 * @author Michael Rapp
 */
public class AprioriTest extends AbstractDataTest {

    /**
     * Tests, if all class members are set correctly by the default constructor of the inner class {@link
     * Apriori.Configuration}.
     */
    @Test
    public final void testConfigurationDefaultConstructor() {
        Apriori.Configuration configuration = new Apriori.Configuration();
        assertEquals(0.0, configuration.getMinSupport());
        assertEquals(1.0, configuration.getMaxSupport());
        assertEquals(0.1, configuration.getSupportDelta());
        assertEquals(0, configuration.getFrequentItemSetCount());
        assertFalse(configuration.getGenerateRules());
        assertEquals(0.0, configuration.getMinConfidence());
        assertEquals(1.0, configuration.getMaxConfidence());
        assertEquals(0.1, configuration.getConfidenceDelta());
        assertEquals(0, configuration.getRuleCount());
    }

    /**
     * Tests the functionality of the builder, when configuring the Apriori algorithm to not trying to find a specific
     * number of frequent item sets.
     */
    @Test
    public final void testBuilderWhenNotTryingToFindASpecificNumberOfFrequentItemSets() {
        double minSupport = 0.3;
        double maxSupport = 0.8;
        double supportDelta = 0.2;
        int frequentItemSetCount = 0;
        Apriori<NamedItem> apriori =
                new Apriori.Builder<NamedItem>(minSupport).maxSupport(maxSupport).supportDelta(supportDelta)
                        .frequentItemSetCount(frequentItemSetCount).create();
        Apriori.Configuration configuration = apriori.getConfiguration();
        assertEquals(minSupport, configuration.getMinSupport());
        assertEquals(maxSupport, configuration.getMaxSupport());
        assertEquals(supportDelta, configuration.getSupportDelta());
        assertEquals(frequentItemSetCount, configuration.getFrequentItemSetCount());
        assertFalse(configuration.getGenerateRules());
    }

    /**
     * Tests the functionality of the builder, when configuring the Apriori algorithm to trying to find a specific
     * number of frequent item sets.
     */
    @Test
    public final void testBuilderWhenTryingToFindASpecificNumberOfFrequentItemSets() {
        double minSupport = 0.3;
        double maxSupport = 0.8;
        double supportDelta = 0.2;
        int frequentItemSetCount = 2;
        Apriori<NamedItem> apriori =
                new Apriori.Builder<NamedItem>(frequentItemSetCount).minSupport(minSupport).maxSupport(maxSupport)
                        .supportDelta(supportDelta).create();
        Apriori.Configuration configuration = apriori.getConfiguration();
        assertEquals(minSupport, configuration.getMinSupport());
        assertEquals(maxSupport, configuration.getMaxSupport());
        assertEquals(supportDelta, configuration.getSupportDelta());
        assertEquals(frequentItemSetCount, configuration.getFrequentItemSetCount());
        assertFalse(configuration.getGenerateRules());
    }

    /**
     * Tests the functionality of the builder, when configuring the Apriori algorithm to generate association rules.
     */
    @Test
    public final void testBuilderWhenGeneratingAssociationRules() {
        double minSupport = 0.3;
        double maxSupport = 0.8;
        double supportDelta = 0.2;
        int frequentItemSetCount = 2;
        double minConfidence = 0.4;
        double maxConfidence = 0.8;
        double confidenceDelta = 0.2;
        int ruleCount = 0;
        Apriori<NamedItem> apriori =
                new Apriori.Builder<NamedItem>(frequentItemSetCount).generateRules(minConfidence).minSupport(minSupport)
                        .maxSupport(maxSupport).supportDelta(supportDelta).frequentItemSetCount(frequentItemSetCount)
                        .maxConfidence(maxConfidence).confidenceDelta(confidenceDelta).ruleCount(ruleCount).create();
        Apriori.Configuration configuration = apriori.getConfiguration();
        assertEquals(minSupport, configuration.getMinSupport());
        assertEquals(maxSupport, configuration.getMaxSupport());
        assertEquals(supportDelta, configuration.getSupportDelta());
        assertEquals(frequentItemSetCount, configuration.getFrequentItemSetCount());
        assertTrue(configuration.getGenerateRules());
        assertEquals(minConfidence, configuration.getMinConfidence());
        assertEquals(maxConfidence, configuration.getMaxConfidence());
        assertEquals(confidenceDelta, configuration.getConfidenceDelta());
        assertEquals(ruleCount, configuration.getRuleCount());
    }

    /**
     * Tests the functionality of the builder, when configuring the Apriori algorithm to trying to generate a specific
     * number of association rules.
     */
    @Test
    public final void testBuilderWhenTryingToGenerateASpecificNumberOfAssociationRules() {
        double minSupport = 0.3;
        double maxSupport = 0.8;
        double supportDelta = 0.2;
        int frequentItemSetCount = 2;
        double minConfidence = 0.4;
        double maxConfidence = 0.8;
        double confidenceDelta = 0.2;
        int ruleCount = 2;
        Apriori<NamedItem> apriori =
                new Apriori.Builder<NamedItem>(frequentItemSetCount).generateRules(ruleCount).minSupport(minSupport)
                        .maxSupport(maxSupport).supportDelta(supportDelta).frequentItemSetCount(frequentItemSetCount)
                        .minConfidence(minConfidence).maxConfidence(maxConfidence).confidenceDelta(confidenceDelta)
                        .create();
        Apriori.Configuration configuration = apriori.getConfiguration();
        assertEquals(minSupport, configuration.getMinSupport());
        assertEquals(maxSupport, configuration.getMaxSupport());
        assertEquals(supportDelta, configuration.getSupportDelta());
        assertEquals(frequentItemSetCount, configuration.getFrequentItemSetCount());
        assertTrue(configuration.getGenerateRules());
        assertEquals(minConfidence, configuration.getMinConfidence());
        assertEquals(maxConfidence, configuration.getMaxConfidence());
        assertEquals(confidenceDelta, configuration.getConfidenceDelta());
        assertEquals(ruleCount, configuration.getRuleCount());
    }

    /**
     * Tests, if all class members are set correctly by the constructor, which expects a configuration as a parameter.
     */
    @Test
    public final void testConstructorWithConfigurationParameter() {
        Apriori.Configuration configuration = mock(Apriori.Configuration.class);
        Apriori<NamedItem> apriori = new Apriori<>(configuration);
        assertEquals(configuration, apriori.getConfiguration());
    }

    /**
     * Tests, if all class members are set correctly by the constructor, which expects a configuration and tasks as
     * parameters.
     */
    @Test
    public final void testConstructorWithConfigurationAndTaskParameters() {
        Apriori.Configuration configuration = mock(Apriori.Configuration.class);
        Apriori<NamedItem> apriori = new Apriori<>(configuration, new FrequentItemSetMinerTask<>(configuration),
                new AssociationRuleGeneratorTask<>(configuration));
        assertEquals(configuration, apriori.getConfiguration());
    }

    /**
     * Tests the functionality of the method, which allows to execute the Apriori algorithm, if no association rules
     * should be generated.
     */
    @Test
    public final void testExecuteWhenNotGeneratingRules() {
        Apriori.Configuration configuration = mock(Apriori.Configuration.class);
        when(configuration.getGenerateRules()).thenReturn(false);
        Map<Integer, TransactionalItemSet<NamedItem>> map = new HashMap<>();
        TransactionalItemSet<NamedItem> itemSet1 = new TransactionalItemSet<>();
        NamedItem item1 = new NamedItem("a");
        itemSet1.add(item1);
        itemSet1.setSupport(1.0);
        TransactionalItemSet<NamedItem> itemSet2 = new TransactionalItemSet<>();
        NamedItem item2 = new NamedItem("b");
        itemSet2.add(item2);
        itemSet2.setSupport(0.9);
        map.put(itemSet1.hashCode(), itemSet1);
        map.put(itemSet2.hashCode(), itemSet2);
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask =
                new FrequentItemSetMinerTask<>(configuration, (iterator, minSupport) -> map);
        AssociationRuleGeneratorTask<NamedItem> associationRuleGeneratorTask =
                new AssociationRuleGeneratorTask<>(configuration, (frequentItemSets, minConfidence) -> {
                    throw new RuntimeException();
                });
        File file = getInputFile(INPUT_FILE_1);
        Apriori<NamedItem> apriori =
                new Apriori<>(configuration, frequentItemSetMinerTask, associationRuleGeneratorTask);
        Output<NamedItem> output = apriori.execute(() -> new DataIterator(file));
        assertEquals(configuration, output.getConfiguration());
        assertNull(output.getRuleSet());
        SortedSet<ItemSet<NamedItem>> set = output.getFrequentItemSets();
        assertEquals(map.size(), set.size());
        assertFalse(set.first() instanceof TransactionalItemSet);
        assertEquals(1, set.first().size());
        assertEquals(item1, set.first().first());
        assertFalse(set.last() instanceof TransactionalItemSet);
        assertEquals(1, set.last().size());
        assertEquals(item2, set.last().first());
    }

    /**
     * Tests the functionality of the method, which allows to execute the Apriori algorithm, if association rules should
     * be generated.
     */
    @Test
    public final void testExecuteWhenGeneratingRules() {
        Apriori.Configuration configuration = mock(Apriori.Configuration.class);
        when(configuration.getGenerateRules()).thenReturn(true);
        Map<Integer, TransactionalItemSet<NamedItem>> map = new HashMap<>();
        TransactionalItemSet<NamedItem> itemSet1 = new TransactionalItemSet<>();
        NamedItem item1 = new NamedItem("a");
        itemSet1.add(item1);
        itemSet1.setSupport(1.0);
        TransactionalItemSet<NamedItem> itemSet2 = new TransactionalItemSet<>();
        NamedItem item2 = new NamedItem("b");
        itemSet2.add(item2);
        itemSet2.setSupport(0.9);
        map.put(itemSet1.hashCode(), itemSet1);
        map.put(itemSet2.hashCode(), itemSet2);
        RuleSet<NamedItem> ruleSet = new RuleSet<>(null);
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.5);
        ruleSet.add(associationRule);
        FrequentItemSetMinerTask<NamedItem> frequentItemSetMinerTask =
                new FrequentItemSetMinerTask<>(configuration, (iterator, minSupport) -> map);
        AssociationRuleGeneratorTask<NamedItem> associationRuleGeneratorTask =
                new AssociationRuleGeneratorTask<>(configuration, (frequentItemSets, minConfidence) -> ruleSet);
        File file = getInputFile(INPUT_FILE_1);
        Apriori<NamedItem> apriori =
                new Apriori<>(configuration, frequentItemSetMinerTask, associationRuleGeneratorTask);
        Output<NamedItem> output = apriori.execute(() -> new DataIterator(file));
        assertEquals(configuration, output.getConfiguration());
        assertEquals(ruleSet, output.getRuleSet());
        SortedSet<ItemSet<NamedItem>> set = output.getFrequentItemSets();
        assertEquals(map.size(), set.size());
        assertFalse(set.first() instanceof TransactionalItemSet);
        assertEquals(1, set.first().size());
        assertEquals(item1, set.first().first());
        assertFalse(set.last() instanceof TransactionalItemSet);
        assertEquals(1, set.last().size());
        assertEquals(item2, set.last().first());
    }

}