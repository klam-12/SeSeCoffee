package com.example.sesecoffee.adapters

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.ProductUpdateOrderActivity
import com.example.sesecoffee.databinding.OrderItemBinding
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.viewModel.OrderItemsViewModel

class OrderAdapter(val context: Context)
    : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>()
{
    lateinit var binding : OrderItemBinding
    lateinit var viewModel : OrderItemsViewModel

    inner class OrderViewHolder(var binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
            binding.apply {
                Glide.with(itemView).load(orderItem.productImage).into(productImageView)
            }
            binding.orderItemCardView.setOnClickListener {
                val intent = Intent(context, ProductUpdateOrderActivity::class.java)
                intent.putExtra("order", orderItem.id)
                context.startActivity(intent)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<OrderItem>(){
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

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
        val orderItem =  differ.currentList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun deleteItem(position: Int, application: Application, orderId : String){
        viewModel = OrderItemsViewModel(application)
        viewModel.deleteOrderItem(differ.currentList[position].id!!, orderId)
        val orderList = differ.currentList.toMutableList()
        orderList.removeAt(position)
        differ.submitList(orderList)
        notifyItemRemoved(position)
    }
}