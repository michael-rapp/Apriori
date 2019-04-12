/*
 * Copyright 2017 - 2019 Michael Rapp
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
import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

/**
 * An iterator, which allows to iterate the transactions, which are contained by a text file.
 *
 * @property file The text file, which is read by the iterator
 * @author Michael Rapp
 */
class DataIterator(private val file: File) : Iterator<Transaction<NamedItem>> {

    companion object {

        private val LOGGER = LoggerFactory.getLogger(DataIterator::class.java)

    }

    /**
     * An implementation of the interface [Transaction]. Each transaction corresponds to a single
     * line of a text file.
     *
     * @property line The line the transaction corresponds to
     */
    class TransactionImplementation(private val line: String) : Transaction<NamedItem> {

        init {
            ensureNotEmpty(line, "The line may not be empty")
        }

        override fun iterator(): Iterator<NamedItem> {
            return LineIterator(line)
        }

    }

    /**
     * An iterator, which allows to iterate the items, which are contained by a single line of a
     * text file.
     *
     * @property line The line, which should be tokenized by the iterator
     */
    private class LineIterator(private val line: String) : Iterator<NamedItem> {

        private val tokenizer: StringTokenizer

        init {
            ensureNotEmpty(line, "The line may not be empty")
            this.tokenizer = StringTokenizer(line)
        }

        override fun hasNext(): Boolean {
            return tokenizer.hasMoreTokens()
        }

        override fun next(): NamedItem {
            val token = tokenizer.nextToken()
            return NamedItem(token)
        }

    }

    private var reader: BufferedReader? = null

    private var nextLine: String? = null

    private var reachedEnd: Boolean = false

    private fun openReader() {
        if (reader == null) {
            try {
                reader = BufferedReader(InputStreamReader(FileInputStream(file)))
            } catch (e: IOException) {
                val message = "Failed to open file $file"
                LOGGER.error(message, e)
                throw RuntimeException(message, e)
            }

        }
    }

    private fun closeReader() {
        reader?.let {
            try {
                it.close()
            } catch (e: IOException) {
                // No need to handle
            } finally {
                reader = null
            }
        }
    }

    private fun readNextLine(): String? {
        try {
            val nextLine = reader!!.readLine()

            if (nextLine != null) {
                return if (!nextLine.matches("\\s*".toRegex()) && !nextLine.startsWith("#")) {
                    nextLine
                } else {
                    readNextLine()
                }
            }

            closeReader()
            reachedEnd = true
            return null
        } catch (e: IOException) {
            val message = "Failed to read file $file"
            LOGGER.error(message, e)
            throw RuntimeException(message)
        }

    }

    override fun hasNext(): Boolean {
        openReader()

        if (nextLine == null && !reachedEnd) {
            nextLine = readNextLine()
        }

        return nextLine != null && !reachedEnd
    }

    override fun next(): Transaction<NamedItem> {
        if (hasNext()) {
            val transaction = TransactionImplementation(nextLine!!)
            nextLine = null
            return transaction
        }

        throw NoSuchElementException()
    }

}