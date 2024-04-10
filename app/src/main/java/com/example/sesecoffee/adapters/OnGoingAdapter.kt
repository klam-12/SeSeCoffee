package com.example.sesecoffee.adapters

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView;
import com.example.sesecoffee.databinding.OnGoingItemBinding
import com.example.sesecoffee.model.Order

class OnGoingAdapter (val context: Context?):  RecyclerView.Adapter<OnGoingAdapter.OnGoingViewHolder>(){
    lateinit var binding : OnGoingItemBinding

    inner class OnGoingViewHolder(var binding: OnGoingItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: Order){
            binding.order = orderItem
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
        val orderItem =  differ.currentList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}