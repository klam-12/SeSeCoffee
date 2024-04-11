package com.example.sesecoffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.databinding.HistoryItemBinding
import com.example.sesecoffee.model.OrderItem

class HistoryAdapter (val context: Context?):  RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){
    lateinit var binding : HistoryItemBinding

    inner class HistoryViewHolder(var binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<OrderItem>(){
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
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