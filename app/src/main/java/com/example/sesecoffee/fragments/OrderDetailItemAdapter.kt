package com.example.sesecoffee.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.OrderDetailItemBinding
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Format

class OrderDetailItemAdapter(private val orderItems: List<OrderItem>) : RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder>() {

    var format: Format = Format()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: OrderDetailItemBinding = DataBindingUtil.inflate(inflater, R.layout.order_detail_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = orderItems[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    inner class ViewHolder(private val binding: OrderDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(orderItem: OrderItem) {
            binding.orderItem = orderItem
            Glide.with(itemView)
                .load(orderItem.productImage)
                .placeholder(R.drawable.cancel) // optional placeholder while loading
                .error(R.drawable.ic_error) // optional error placeholder
                .into(binding.productImage)

            binding.productName.text = orderItem.productName
            binding.quantity.text = "x ${orderItem.quantity}" // Assuming quantity is an Int
            binding.hotCold.text = orderItem.hotCold
            binding.milk.text = orderItem.milk
            binding.price.text = "Price: " + orderItem.price?.let { format.formatToDollars(it) }// Assuming price is a String
            binding.executePendingBindings()
        }

    }

}


