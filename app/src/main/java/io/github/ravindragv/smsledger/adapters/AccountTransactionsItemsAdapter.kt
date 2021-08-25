package io.github.ravindragv.smsledger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.github.ravindragv.smsledger.MessageParser
import io.github.ravindragv.smsledger.R
import io.github.ravindragv.smsledger.data.Transaction
import io.github.ravindragv.smsledger.databinding.ItemAccTransactionsBinding
import java.text.SimpleDateFormat
import java.util.*

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
            holder.binding.tvAccTransPos.text = list[position].pos

            val textColor = when (list[position].transactionType) {
                MessageParser.TransactionType.CREDIT -> ContextCompat.getColor(context, R.color.green)
                MessageParser.TransactionType.DEBIT -> ContextCompat.getColor(context, R.color.red)
                else -> ContextCompat.getColor(context, R.color.black)
            }

            holder.binding.tvAccTransAmt.setTextColor(textColor)

            holder.binding.root.setOnClickListener {
                if (holder.msgDetailsVisible) {
                    holder.msgDetailsVisible = false

                    holder.binding.llMsgDetails.visibility = View.GONE
                } else {
                    holder.msgDetailsVisible = true

                    holder.binding.tvSmsMsg.text = list[position].smsMsg
                    holder.binding.tvSmsMsgSender.text = list[position].sender
                    val date = SimpleDateFormat("dd-MMM-yyyy HH:mm").format(Date(list[position].timestamp))
                    holder.binding.tvSmsMsgDatetime.text = date
                    holder.binding.llMsgDetails.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AccountTransactionViewHolder(val binding: ItemAccTransactionsBinding,
                                             var msgDetailsVisible:Boolean = false)
        : RecyclerView.ViewHolder(binding.root)
}