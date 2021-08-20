package io.github.ravindragv.smsledger.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.adapters.AccountTransactionsItemsAdapter
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.data.TransactionDB
import io.github.ravindragv.smsledger.databinding.ActivityAccountTransactionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountTransactions : AppCompatActivity() {
    private lateinit var mTransactionDB: TransactionDB
    private lateinit var binding: ActivityAccountTransactionsBinding
    private lateinit var mAccTransactionsList: List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountTransactionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mTransactionDB = TransactionDB.getInstance(this)

        var accNum: Int = -1
        if (intent.hasExtra(Constants.ACCOUNT_NUMBER)) {
            accNum = intent.getIntExtra(Constants.ACCOUNT_NUMBER, -1)
        }

        if (accNum != -1) {
            val ioScope = CoroutineScope(Dispatchers.IO)
            ioScope.launch {
                setUpTransactionsView(accNum)
            }
        }
    }

    private suspend fun setUpTransactionsView(accNum: Int) {
        mAccTransactionsList = mTransactionDB.transactionDAO().getAllTransactions(accNum)

        withContext(Dispatchers.Main) {
            updateAccountTransactionsView()
        }
    }

    private fun updateAccountTransactionsView() {
        binding.rvAccTransactions.layoutManager = LinearLayoutManager(applicationContext)
        binding.rvAccTransactions.adapter = AccountTransactionsItemsAdapter(applicationContext,
                                                                            mAccTransactionsList)
        binding.llAccTransactions.visibility = View.VISIBLE
    }
}