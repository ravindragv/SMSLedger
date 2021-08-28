package io.github.ravindragv.smsledger

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.ravindragv.smsledger.Constants.LOG_TAG
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.data.TransactionDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SMSInboxWorker(private val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams){
    private val mMsgParser = MessageParser()
    private val mTdb = TransactionDB.getInstance(appContext).transactionDAO()

    private suspend fun prepareMessage(msgBody: String, msgFrom: String, ts: Long) {
        val transaction: Transaction? = mMsgParser.getTransaction(msgBody, msgFrom, ts)
        if  (transaction != null) {
            mTdb.insertTransactions(listOf(transaction))
        }
    }

    private fun reCheckDbForSmallAccNumbers() {
        runBlocking {
            val accList = mTdb.getAllAccounts()
            for (smallAccNum in accList) {
                if (smallAccNum < 1000) {
                    Log.e(LOG_TAG, "smallAccNum is $smallAccNum")
                    for (accNum in accList) {
                        if (accNum != smallAccNum &&
                            accNum.toString().contains(smallAccNum.toString())
                        ) {
                            val smallAccTrans = mTdb.getAllTransactions(smallAccNum)
                            for (transaction in smallAccTrans) {
                                transaction.accNumber = accNum
                            }
                            mTdb.deleteAndInsert(smallAccNum, smallAccTrans)
                        }
                    }
                }
            }
        }
    }

    override fun doWork(): Result {
        val cursor = appContext.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null)

        if (cursor != null) {
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    var msgData = ""
                    var msgBody = ""
                    var msgFrom = ""
                    var ts = 0L
                    for (i in 0 until cursor.columnCount)
                    {
                        msgData += " " + cursor.getColumnName(i) + ":" + cursor.getString(i)
                        when (cursor.getColumnName(i)) {
                            "body" -> msgBody = cursor.getString(i)
                            "address" -> msgFrom = cursor.getString(i)
                            "date" -> ts = cursor.getLong(i)
                        }
                    }
                    // use msgData
                    val scope = CoroutineScope(context = Dispatchers.IO)
                    scope.launch {
                        prepareMessage(msgBody, msgFrom, ts)
                    }
                } while (cursor.moveToNext())

                reCheckDbForSmallAccNumbers()
            } else {
                // empty box, no SMS
                Log.e("SMSInboxWorker", "Inbox is empty")
            }

            cursor.close()
        }

        return Result.success()
    }
}