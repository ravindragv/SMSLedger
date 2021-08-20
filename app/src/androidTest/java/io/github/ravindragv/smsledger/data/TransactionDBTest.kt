package io.github.ravindragv.smsledger.data

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import io.github.ravindragv.smsledger.TestConstants
import org.junit.Assert.assertEquals
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class TransactionDBTest {
    private lateinit var database: TransactionDB
    private lateinit var transactionDAO: TransactionDAO
    private val testTransactionList = TestConstants.getTestMessageSamples()

    @Before
    fun createDb() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, TransactionDB::class.java).build()
        transactionDAO = database.transactionDAO()

        transactionDAO.insertTransactions(testTransactionList)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetAllTransactions() = runBlocking {
        Log.e("SMSLedger Test", "Running testGetAllTransactions tests")
        val transactionList = transactionDAO.getAllTransactions()
        assertEquals(transactionList.size, testTransactionList.size)

        assert(transactionList.containsAll(testTransactionList))
    }

    @Test
    fun testGetAllTransactionsForAccNum() = runBlocking {
        val accNumOccurences = testTransactionList.groupingBy { it.accNumber }.eachCount()

        for (accNum in accNumOccurences) {
            if (accNum.value > 1) {
                Log.e("SMSLedger Test", "${accNum.key} is repeated ${accNum.value} times")
                assertEquals(transactionDAO.getAllTransactions(accNum.key).size, accNum.value)
            }
        }
    }
}