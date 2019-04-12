/*
 * Copyright 2017 - 2019 Michael Rapp
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
package de.mrapp.apriori

import de.mrapp.util.Condition

/**
 * An implementation of the type [Item], which is used for test purposes. Each item can
 * unambiguously be identified via its name.
 *
 * @property name The name of the item
 * @author Michael Rapp
 */
data class NamedItem(val name: String) : Item {

    init {
        Condition.ensureNotEmpty(name, "The name may not be empty")
    }

    override fun compareTo(other: Item) = toString().compareTo(other.toString())

    override fun toString() = name

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + name.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as NamedItem
        return name == another.name
    }

}
