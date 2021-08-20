package io.github.ravindragv.smsledger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.ravindragv.smsledger.databinding.ItemAccountBinding

class AccountsItemsAdapter(private val context: Context, private var list:List<Int>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountsViewHolder(ItemAccountBinding
            .inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AccountsViewHolder) {
            holder.binding.tvAccNum.text = list[position].toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(accNumber: Int)
    }

    inner class AccountsViewHolder(val binding:ItemAccountBinding)
        : RecyclerView.ViewHolder(binding.root)
}