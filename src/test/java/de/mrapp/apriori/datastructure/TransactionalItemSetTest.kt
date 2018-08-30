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

import de.mrapp.apriori.DataIterator.TransactionImplementation
import de.mrapp.apriori.NamedItem
import de.mrapp.apriori.Transaction
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests the functionality of the class [TransactionalItemSet].
 *
 * @author Michael Rapp
 */
class TransactionalItemSetTest {

    @Test
    fun testDefaultConstructor() {
        val transactionalItemSet = TransactionalItemSet<NamedItem>()
        val transactions = transactionalItemSet.transactions
        assertNotNull(transactions)
        assertTrue(transactions.isEmpty())
    }

    @Test
    fun testConstructorWithItemSetParameter() {
        val item = NamedItem("a")
        val transactions = HashMap<Int, Transaction<NamedItem>>()
        transactions[0] = TransactionImplementation("foo")
        transactions[1] = TransactionImplementation("bar")
        val transactionalItemSet1 = TransactionalItemSet<NamedItem>()
        transactionalItemSet1.support = 0.5
        transactionalItemSet1.add(item)
        transactionalItemSet1.transactions = transactions
        val transactionalItemSet2 = TransactionalItemSet(
                transactionalItemSet1)
        assertEquals(transactionalItemSet1.support, transactionalItemSet2.support)
        assertEquals(transactionalItemSet1.size.toLong(), transactionalItemSet2.size.toLong())
        assertEquals(transactionalItemSet1.first(), transactionalItemSet2.first())
        assertEquals(transactionalItemSet1.transactions.size.toLong(),
                transactionalItemSet2.transactions.size.toLong())
        assertEquals(transactionalItemSet1.transactions[0],
                transactionalItemSet2.transactions[0])
        assertEquals(transactionalItemSet1.transactions[1],
                transactionalItemSet2.transactions[1])
    }

    @Test
    fun testSetTransactions() {
        val transactionalItemSet = TransactionalItemSet<NamedItem>()
        assertTrue(transactionalItemSet.transactions.isEmpty())
        val transactions = HashMap<Int, Transaction<NamedItem>>()
        transactions[0] = TransactionImplementation("foo")
        transactions[1] = TransactionImplementation("bar")
        transactionalItemSet.transactions = transactions
        assertEquals(transactions, transactionalItemSet.transactions)
    }

}