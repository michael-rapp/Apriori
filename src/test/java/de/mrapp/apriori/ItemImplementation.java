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

import java.io.Serializable;

/**
 * An implementation of the type {@link Item}, which is used for test purposes.
 *
 * @author Michael Rapp
 */
public class ItemImplementation implements Item, Serializable {

    /**
     * The constant serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the item.
     */
    private final String name;

    /**
     * Creates a new implementation of the type {@link Item}.
     *
     * @param name The name of the item as a {@link String}. The name may neither be null, nor
     *             empty
     */
    public ItemImplementation(@NotNull final String name) {
        // TODO: Throw exceptions
        this.name = name;
    }

    /**
     * Returns the name of the item.
     *
     * @return The name of the item as a {@link String}. The name may neither be null, nor empty
     */
    @NotNull
    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return getName();
    }


    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemImplementation other = (ItemImplementation) obj;
        return name.equals(other.name);
    }

}