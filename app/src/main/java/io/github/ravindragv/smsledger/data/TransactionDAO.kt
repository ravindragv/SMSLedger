package io.github.ravindragv.smsledger.data

import androidx.room.*
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.data.Transaction

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Delete
    suspend fun delete(msg: List<Transaction>)

    @Query("SELECT * FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME}")
    suspend fun getAllTransactions() : List<Transaction>

    @Query("SELECT * FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME} where accNumber = :accNum")
    suspend fun getAllTransactions(accNum: Int) : List<Transaction>

    @Query("SELECT DISTINCT accNumber FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME}")
    suspend fun getAllAccounts() : List<Int>
}