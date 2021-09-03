package io.github.ravindragv.smsledger.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.R
import io.github.ravindragv.smsledger.SMSInboxWorker
import io.github.ravindragv.smsledger.adapters.AccountsItemsAdapter
import io.github.ravindragv.smsledger.data.TransactionDB
import io.github.ravindragv.smsledger.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mTelephonyPermissionGranted = false
    private lateinit var mTransactionDB: TransactionDB
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val READ_TELEPHONY = 1
        const val IS_INBOX_READ = "is_inbox_read"
        const val CHANNEL_ID = "SMS_Ledger_Transaction"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val isInboxRead = getPreferences(Context.MODE_PRIVATE).getBoolean(IS_INBOX_READ, false)
        setUpViews(isInboxRead)
        requestPermissionsToReadSMS()

        createNotificationChannel()

        mTransactionDB = TransactionDB.getInstance(this)

        val scope = CoroutineScope(context = Dispatchers.Main)
        scope.launch {
            setUpAccountsView()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setUpViews(isInboxRead: Boolean) {
        //Log.e(Constants.LOG_TAG, "isInboxRead $isInboxRead")
        binding.llAccountsBuilding.visibility = View.VISIBLE
        if (!isInboxRead) {
            binding.tvNoTransactions.setText(R.string.reading_inbox)
            binding.lpInboxReading.visibility = View.VISIBLE
        } else {
            binding.tvNoTransactions.setText(R.string.no_transactions)
            binding.lpInboxReading.visibility = View.GONE
        }
        binding.llAccounts.visibility = View.GONE
    }

    private fun readInbox() {
        val isInboxRead = getPreferences(Context.MODE_PRIVATE)
            .getBoolean(IS_INBOX_READ, false)
        if (!isInboxRead) {
            val inboxReader: WorkRequest = OneTimeWorkRequestBuilder<SMSInboxWorker>().build()
            val workManager = WorkManager.getInstance(this)

            workManager.enqueue(inboxReader)
            workManager.getWorkInfoByIdLiveData(inboxReader.id)
                .observe(this, { info ->
                    if (info != null && info.state.isFinished && info.state == WorkInfo.State.SUCCEEDED) {
                        val sharedPref = getPreferences(Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putBoolean(IS_INBOX_READ, true)
                            apply()
                        }

                        val scope = CoroutineScope(context = Dispatchers.Main)
                        scope.launch {
                            setUpAccountsView()
                        }
                    }
                })
        }
    }

    private fun requestPermissionsToReadSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED) {
            mTelephonyPermissionGranted = true
            readInbox()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                READ_TELEPHONY
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_TELEPHONY) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mTelephonyPermissionGranted = true
                readInbox()
            } else {
                binding.tvNoTransactions.setText(R.string.permission_required)
                binding.lpInboxReading.visibility = View.GONE
            }
        }
    }

    private suspend fun setUpAccountsView() {
        val accountsList = mTransactionDB.transactionDAO().getAllAccounts()
        Log.e(Constants.LOG_TAG, "$accountsList")

        if (accountsList.isNotEmpty()) {
            binding.llAccountsBuilding.visibility = View.GONE
            binding.llAccounts.visibility = View.VISIBLE

            binding.rvAccountsList.layoutManager = LinearLayoutManager(applicationContext)
            val accountsAdapter = AccountsItemsAdapter(applicationContext, accountsList)
            binding.rvAccountsList.adapter = accountsAdapter
            accountsAdapter.setOnClickListener(object: AccountsItemsAdapter.OnClickListener{
                override fun onClick(accNumber: Int) {
                    val intent = Intent(this@MainActivity, AccountTransactions::class.java)
                    intent.putExtra(Constants.ACCOUNT_NUMBER, accNumber)

                    startActivity(intent)
                }
            })
        }
    }
}