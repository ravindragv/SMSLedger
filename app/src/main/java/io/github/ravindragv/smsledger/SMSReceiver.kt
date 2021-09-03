package io.github.ravindragv.smsledger

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.ravindragv.smsledger.activities.AccountTransactions
import io.github.ravindragv.smsledger.activities.MainActivity
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.data.TransactionDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    val transaction: Transaction? = prepareMessage(msgBody, msgFrom, ts, context, pendingResult)
                    if (transaction != null) {
                        withContext(Dispatchers.Main) {
                            sendNotification(transaction, context)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(transaction: Transaction, context: Context?) {
        if (context == null) return

        val intent = Intent(context, AccountTransactions::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra(Constants.ACCOUNT_NUMBER, transaction.accNumber)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val content = when (transaction.transactionType) {
                MessageParser.TransactionType.CREDIT -> "Credit"
                MessageParser.TransactionType.DEBIT -> "Debit"
                else -> "Unknown Transaction"
            } + " in XX" + transaction.accNumber
        val text = transaction.pos + " " + transaction.transactionAmount

        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setContentTitle(content)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_stat_ledger_with_rupee_cutout);
            builder.color = ContextCompat.getColor(context, R.color.day_primary)
        } else {
            builder.setSmallIcon(R.drawable.ic_stat_ledger_with_rupee_cutout);
        }

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }

    private suspend fun prepareMessage(msgBody: String, msgFrom: String, ts: Long,context: Context?,
                                       pendingResult: PendingResult): Transaction? {
        val msgParser = MessageParser()
        val transaction: Transaction? = msgParser.getTransaction(msgBody, msgFrom, ts)
        if  (transaction != null) {
            val tdb = context?.let { TransactionDB.getInstance(it).transactionDAO() }
            if (tdb != null) {
                if (transaction.accNumber < 1000) {
                    val uniqueAccNums = tdb.getAllAccounts()
                    for (accNum in uniqueAccNums) {
                        if (accNum.toString().contains(transaction.accNumber.toString())) {
                            transaction.accNumber = accNum
                            break
                        }
                    }
                }
                tdb.insertTransactions(listOf(transaction))
            }
        } else {
            Log.e(Constants.LOG_TAG, "Invalid transaction for msg $msgBody")
        }
        pendingResult.finish()

        return transaction
    }
}