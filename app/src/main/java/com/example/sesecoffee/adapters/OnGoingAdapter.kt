package com.example.sesecoffee.adapters

import android.content.Context;
import android.content.Intent
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView;
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.OrderDetailActivity
import com.example.sesecoffee.databinding.OnGoingItemBinding
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem

class OnGoingAdapter (val context: Context?):  RecyclerView.Adapter<OnGoingAdapter.OnGoingViewHolder>(){
    lateinit var binding : OnGoingItemBinding

    inner class OnGoingViewHolder(private val binding: OnGoingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val order = differ.currentList[position]
                    // Start the activity here using the context
                    val intent = Intent(context, OrderDetailActivity::class.java)
//                    intent.putExtra('order_detail', order)
                    context?.startActivity(intent)
                }
            }

        }

        fun bind(order: Order) {
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
    ): OnGoingViewHolder {
        binding = OnGoingItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)

        return OnGoingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: OnGoingViewHolder, position: Int) {
        val order=  differ.currentList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}