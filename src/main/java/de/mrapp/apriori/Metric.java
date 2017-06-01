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

/**
 * Defines the interface, a class, which allows to measure the "interestingly" of association
 * rules according to a certain metric, must implement.
 */
public interface Metric extends Operator {

    /**
     * Returns the minimum heuristic value of the metric.
     *
     * @return The minimum heuristic value of the metric as a {@link Double} value
     */
    double minValue();

    /**
     * Returns the maximum heuristic value of the metric.
     *
     * @return The maximum heuristic value of the metric as a {@link Double} value
     */
    double maxValue();

}