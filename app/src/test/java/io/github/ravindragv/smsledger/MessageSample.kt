package io.github.ravindragv.smsledger

data class MessageSample(
    val msgBody: String,
    val transactionType: MessageParser.TransactionType,
    val accountType: MessageParser.AccountType,
    val transactionAmount: Float
)
