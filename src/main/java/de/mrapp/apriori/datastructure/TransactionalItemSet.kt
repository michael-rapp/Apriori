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

import de.mrapp.apriori.Item
import de.mrapp.apriori.ItemSet
import de.mrapp.apriori.Transaction
import java.util.*

/**
 * An extension of the class [ItemSet], which allows to store the transactions, the item
 * set occurs in.
 *
 * @param ItemType The type of the items, which are contained by the item set
 * @author Michael Rapp
 * @since 1.0.0
 */
class TransactionalItemSet<ItemType : Item> : ItemSet<ItemType> {

    /**
     * A map that contains the transactions, the item set occurs in. The transactions are
     * mapped to unique ids.
     */
    var transactions: MutableMap<Int, Transaction<ItemType>>

    /**
     * Creates an empty item set.
     */
    constructor() : super() {
        this.transactions = HashMap()
    }

    /**
     * Creates a new item set by copying another [itemSet].
     */
    constructor(itemSet: TransactionalItemSet<ItemType>) : super(itemSet) {
        this.transactions = HashMap(itemSet.transactions)
    }

}
