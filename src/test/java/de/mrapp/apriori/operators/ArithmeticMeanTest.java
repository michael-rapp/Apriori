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
package de.mrapp.apriori.operators;

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Metric;
import de.mrapp.apriori.NamedItem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the class {@link ArithmeticMean}.
 *
 * @author Michael Rapp
 */
public class ArithmeticMeanTest {

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the add methods, when the weight, which is passes
     * as an argument, is not greater than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testAddThrowsExceptionWhenWeightIsNotGreaterThanZero() {
        new ArithmeticMean().add(mock(Metric.class), 0);
    }

    /**
     * Tests the functionality of the evaluate-method, when using equal weights.
     */
    @Test
    public final void testEvaluateWithEqualWeights() {
        Metric metric = mock(Metric.class);
        when(metric.evaluate(any())).thenReturn(3d, 5d);
        ArithmeticMean arithmeticMean = new ArithmeticMean().add(metric).add(metric);
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.0);
        assertEquals(4, arithmeticMean.evaluate(associationRule), 0);
    }

    /**
     * Tests the functionality of the evaluate-method, when using different weights.
     */
    @Test
    public final void testEvaluateWithDifferentWeights() {
        Metric metric = mock(Metric.class);
        when(metric.evaluate(any())).thenReturn(3d, 5d);
        ArithmeticMean arithmeticMean = new ArithmeticMean().add(metric, 2.0).add(metric, 1.0);
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.0);
        assertEquals(11d / 3d, arithmeticMean.evaluate(associationRule), 0);
    }

    /**
     * Ensures, that an {@link IllegalStateException} is thrown by the evaluate-method, when no metrics have been
     * added.
     */
    @Test(expected = IllegalStateException.class)
    public final void testEvaluateThrowsExceptionWhenNoMetricsAdded() {
        AssociationRule<NamedItem> associationRule = new AssociationRule<>(new ItemSet<>(), new ItemSet<>(), 0.0);
        new ArithmeticMean().evaluate(associationRule);
    }

}