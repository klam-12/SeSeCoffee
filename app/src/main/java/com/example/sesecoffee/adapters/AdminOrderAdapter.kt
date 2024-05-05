package com.example.sesecoffee.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format

class AdminOrderAdapter
    : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>()
{
    private val fbSingleton = FirebaseSingleton.getInstance()

    inner class OrderViewHolder(private val itemBinding: AdminOrderItemBinding):RecyclerView.ViewHolder(itemBinding.root){
        private var format : Format = Format()
        fun bind(order: Order,username: String){
            itemBinding.orderItem = order
            itemBinding.timeOrder.text = order.createAt?.let { format.timestampToFormattedString(it) }
            itemBinding.totalBill.text = order.total?.let { format.formatToDollars(it) }
            itemBinding.username.text = username

            if(order.delivered){
                itemBinding.chipStatus.text = "Done"
                itemBinding.chipStatus.setChipBackgroundColorResource(R.color.dark_grey)
            }else{
                itemBinding.chipStatus.text = "Pending"
                itemBinding.chipStatus.setChipBackgroundColorResource(R.color.yellow)
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
        var username = ""

        val userId : String = currentOrder.userId!!
        if (userId != null && userId != "") {
            fbSingleton.db.collection(Constant.USER_COLLECTION).document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        username = document.data?.get(key = "fullname").toString()
                        holder.bind(currentOrder,username);
                    }
                }
                .addOnFailureListener {
                    Log.i("AdminErr", it.message.toString())
                }
        }else{
            holder.bind(currentOrder,username);
        }



        holder.itemView.setOnClickListener(){
            val intent = Intent(it.context,AdminEditOrderActivity::class.java)
            intent.putExtra("orderId",currentOrder.id.toString())
            intent.putExtra("position", position)
            it.context.startActivity(intent)
        }
    }
}