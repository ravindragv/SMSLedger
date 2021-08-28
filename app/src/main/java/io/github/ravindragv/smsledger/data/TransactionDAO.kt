package io.github.ravindragv.smsledger.data

import androidx.room.*
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.data.Transaction

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Query("DELETE FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME} WHERE accNumber = :accNum")
    suspend fun deleteAllTransactions(accNum: Int)

    @Query("SELECT * FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME}")
    suspend fun getAllTransactions() : List<Transaction>

    @Query("SELECT * FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME} WHERE accNumber = :accNum ORDER BY timestamp DESC")
    suspend fun getAllTransactions(accNum: Int) : List<Transaction>

    @Query("SELECT DISTINCT accNumber FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME}")
    suspend fun getAllAccounts() : List<Int>

    @androidx.room.Transaction
    suspend fun deleteAndInsert(accNum: Int, transactions: List<Transaction>) {
        deleteAllTransactions(accNum)
        insertTransactions(transactions)
    }
}