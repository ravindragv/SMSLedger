package io.github.ravindragv.smsledger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transaction::class),
          version = 3)
abstract class TransactionDB : RoomDatabase(){

    abstract fun transactionDAO(): TransactionDAO

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: TransactionDB? = null

        fun getInstance(context: Context): TransactionDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TransactionDB::class.java, "Transaction.db")
                .fallbackToDestructiveMigration() // TBD Fix me with a proper migration
                .build()
    }
}