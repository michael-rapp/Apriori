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

import java.io.Serializable;

/**
 * An output of the Apriori algorithm.
 *
 * @param <ItemType> The type of the items, which have been processed by the Apriori algorithm
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Output<ItemType extends Item> implements Serializable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The time, the Apriori algorithm has been started, in milliseconds.
     */
    private final long startTime;

    /**
     * The time, the Apriori algorithm has been ended, in milliseconds.
     */
    private final long endTime;

    /**
     * Creates a new output of the Apriori algorithm.
     *
     * @param startTime The time, the Apriori algorithm has been started, in milliseconds as a
     *                  {@link Long} value. The time must be at least 0
     * @param endTime   The time, the Apriori algorithm has been ended, in milliseconds as a {@link
     *                  Long} value. The time must be at least the start time
     */
    public Output(final long startTime, final long endTime) {
        // TODO: Throw exceptions
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the time, the Apriori algorithm has been started.
     *
     * @return The time, the Apriori algorithm has been started, in milliseconds as a {@link Long}
     * value
     */
    public final long getStartTime() {
        return startTime;
    }

    /**
     * Returns the time, the Apriori algorithm has been ended.
     *
     * @return The time, the Apriori algorithm has been ended, in milliseconds as a {@link Long}
     * value
     */
    public final long getEndTime() {
        return endTime;
    }

    /**
     * Returns the runtime of the Apriori algorithm.
     *
     * @return The runtime of the Apriori algorithm in milliseconds as a {@link Long} value
     */
    public final long getRuntime() {
        return endTime - startTime;
    }

}