package io.github.ravindragv.smsledger

class MessageParser {
    private val currencyRegex = Regex("(INR|Rs)[ .]*([0-9]*[,.])*.[0-9]*")
    private val debitRegex = Regex("(?i)(spent|debited|tx|using|for)")
    private val creditRegex = Regex("(?i)(credited|reversed|Contribution)")
    private val avlBalanceRegex = Regex("(?i)(Avl Bal|Balance|Available Balance)")
    private val accTypeRegex = Regex("(?i)(Account|A/c|Acct)")
    private val ccRegex = Regex("(?i)(Credit Card)")
    private val dcRegex = Regex("(?i)(Debit Card|(gift[ ]*card))")
    private val accNumberRegex = Regex("(?i)(ending (with )?[0-9]{3,}|[X]+[0-9]{3,})")

    enum class AccountType {
        ACCOUNT,
        CREDIT_CARD,
        DEBIT_CARD,
        UNKNOWN
    }

    enum class TransactionType {
        DEBIT,
        CREDIT,
        INVALID
    }

    fun getAccountType(message: String) : AccountType {
        return when {
            accTypeRegex.find(message, 0) != null -> AccountType.ACCOUNT
            ccRegex.find(message, 0) != null -> AccountType.CREDIT_CARD
            dcRegex.find(message, 0) != null -> AccountType.DEBIT_CARD
            else -> AccountType.UNKNOWN
        }
    }

    fun getTransactionAmt(message: String) : Float {
        val amt = currencyRegex.find(message, 0) ?: return 0.0f
        val amtTokens = amt.value.split(" ")

        if (amtTokens.size < 2) {
            // Get rid of commas
            val amount = amt.value.replace(",", "")
            // For the mahanubhava who didn't put a delimiter
            return when {
                amount.contains("Rs.") -> amount.replace("Rs.", "").toFloat()
                amount.contains("Rs") -> amount.replace("Rs", "").toFloat()
                amount.contains("INR.") -> amount.replace("INR.", "").toFloat()
                amount.contains("INR") -> amount.replace("INR", "").toFloat()
                else -> 0.0f
            }
        }

        val amount = amtTokens[1].replace(",", "")
        return amount.toFloat()
    }

    fun getTransactionType(message: String) : TransactionType {
        return when {
            debitRegex.find(message, 0) != null -> TransactionType.DEBIT
            creditRegex.find(message, 0) != null -> TransactionType.CREDIT
            else -> TransactionType.INVALID
        }
    }

    fun getAccountNumber(message: String) : Int {
        val accNum = accNumberRegex.find(message, 0)?: return -1
        return accNum.value.replace("[^0-9]".toRegex(), "").toInt()
    }
}