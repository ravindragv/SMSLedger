package io.github.ravindragv.smsledger

import io.github.ravindragv.smsledger.data.Transaction
import java.lang.NumberFormatException

class MessageParser {
    private val currencyRegex = Regex("(INR|Rs)[ .]*([0-9]*[,.])*.[0-9]*")
    private val debitRegex = Regex("(?i)(spent|debited|tx|using|is used|transaction number)")
    private val creditRegex = Regex("(?i)(credited|reversed|Contribution)")
    private val avlBalanceRegex = Regex("(?i)(Avl Bal|Balance|Available Balance)")
    private val accTypeRegex = Regex("(?i)(Account|A/c|Acct)")
    private val ccRegex = Regex("(?i)(Credit Card)")
    private val dcRegex = Regex("(?i)(Debit Card|(gift[ ]*card))")
    private val accNumberRegex = Regex("(?i)(ending (with )?[0-9]{3,}|[X]+[0-9]{3,})")
    private val otpRegex = Regex("(?i)(otp)")
    /* There has to be better way -
        The ordering of the regex here is critical, i.e., if we see credited to first then don't
        look for anything else. If we see something like "at POS for" then don't look for anything
        else.

        This is not very robust since any new type of message can break this
    */
    private val posRegexList = listOf(Regex("(?i)(credited to (a/c no)*(\\.)*(\\s)*[^\\s]+)"),
                                            Regex("(?i)(Info:(\\s)*[^\\.]+)"),
                                            Regex("(?i)([a-z0-9\\.\\-]+@[a-z0-9]+)"),
                                            Regex("(?i)((at).*(for ))"),
                                            Regex("(?i)((at).*(on ))"))
    private val posAffix = listOf("credited to ", "Info: ", "Info:", "at ", " on ", " for ")

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

    fun getPos(message: String): String {
        var pos = ""
        for (pattern in posRegexList) {
            val loc = pattern.find(message, 0)
            if (loc != null) {
                pos = loc.value
                for (affix in posAffix) {
                    pos = pos.replace(affix, "")
                }
                break
            }
        }
        return pos
    }

    fun getTransactionAmt(message: String) : Float {
        val amt = currencyRegex.find(message, 0) ?: return 0.0f
        val amtTokens = amt.value.split(" ")
        var transactionAmt = 0.0f

        if (amtTokens.size < 2) {
            // Get rid of commas
            val amount = amt.value.replace(",", "")
            try {
                transactionAmt = when {
                    amount.contains("Rs.") -> amount.replace("Rs.", "").toFloat()
                    amount.contains("Rs") -> amount.replace("Rs", "").toFloat()
                    amount.contains("INR.") -> amount.replace("INR.", "").toFloat()
                    amount.contains("INR") -> amount.replace("INR", "").toFloat()
                    else -> 0.0f
                }
            } catch (e: NumberFormatException) {
                //e.printStackTrace()
            }
        } else {
            var amount = ""
            try {
                amount = amtTokens[1].replace(",", "")
                if (amount[amount.length - 1] == '.') {
                    amount = amount.dropLast(1)
                }

                if (amount.isNotEmpty()) {
                    transactionAmt = amount.toFloat()
                }

            } catch (e: Exception) {
                //e.printStackTrace()
            }
        }

        return transactionAmt
    }

    fun getTransactionType(message: String) : TransactionType {
        return when {
            otpRegex.find(message, 0) != null -> TransactionType.INVALID // Don't handle OTP
            debitRegex.find(message, 0) != null -> TransactionType.DEBIT
            creditRegex.find(message, 0) != null -> TransactionType.CREDIT
            else -> TransactionType.INVALID
        }
    }

    fun getAccountNumber(message: String) : Int {
        val matchResult = accNumberRegex.find(message, 0)?: return -1
        var accNum = matchResult.value.replace("[^0-9]".toRegex(), "")
        // The account number can be greater than 4, in such case let's keep it constant at 4 digits
        if (accNum.length > 4) {
            accNum = accNum.drop(accNum.length - 4)
        }
        return accNum.toInt()
    }

    fun getTransaction(message: String, msgFrom: String, ts: Long) :Transaction? {
        val transactionType = getTransactionType(message)

        if (TransactionType.INVALID == transactionType) return null
        var pos = getPos(message)
        if (pos.isEmpty()) {
            pos = when (transactionType) {
                TransactionType.CREDIT -> "credit"
                TransactionType.DEBIT -> "debit"
                else -> "Unknown POS"
            }
        }

        val accountType = getAccountType(message)
        val transAmt = getTransactionAmt(message)
        val accNumber = getAccountNumber(message)

        if (transactionType != TransactionType.INVALID &&
            accountType != AccountType.UNKNOWN &&
            accNumber != -1 &&
            transAmt.compareTo(0.0f) != 0) {
            return Transaction(message,
                msgFrom,
                ts,
                transactionType,
                accountType,
                transAmt,
                accNumber,
                pos)
        }
        return null
    }
}