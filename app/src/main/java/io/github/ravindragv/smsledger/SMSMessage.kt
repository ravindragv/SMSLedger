package io.github.ravindragv.smsledger

data class SMSMessage(
    val msgBody: String,
    val transactionType: MessageParser.TransactionType,
    val accountType: MessageParser.AccountType,
    val transactionAmount: Float,
)
