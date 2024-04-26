package com.example.sesecoffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.databinding.HistoryItemBinding
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem

class HistoryAdapter (val context: Context?):  RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){
    lateinit var binding : HistoryItemBinding

    inner class HistoryViewHolder(var binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(order: Order){
            binding.order = order
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        binding = HistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val orderItem =  differ.currentList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}