package io.github.ravindragv.smsledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.MessageParser

@Entity(tableName = Constants.TRANSACTIONS_ROOM_DB_NAME)
data class Transaction(
    val smsMsg: String,
    val transactionType: MessageParser.TransactionType,
    val accountType: MessageParser.AccountType,
    val transactionAmount: Float,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
