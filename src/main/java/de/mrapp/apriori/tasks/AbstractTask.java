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
package de.mrapp.apriori.tasks;

import de.mrapp.apriori.Apriori.Configuration;
import de.mrapp.apriori.Item;
import org.jetbrains.annotations.NotNull;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * An abstract base class for all tasks, which execute a module multiple times in order to
 * obtain the results, which are requested according to a specific configuration.
 *
 * @param <ItemType> The type of the items, which are processed by the algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public abstract class AbstractTask<ItemType extends Item> {

    /**
     * The configuration, which is used by the task.
     */
    private final Configuration configuration;

    /**
     * Creates a new task, which executes a module multiple times in order to obtain the results,
     * which are requested according to a specific configuration.
     *
     * @param configuration The configuration, which should be used by the task, as an instance of
     *                      the class {@link Configuration}. The configuration may not be null
     */
    public AbstractTask(@NotNull final Configuration configuration) {
        ensureNotNull(configuration, "The configuration may not be null");
        this.configuration = configuration;
    }

    /**
     * Returns the configuration, which is used by the task.
     *
     * @return The configuration, which is used by the task, as an instance of the class {@link
     * Configuration}. The configuration may not be null
     */
    @NotNull
    protected final Configuration getConfiguration() {
        return configuration;
    }

}