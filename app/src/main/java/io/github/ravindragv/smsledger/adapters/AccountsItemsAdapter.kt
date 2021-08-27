package io.github.ravindragv.smsledger.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.ravindragv.smsledger.Constants
import io.github.ravindragv.smsledger.databinding.ItemAccountBinding

class AccountsItemsAdapter(private val context: Context, private var list:List<Int>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mOnClickListener: OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountsViewHolder(ItemAccountBinding
            .inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AccountsViewHolder) {
            holder.binding.tvAccNum.text = "XX"+list[position].toString()

            holder.binding.root.setOnClickListener {
                mOnClickListener.onClick(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(accNumber: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        mOnClickListener = onClickListener
    }

    inner class AccountsViewHolder(val binding:ItemAccountBinding)
        : RecyclerView.ViewHolder(binding.root)
}