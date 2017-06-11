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

import org.junit.Test;

/**
 * Tests the functionality of the class {@link FrequentItemSetMinerTask}.
 *
 * @author Michael Rapp
 */
public class FrequentItemSetMinerTaskTest {

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown, if the configuration, which is
     * passed to the constructor, is null,
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testConstructorThrowsException() {
        new FrequentItemSetMinerTask<>(null);
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