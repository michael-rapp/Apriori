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
package de.mrapp.apriori.operators

import de.mrapp.apriori.*
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [HarmonicMean].
 *
 * @author Michael Rapp
 */
class HarmonicMeanTest {

    @Test(expected = IllegalArgumentException::class)
    fun testAddThrowsExceptionWhenWeightIsNotGreaterThanZero() {
        HarmonicMean().add(mock(Metric::class.java), 0.0)
    }

    @Test
    fun testAverageWithEqualWeights() {
        val metric = mock(Metric::class.java)
        doReturn(20.0, 5.0).`when`(metric).evaluate(MockUtil.any())
        val harmonicMean = HarmonicMean().add(metric).add(metric)
        val associationRule = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        assertEquals(8.0, harmonicMean.evaluate(associationRule))
    }

    @Test
    fun testAverageWithDifferentWeights() {
        val metric = mock(Metric::class.java)
        doReturn(3.0, 6.0).`when`(metric).evaluate(MockUtil.any())
        val harmonicMean = HarmonicMean().add(metric, 1.0).add(metric, 2.0)
        val associationRule = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        assertEquals(4.5, harmonicMean.evaluate(associationRule))
    }

    @Test(expected = IllegalStateException::class)
    fun testEvaluateThrowsExceptionWhenNoMetricsAdded() {
        val associationRule = AssociationRule(ItemSet(), ItemSet<NamedItem>(), 0.0)
        HarmonicMean().evaluate(associationRule)
    }

}
