package io.github.ravindragv.smsledger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.data.TransactionDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            val msgs: Array<SmsMessage?>?
            var msgFrom = ""
            if (bundle != null) {
                var msgBody = ""
                try {
                    val pdus = bundle["pdus"] as Array<*>?
                    msgs = arrayOfNulls(pdus!!.size)
                    for (i in msgs.indices) {
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        msgFrom = msgs[i]!!.originatingAddress.toString()
                        msgBody += msgs[i]!!.messageBody

                        Log.e(Constants.LOG_TAG, "message $msgBody from $msgFrom")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Do this in the background via a coroutine so as to not block the onReceive
                val scope = CoroutineScope(context = Dispatchers.IO)
                val pendingResult: PendingResult = goAsync()
                scope.launch {
                    prepareMessage(msgBody, msgFrom, context, pendingResult)
                }
            }
        }
    }

    private suspend fun prepareMessage(msgBody: String,
                                       msgFrom: String,
                                       context: Context?,
                                       pendingResult: PendingResult) {
        val msgParser = MessageParser()
        val transaction = Transaction(msgBody,
                            msgParser.getTransactionType(msgBody),
                            msgParser.getAccountType(msgBody),
                            msgParser.getTransactionAmt(msgBody))

        Log.e(Constants.LOG_TAG, "Parsed transaction is $transaction")

        val tdb = context?.let { TransactionDB.getInstance(it).transactionDAO() }
        tdb?.insertTransactions(transaction)
        pendingResult.finish()
    }
}