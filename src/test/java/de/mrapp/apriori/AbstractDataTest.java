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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * An abstract base class for all tests, which rely on reading data from text files.
 *
 * @author Michael Rapp
 */
public class AbstractDataTest {

    /**
     * The name of the input file, which is used by the tests.
     */
    private static final String INPUT_FILE = "data.txt";

    /**
     * Returns the input file, which can be used by the tests.
     *
     * @return The input file, which can be used by the tests, as an instance of the class {@link
     * File}. The file may not be null
     */
    @NotNull
    protected final File getInputFile() {
        try {
            URL url = getClass().getClassLoader().getResource(INPUT_FILE);

            if (url != null) {
                File file = Paths.get(url.toURI()).toFile();
                assertTrue(file.isFile());
                return file;
            }

            throw new RuntimeException("Failed to retrieve path of input file");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to read input file", e);
        }
    }

}