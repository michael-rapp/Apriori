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
package de.mrapp.apriori.datastructure

import de.mrapp.apriori.Filter

import java.util.function.Predicate

/**
 * Defines the interface, a data structure, which is filterable using [Predicate] or [Filter]
 * instances, must implement.
 *
 * @param DataStructureType The type of the data structure
 * @param T                 The type of the items, which are contained by the data structure
 * @author Michael Rapp
 * @since 1.2.0
 */
interface Filterable<DataStructureType, T> {

    /**
     * Filters the items, which are contained by the data structure. Only the items, which are
     * accepted by the given [Predicate] or [Filter], are retained.
     *
     * @return A new data structure, which contains the filtered items
     */
    fun filter(predicate: Predicate<in T>): DataStructureType

}
