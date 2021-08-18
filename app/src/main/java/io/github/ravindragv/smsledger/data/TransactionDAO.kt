package io.github.ravindragv.scratch

import androidx.room.*
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.data.Transaction

@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(vararg msgs: Transaction)

    @Delete
    suspend fun delete(msg: Transaction)

    @Query("SELECT * FROM ${Constants.TRANSACTIONS_ROOM_DB_NAME}")
    suspend fun getAllTransactions() : List<Transaction>
}