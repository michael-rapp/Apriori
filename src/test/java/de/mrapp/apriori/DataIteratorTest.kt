/*
 * Copyright 2017 - 2018 Michael Rapp
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
package de.mrapp.apriori

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the functionality of the class [DataIterator].
 *
 * @author Michael Rapp
 */
class DataIteratorTest : AbstractDataTest() {

    companion object {

        private val DATA_1 = arrayOf(
                arrayOf("bread", "butter", "sugar"),
                arrayOf("coffee", "milk", "sugar"),
                arrayOf("bread", "coffee", "milk", "sugar"),
                arrayOf("coffee", "milk")
        )

        private val DATA_2 = arrayOf(
                arrayOf("beer", "chips", "wine"),
                arrayOf("beer", "chips"),
                arrayOf("pizza", "wine"),
                arrayOf("chips", "pizza")
        )

        private val DATA_3 = arrayOf(
                arrayOf("0", "1", "2", "3"),
                arrayOf("0", "1", "3", "4", "5"),
                arrayOf("0", "1", "4"),
                arrayOf("0", "1", "4")
        )

        private val DATA_4 = arrayOf(
                arrayOf("0", "1", "2", "3"),
                arrayOf("0", "1", "2", "3"),
                arrayOf("0", "1", "3", "4", "5"),
                arrayOf("0", "1", "4"),
                arrayOf("0", "1", "4")
        )

    }

    private fun testIterator(fileName: String, actualData: Array<Array<String>>) {
        val inputFile = getInputFile(fileName)
        val iterator = DataIterator(inputFile)
        var transactionCount = 0

        while (iterator.hasNext()) {
            val transaction = iterator.next()
            val line = actualData[transactionCount]
            var index = 0

            for ((name) in transaction) {
                assertEquals(line[index], name)
                index++
            }

            transactionCount++
        }

        assertEquals(actualData.size.toLong(), transactionCount.toLong())
    }

    @Test
    fun testIterator1() {
        testIterator(AbstractDataTest.INPUT_FILE_1, DATA_1)
    }

    @Test
    fun testIterator2() {
        testIterator(AbstractDataTest.INPUT_FILE_2, DATA_2)
    }

    @Test
    fun testIterator3() {
        testIterator(AbstractDataTest.INPUT_FILE_3, DATA_3)
    }

    @Test
    fun testIterator4() {
        testIterator(AbstractDataTest.INPUT_FILE_4, DATA_4)
    }

}
