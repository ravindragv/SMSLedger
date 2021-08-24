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
            var ts = 0L
            if (bundle != null) {
                var msgBody = ""
                try {
                    val pdus = bundle["pdus"] as Array<*>?
                    msgs = arrayOfNulls(pdus!!.size)
                    for (i in msgs.indices) {
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        msgFrom = msgs[i]!!.originatingAddress.toString()
                        msgBody += msgs[i]!!.messageBody
                        ts = msgs[i]!!.timestampMillis
                        Log.e(Constants.LOG_TAG, "message $msgBody from $msgFrom")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Do this in the background via a coroutine so as to not block the onReceive
                val scope = CoroutineScope(context = Dispatchers.IO)
                val pendingResult: PendingResult = goAsync()
                scope.launch {
                    prepareMessage(msgBody, msgFrom, ts, context, pendingResult)
                }
            }
        }
    }

    private suspend fun prepareMessage(msgBody: String, msgFrom: String, ts: Long,context: Context?,
                                       pendingResult: PendingResult) {
        val msgParser = MessageParser()
        val transaction: Transaction? = msgParser.getTransaction(msgBody, msgFrom, ts)
        if  (transaction != null) {
            val tdb = context?.let { TransactionDB.getInstance(it).transactionDAO() }
            tdb?.insertTransactions(listOf(transaction))
        } else {
            Log.e(Constants.LOG_TAG, "Invalid transaction for msg $msgBody")
        }
        pendingResult.finish()
    }
}