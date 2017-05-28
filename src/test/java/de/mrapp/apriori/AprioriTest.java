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
 * Tests the functionality of the class {@link Apriori}.
 *
 * @author Michael Rapp
 */
public class AprioriTest extends AbstractDataTest {

    /**
     * Tests, if all class members are set correctly by the constructor.
     */
    @Test
    public final void testConstructor() {
        double minSupport = 0.5;
        double minConfidence = 0.6;
        Apriori<NamedItem> apriori = new Apriori<>(minSupport, minConfidence);
        assertEquals(minSupport, apriori.getMinSupport(), 0);
        assertEquals(minConfidence, apriori.getMinConfidence(), 0);
    }

    /**
     * Tests the functionality of the execute-method.
     */
    @Test
    public final void testExecute() {
        File inputFile = getInputFile();
        DataIterator dataIterator = new DataIterator(inputFile);
        Apriori<NamedItem> apriori = new Apriori<>(0.5, 1.0);
        RuleSet<NamedItem> ruleSet = apriori.execute(dataIterator);
    }

}