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
package de.mrapp.apriori.metrics;

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.NamedItem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality of the class {@link Confidence}.
 *
 * @author Michael Rapp
 */
public class ConfidenceTest {

    /**
     * Tests the functionality of the evaluate-method.
     */
    @Test
    public final void testEvaluate() {
        double bodySupport = 0.7;
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        body.setSupport(bodySupport);
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        double support = 0.5;
        AssociationRule<NamedItem> rule = new AssociationRule<>(body, head, support);
        assertEquals(support / bodySupport, new Confidence().evaluate(rule), 0);
    }

    /**
     * Tests the functionality of the evaluate-method, when dividing by zero.
     */
    @Test
    public final void testEvaluateDivisionByZero() {
        ItemSet<NamedItem> body = new ItemSet<>();
        body.add(new NamedItem("a"));
        ItemSet<NamedItem> head = new ItemSet<>();
        head.add(new NamedItem("b"));
        AssociationRule<NamedItem> rule = new AssociationRule<>(body, head, 0.5);
        assertEquals(0, new Confidence().evaluate(rule), 0);
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the evaluate-method, when
     * passing null as a parameter.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testEvaluateThrowsException() {
        new Confidence().evaluate(null);
    }

    /**
     * Tests the functionality of the minValue-method.
     */
    @Test
    public final void testMinValue() {
        assertEquals(0, new Confidence().minValue(), 0);
    }

    /**
     * Tests the functionality of the maxValue-method.
     */
    @Test
    public final void testMaxValue() {
        assertEquals(1, new Confidence().maxValue(), 0);
    }

}
