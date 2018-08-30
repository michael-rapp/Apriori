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

import de.mrapp.util.Condition.ensureNotEmpty
import org.junit.Assert.assertTrue
import java.io.File
import java.net.URISyntaxException
import java.nio.file.Paths

/**
 * An abstract base class for all tests, which rely on reading data from text files.
 *
 * @author Michael Rapp
 */
abstract class AbstractDataTest {

    companion object {

        const val INPUT_FILE_1 = "data1.txt"

        const val INPUT_FILE_2 = "data2.txt"

        const val INPUT_FILE_3 = "data3.txt"

        const val INPUT_FILE_4 = "data4.txt"

        val FREQUENT_ITEM_SETS_1 = arrayOf(
                arrayOf("milk", "sugar"),
                arrayOf("coffee"),
                arrayOf("coffee", "milk", "sugar"),
                arrayOf("coffee", "sugar"),
                arrayOf("milk"),
                arrayOf("coffee", "milk"),
                arrayOf("bread"),
                arrayOf("bread", "sugar"),
                arrayOf("sugar")
        )

        val SUPPORTS_1 = doubleArrayOf(0.5, 0.75, 0.5, 0.5, 0.75, 0.75, 0.5, 0.5, 0.75)

        val FREQUENT_ITEM_SETS_2 = arrayOf(
                arrayOf("beer"),
                arrayOf("wine"),
                arrayOf("beer", "wine"),
                arrayOf("chips", "pizza"),
                arrayOf("beer", "chips", "wine"),
                arrayOf("chips"),
                arrayOf("beer", "chips"),
                arrayOf("chips", "wine"),
                arrayOf("pizza"),
                arrayOf("pizza", "wine")
        )

        val SUPPORTS_2 = doubleArrayOf(0.5, 0.5, 0.25, 0.25, 0.25, 0.75, 0.5, 0.25, 0.5, 0.25)

        val FREQUENT_ITEM_SETS_3 = arrayOf(
                arrayOf("0", "3"),
                arrayOf("0", "1", "3"),
                arrayOf("3"),
                arrayOf("0", "4"),
                arrayOf("0", "1", "4"),
                arrayOf("4"),
                arrayOf("1", "4"),
                arrayOf("0"),
                arrayOf("0", "1"),
                arrayOf("1")
        )

        val SUPPORTS_3 = doubleArrayOf(0.5, 0.5, 0.5, 0.75, 0.75, 0.75, 0.75, 1.0, 1.0, 1.0)

        val FREQUENT_ITEM_SETS_4 = arrayOf(
                arrayOf("0", "3"),
                arrayOf("0", "1", "3"),
                arrayOf("3"),
                arrayOf("0", "4"),
                arrayOf("0", "1", "4"),
                arrayOf("4"),
                arrayOf("1", "4"),
                arrayOf("0"),
                arrayOf("0", "1"),
                arrayOf("1")
        )

        val SUPPORTS_4 = doubleArrayOf(0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 1.0, 1.0, 1.0)

    }

    protected fun getInputFile(fileName: String): File {
        ensureNotEmpty(fileName, "The file name may not be null")

        try {
            val url = javaClass.classLoader.getResource(fileName)

            if (url != null) {
                val file = Paths.get(url.toURI()).toFile()
                assertTrue(file.isFile)
                return file
            }

            throw RuntimeException("Failed to retrieve path of input file")
        } catch (e: URISyntaxException) {
            throw RuntimeException("Failed to read input file", e)
        }

    }

}
