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
 * Defines the interface, a class, which allows to measure the "interestingly" of an association
 * rule according to a certain metric, must implement.
 */
public interface Metric {

    /**
     * Calculates and returns a heuristic value, which measures the "interestingly" of a specific
     * association rule.
     *
     * @param rule The rule, whose "interestingly" should be measured , as an instance of the class
     *             {@link AssociationRule}. The association rule may not be null
     * @return The heuristic value, which has been calculated, as a {@link Double} value
     */
    double evaluate(@NotNull AssociationRule<?> rule);

}