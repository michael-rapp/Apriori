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

import de.mrapp.apriori.Item;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * An extension of the class {@link ItemSet}, which allows to store the transactions, the item
 * set occurs in.
 *
 * @param <ItemType> The type of the items, which are contained by the item set
 * @author Michael Rapp
 * @since 1.0.0
 */
public class TransactionalItemSet<ItemType extends Item> extends ItemSet<ItemType> {

    /**
     * A map, which contains the transactions, the item set occurs in. The transactions are
     * mapped to unique ids.
     */
    private Map<Integer, Transaction<ItemType>> transactions;

    /**
     * Creates an empty item set.
     */
    public TransactionalItemSet() {
        super();
        this.transactions = new HashMap<>();
    }

    /**
     * Creates a new item set by copying another item set.
     *
     * @param itemSet The item set, which should be copied, as an instance of the class {@link
     *                TransactionalItemSet}. The item set may not be null
     */
    public TransactionalItemSet(@NotNull final TransactionalItemSet<ItemType> itemSet) {
        super(itemSet);
        this.transactions = new HashMap<>(itemSet.transactions);
    }

    /**
     * Returns the transactions, the item set occurs in.
     *
     * @return A map, which contains the transactions, the item set occurs in, as an instance of the
     * type {@link Map}. The map may not be null
     */
    @NotNull
    public final Map<Integer, Transaction<ItemType>> getTransactions() {
        return transactions;
    }

    /**
     * Sets the transactions, the item set occurs in.
     *
     * @param transactions A map, which contains the transactions, which should be set, as an
     *                     instance of the type {@link Map}. The map may not be null
     */
    public final void setTransactions(
            @NotNull final Map<Integer, Transaction<ItemType>> transactions) {
        ensureNotNull(transactions, "The map may not be null");
        this.transactions = transactions;
    }

}
