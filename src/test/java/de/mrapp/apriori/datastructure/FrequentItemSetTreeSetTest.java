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
package de.mrapp.apriori.datastructure;

import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.NamedItem;
import org.junit.Test;

import java.util.Comparator;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality of the class {@link FrequentItemSetTreeSet}.
 *
 * @author Michael Rapp
 */
public class FrequentItemSetTreeSetTest {

    /**
     * Tests, if all class members are set correctly by the constructor.
     */
    @Test
    public final void testConstructor() {
        SortedSet<ItemSet<NamedItem>> frequentItemSets = new FrequentItemSetTreeSet<>(
                Comparator.reverseOrder());
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.add(new NamedItem("a"));
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.add(new NamedItem("b"));
        itemSet2.setSupport(0.6);
        frequentItemSets.add(itemSet1);
        frequentItemSets.add(itemSet2);
        assertEquals(2, frequentItemSets.size());
        assertEquals(itemSet2, frequentItemSets.first());
        assertEquals(itemSet1, frequentItemSets.last());
    }

    /**
     * Test the functionality of the toString-method.
     */
    @Test
    public final void testToString() {
        SortedSet<ItemSet<NamedItem>> frequentItemSets = new FrequentItemSetTreeSet<>(
                Comparator.reverseOrder());
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.add(new NamedItem("a"));
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.add(new NamedItem("b"));
        itemSet2.setSupport(0.6);
        frequentItemSets.add(itemSet1);
        frequentItemSets.add(itemSet2);
        assertEquals("[[b] (support = 0.6),\n[a] (support = 0.5)]", frequentItemSets.toString());
    }

}