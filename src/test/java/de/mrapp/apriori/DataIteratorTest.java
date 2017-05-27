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

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality of the class {@link DataIterator}.
 *
 * @author Michael Rapp
 */
public class DataIteratorTest extends AbstractDataTest {

    /**
     * The data, which is contained by the input file.
     */
    private static final String[][] DATA = {{"beer", "chips", "wine"}, {"beer", "chips"}, {"pizza", "wine"}, {"chips", "pizza"}};

    /**
     * Tests, if all items are correctly iterated.
     */
    @Test
    public final void testIterator() {
        File inputFile = getInputFile();
        DataIterator iterator = new DataIterator(inputFile);
        int transactionCount = 0;
        Transaction<NamedItem> transaction;

        while ((transaction = iterator.next()) != null) {
            String[] line = DATA[transactionCount];
            int index = 0;

            for (NamedItem item : transaction) {
                assertEquals(line[index], item.getName());
                index++;
            }

            transactionCount++;
            System.out.print("\n");
        }

        assertEquals(DATA.length, transactionCount);
    }

}