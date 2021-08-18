package io.github.ravindragv.smsledger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import io.github.ravindragv.smsledger.data.Transaction

class SMSReceiver : BroadcastReceiver() {
    private var mMsgParser = MessageParser()

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

                        Log.e(MainActivity.LOG_TAG, "message $msgBody from $msgFrom")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                prepareMessage(msgBody, msgFrom)
            }
        }
    }

    private fun prepareMessage(msgBody: String, msgFrom: String) {
        val msg = Transaction(msgBody,
            mMsgParser.getTransactionType(msgBody),
            mMsgParser.getAccountType(msgBody),
            mMsgParser.getTransactionAmt(msgBody))

        Log.e(MainActivity.LOG_TAG, "Parsed message is $msg")
    }
}