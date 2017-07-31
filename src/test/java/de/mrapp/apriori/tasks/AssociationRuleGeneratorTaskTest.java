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
package de.mrapp.apriori.tasks;

import de.mrapp.apriori.Apriori.Configuration;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.NamedItem;
import de.mrapp.apriori.RuleSet;
import de.mrapp.apriori.modules.AssociationRuleGenerator;
import de.mrapp.apriori.modules.AssociationRuleGeneratorModule;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link AssociationRuleGeneratorTask}.
 *
 * @author Michael Rapp
 */
public class AssociationRuleGeneratorTaskTest {

    /**
     * A class, which implements the interface {@link AssociationRuleGenerator} for testing
     * purposes.
     */
    private class AssociationRuleGeneratorMock implements AssociationRuleGenerator<NamedItem> {

        /**
         * A list, which contains the minimum confidences, which have been passed when invoking the
         * {@link AssociationRuleGeneratorMock#generateAssociationRules(Map, double)} method.
         */
        private final List<Double> minConfidences = new LinkedList<>();

        @NotNull
        @Override
        public RuleSet<NamedItem> generateAssociationRules(
                @NotNull final Map<Integer, ? extends ItemSet<NamedItem>> frequentItemSets,
                final double minConfidence) {
            minConfidences.add(minConfidence);
            return new RuleSet<>(null);
        }

    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration as a parameter, if the configuration is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationParameterThrowsException() {
        new AssociationRuleGeneratorTask<>(null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and an association rule generator as parameters, if the configuration is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndAssociationRuleGeneratorParameterThrowsExceptionWhenConfigurationIsNull() {
        new AssociationRuleGeneratorTask<>(null, new AssociationRuleGeneratorModule<>());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and an association rule generator as parameters, if the association rule
     * generator is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndAssociationRuleGeneratorParameterThrowsExceptionWhenAssociationRuleGeneratorIsNull() {
        new AssociationRuleGeneratorTask<>(mock(Configuration.class), null);
    }

    /**
     * Tests the functionality of the method, which allows to generate a specific number of
     * association rules.
     */
    @Test
    public final void testGenerateAssociationRules() {
        double minConfidence = 0.5;
        double maxConfidence = 0.8;
        double confidenceDelta = 0.1;
        Configuration configuration = mock(Configuration.class);
        when(configuration.getRuleCount()).thenReturn(1);
        when(configuration.getMinConfidence()).thenReturn(minConfidence);
        when(configuration.getMaxConfidence()).thenReturn(maxConfidence);
        when(configuration.getConfidenceDelta()).thenReturn(confidenceDelta);
        AssociationRuleGeneratorMock associationRuleGeneratorMock = new AssociationRuleGeneratorMock();
        AssociationRuleGeneratorTask<NamedItem> associationRuleGeneratorTask = new AssociationRuleGeneratorTask<>(
                configuration, associationRuleGeneratorMock);
        associationRuleGeneratorTask.generateAssociationRules(new HashMap<>());
        assertEquals(Math.round((maxConfidence - minConfidence) / confidenceDelta) + 1,
                associationRuleGeneratorMock.minConfidences.size(), 0);

        for (int i = 0; i < associationRuleGeneratorMock.minConfidences.size(); i++) {
            double support = associationRuleGeneratorMock.minConfidences.get(i);
            assertEquals(maxConfidence - (i * confidenceDelta), support, 0.01);
        }
    }

    /**
     * Tests the functionality of the method, which allows to generate a specific number of
     * association rules, if no specific number of association rules should be found.
     */
    @Test
    public final void testGenerateAssociationRulesWhenRuleCountIsZero() {
        double minConfidence = 0.5;
        Configuration configuration = mock(Configuration.class);
        when(configuration.getRuleCount()).thenReturn(0);
        when(configuration.getMinConfidence()).thenReturn(minConfidence);
        AssociationRuleGeneratorMock associationRuleGeneratorMock = new AssociationRuleGeneratorMock();
        AssociationRuleGeneratorTask<NamedItem> associationRuleGeneratorTask = new AssociationRuleGeneratorTask<>(
                configuration, associationRuleGeneratorMock);
        associationRuleGeneratorTask.generateAssociationRules(new HashMap<>());
        assertEquals(1, associationRuleGeneratorMock.minConfidences.size());
        assertEquals(minConfidence, associationRuleGeneratorMock.minConfidences.get(0), 0);
    }

}