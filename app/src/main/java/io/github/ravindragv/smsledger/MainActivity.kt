package io.github.ravindragv.smsledger

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        requestPermissionsToReadSMS()

        mTransactionDB = TransactionDB.getInstance(this)

        val scope = CoroutineScope(context = Dispatchers.Main)
        scope.launch {
            setUpAccountsView()
        }
    }

    private fun requestPermissionsToReadSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
            == PackageManager.PERMISSION_GRANTED) {
            mTelephonyPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
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
            } else {
                Toast.makeText(this, "Need Telephony permissions to read SMS!!",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun setUpAccountsView() {
        val accountsList = mTransactionDB.transactionDAO().getAllAccounts()
        Log.e(Constants.LOG_TAG, "$accountsList")

        if (accountsList.isNotEmpty()) {
            binding.tvNoTransactions.visibility = View.GONE
            binding.llAccounts.visibility = View.VISIBLE

            binding.rvAccountsList.layoutManager = LinearLayoutManager(applicationContext)
            binding.rvAccountsList.adapter = AccountsItemsAdapter(applicationContext, accountsList)
        } else {
            binding.tvNoTransactions.visibility = View.VISIBLE
            binding.llAccounts.visibility = View.GONE
        }
    }
}