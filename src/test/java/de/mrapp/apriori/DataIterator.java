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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.StringTokenizer;

import static de.mrapp.util.Condition.ensureNotEmpty;
import static de.mrapp.util.Condition.ensureNotNull;

/**
 * An iterator, which allows to iterate the transactions, which are contained by a text file.
 *
 * @author Michael Rapp
 */
public class DataIterator implements Iterator<Transaction<NamedItem>> {

    /**
     * An implementation of the interface {@link Transaction}. Each transaction corresponds to a
     * single line of a text file.
     */
    public static class TransactionImplementation implements Transaction<NamedItem> {

        /**
         * The line, the transaction corresponds to.
         */
        private final String line;

        /**
         * Creates a new implementation of the interface {@link Transaction}.
         *
         * @param line The line, the transaction corresponds to, as a {@link String}. The line may
         *             neither be null, nor empty
         */
        public TransactionImplementation(@NotNull final String line) {
            ensureNotNull(line, "The line may not be null");
            ensureNotEmpty(line, "The line may not be empty");
            this.line = line;
        }

        @NotNull
        @Override
        public Iterator<NamedItem> iterator() {
            return new LineIterator(line);
        }

    }

    /**
     * An iterator, which allows to iterate the items, which are contained by a single line of a
     * text file.
     */
    private static class LineIterator implements Iterator<NamedItem> {

        /**
         * The tokenizer, which is used by the iterator.
         */
        private final StringTokenizer tokenizer;

        /**
         * Creates a new iterator, which allows to iterate the items, which are contained by a
         * single line of a text file.
         *
         * @param line The line, which should be tokenized by the iterator, as a {@link String}. The
         *             line may neither be null, nor empty
         */
        LineIterator(@NotNull final String line) {
            ensureNotNull(line, "The line may not be null");
            ensureNotEmpty(line, "The line may not be empty");
            this.tokenizer = new StringTokenizer(line);
        }

        @Override
        public boolean hasNext() {
            return tokenizer.hasMoreTokens();
        }

        @Override
        public NamedItem next() {
            String token = tokenizer.nextToken();
            return token != null && !token.isEmpty() ? new NamedItem(token) : null;
        }

    }

    /**
     * The text file, which is read by the iterator.
     */
    private final File file;

    /**
     * The read, which is used the text file.
     */
    private BufferedReader reader;

    /**
     * The next line, which is read by the iterator.
     */
    private String nextLine;

    /**
     * Opens the reader, which is used to read the text file, if it is not opened yet.
     */
    private void openReader() {
        if (reader == null) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } catch (IOException e) {
                String message = "Failed to open file " + file;
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

    /**
     * Closes the reader, which is used to read the text file, if it is opened.
     */
    private void closeReader() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // No need to handle
            } finally {
                reader = null;
            }
        }
    }

    /**
     * Reads the next line from the text file.
     *
     * @return The next line as a {@link String} or null, if the end of the file is reached
     */
    private String readNextLine() {
        try {
            String nextLine = reader.readLine();

            if (nextLine != null) {
                if (!nextLine.matches("\\s*") && !nextLine.startsWith("#")) {
                    return nextLine;
                } else {
                    return readNextLine();
                }
            }

            closeReader();
            return null;
        } catch (IOException e) {
            String message = "Failed to read file " + file;
            LOGGER.error(message, e);
            throw new RuntimeException(message);
        }
    }

    /**
     * The SL4J logger, which is used by the iterator.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIterator.class);

    /**
     * Creates a new iterator, which allows to iterate the transactions, which are contained by a
     * text file.
     *
     * @param file The text file, which should be read by the iterator, as an instance of the class
     *             {@link File}. The file may not be null
     */
    public DataIterator(@NotNull final File file) {
        ensureNotNull(file, "The file may not be null");
        this.file = file;
        this.reader = null;
        this.nextLine = null;
    }

    @Override
    public final boolean hasNext() {
        openReader();
        nextLine = readNextLine();
        return nextLine != null;
    }

    @Override
    public final Transaction<NamedItem> next() {
        return hasNext() ? new TransactionImplementation(nextLine) : null;
    }

}