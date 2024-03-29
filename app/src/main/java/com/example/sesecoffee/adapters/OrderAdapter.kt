package com.example.sesecoffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.OrderItemBinding
import com.example.sesecoffee.model.OrderItem

class OrderAdapter(val context: Context, val itemList: List<OrderItem>)
    : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>()
{
    lateinit var binding : OrderItemBinding
    inner class OrderViewHolder(var binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
//            Glide.with(itemView).load(orderItem.imageUrl).into(productImageView)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        binding = OrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderItem = itemList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun deleteItem(position: Int){
        itemList.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }
}