package io.github.ravindragv.smsledger.data

import io.github.ravindragv.smsledger.MessageParser

data class Transaction(
    val smsMsg: String,
    val transactionType: MessageParser.TransactionType,
    val accountType: MessageParser.AccountType,
    val transactionAmount: Float,
)
