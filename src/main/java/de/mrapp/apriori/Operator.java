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

/**
 * Defines the interface, a class, which allows to average the heuristic values, which are
 * calculated by applying multiple metrics to a rule, must implement.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface Operator {

    /**
     * Averages the heuristic values, which are calculated by all metrics, which have been added to
     * the operator.
     *
     * @param rule The rule, the metrics should be applied to, as an instance of the class {@link
     *             AssociationRule}. The rule may not be null
     * @return The averaged heuristic value, which has been calculated, as a {@link Double} value
     */
    double average(@NotNull AssociationRule<?> rule);

}