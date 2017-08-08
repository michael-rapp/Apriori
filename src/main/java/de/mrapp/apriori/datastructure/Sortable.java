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
package de.mrapp.apriori.datastructure;

import de.mrapp.apriori.Sorting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Defines the interface, a data structure, which is sortable using {@link Comparable} or {@link
 * Sorting} instances, must implement.
 *
 * @param <DataStructureType> The type of the data structure
 * @param <T>                 The type of the items, which are contained by the data structure
 * @author Michael Rapp
 * @since 1.2.0
 */
public interface Sortable<DataStructureType, T> {

    /**
     * Sorts the items, which are contained by the data structure.
     *
     * @param comparator The comparator, which should be used, as an instance of the type {@link
     *                   Comparable} or null, if the natural ordering should be used. It might be an
     *                   instance of the class {@link Sorting}
     * @return A new data structure, which contains the items in sorted order, as an instance of the
     * generic type DataStructureType. The data structure may not be null
     */
    @NotNull
    DataStructureType sort(@Nullable final Comparator<T> comparator);

}