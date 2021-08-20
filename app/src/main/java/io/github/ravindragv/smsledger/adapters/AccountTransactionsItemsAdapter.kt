package io.github.ravindragv.smsledger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.ravindragv.smsledger.MessageParser
import io.github.ravindragv.smsledger.R
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.databinding.ItemAccTransactionsBinding

class AccountTransactionsItemsAdapter (private val context: Context,
                                       private var list:List<Transaction>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountTransactionViewHolder(ItemAccTransactionsBinding
            .inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AccountTransactionsItemsAdapter.AccountTransactionViewHolder) {
            holder.binding.tvAccTransAmt.text = list[position].transactionAmount.toString()

            val textColor = when (list[position].transactionType) {
                MessageParser.TransactionType.CREDIT -> ContextCompat.getColor(context, R.color.green)
                MessageParser.TransactionType.DEBIT -> ContextCompat.getColor(context, R.color.red)
                else -> ContextCompat.getColor(context, R.color.black)
            }

            holder.binding.tvAccTransAmt.setTextColor(textColor)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AccountTransactionViewHolder(val binding: ItemAccTransactionsBinding)
        : RecyclerView.ViewHolder(binding.root)
}