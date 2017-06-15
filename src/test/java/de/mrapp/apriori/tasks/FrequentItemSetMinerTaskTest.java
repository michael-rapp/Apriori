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
import de.mrapp.apriori.modules.FrequentItemSetMinerModule;
import org.junit.Test;

/**
 * Tests the functionality of the class {@link FrequentItemSetMinerTask}.
 *
 * @author Michael Rapp
 */
public class FrequentItemSetMinerTaskTest {

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration as a parameter, if the configuration is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationParameterThrowsException() {
        new FrequentItemSetMinerTask<>(null);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and a frequent item set miner as parameters, if the configuration is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndFrequentItemSetMinerParameterThrowsExceptionWhenConfigurationIsNull() {
        new FrequentItemSetMinerTask<>(null, new FrequentItemSetMinerModule<>());
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the constructor, which expects
     * a configuration and a frequent item set miner as parameters, if the frequent item set miner
     * is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorWithConfigurationAndFrequentItemSetMinerParameterThrowsExceptionWhenFrequentItemSetMinerIsNull() {
        new FrequentItemSetMinerTask<>(new Configuration(), null);
    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent
     * item sets.
     */
    @Test
    public final void testFindFrequentItemSets() {
        // TODO: Implement
    }

    /**
     * Tests the functionality of the method, which allows to find a specific number of frequent
     * item sets, if no specific number of frequent item sets should be found.
     */
    @Test
    public final void testFindFrequentItemSetsWhenFrequentItemSetCountIsZero() {
        // TODO: Implement
    }

}