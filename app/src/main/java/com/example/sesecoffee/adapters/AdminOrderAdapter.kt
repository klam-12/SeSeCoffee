package com.example.sesecoffee.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.AdminEditOrderActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.AdminOrderItemBinding
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.User
import com.example.sesecoffee.utils.Format

class AdminOrderAdapter
    : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>()
{
    inner class OrderViewHolder(private val itemBinding: AdminOrderItemBinding):RecyclerView.ViewHolder(itemBinding.root){
        private var format : Format = Format()
        private val fbSingleton = FirebaseSingleton.getInstance()
        fun bind(order: Order){
            itemBinding.orderItem = order
            itemBinding.timeOrder.text = order.createAt?.let { format.timestampToFormattedString(it) }
            itemBinding.totalBill.text = "BYN: " + order.total?.let { format.formatNumber(it) }

            val userId = order.userId
            if (userId != null) {
                fbSingleton.db.collection("USER").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            itemBinding.username.text = document.data?.get(key = "fullname").toString()
                        }
                    }
                    .addOnFailureListener {
                        Log.i("AdminErr", it.message.toString())
                    }
            }

            if(order.delivered){
                itemBinding.chipStatus.text = "Done"
                itemBinding.chipStatus.setChipBackgroundColorResource(R.color.dark_grey)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffCallback);
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(AdminOrderItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = differ.currentList[position]
        holder.bind(currentOrder);

        holder.itemView.setOnClickListener(){
            val intent = Intent(it.context,AdminEditOrderActivity::class.java)
            intent.putExtra("orderId",currentOrder.id.toString())
            intent.putExtra("position", position)
            it.context.startActivity(intent)
        }
    }
}