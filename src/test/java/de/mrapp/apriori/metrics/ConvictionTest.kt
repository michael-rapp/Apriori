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
package de.mrapp.apriori.metrics

import de.mrapp.apriori.AssociationRule
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.NamedItem
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [Conviction].
 *
 * @author Michael Rapp
 */
class ConvictionTest {

    @Test
    fun testEvaluate() {
        val bodySupport = 0.7
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.support = bodySupport
        val headSupport = 0.8
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        head.support = headSupport
        val support = 0.5
        val rule = AssociationRule(body, head, support)
        val confidence = Confidence().evaluate(rule)
        assertEquals((1 - headSupport) / (1 - confidence), Conviction().evaluate(rule))
    }

    @Test
    fun testEvaluateDivisionByZero() {
        val body = ItemSet<NamedItem>()
        body.add(NamedItem("a"))
        body.support = 0.5
        val head = ItemSet<NamedItem>()
        head.add(NamedItem("b"))
        val rule = AssociationRule(body, head, 0.5)
        assertEquals(0.0, Conviction().evaluate(rule))
    }

    @Test
    fun testMinValue() {
        assertEquals(0.0, Conviction().minValue())
    }

    @Test
    fun testMaxValue() {
        assertEquals(Double.MAX_VALUE, Conviction().maxValue())
    }

}