package com.example.sesecoffee.adapters


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.HistoryRewardsItemBinding
import com.example.sesecoffee.databinding.OrderItemBinding
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem

import java.util.ArrayList;

class RewardAdapter(val context: Context?):  RecyclerView.Adapter<RewardAdapter.RewardViewHolder>(){
    lateinit var binding : HistoryRewardsItemBinding


//    var context: Context;
//
//    ArrayList<Order> list;
//
//
//    public RewardAdapter(Context context, ArrayList<User> list) {
////        this.context = context;
////        this.list = list;
//    }

    inner class RewardViewHolder(var binding: HistoryRewardsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
//            binding.apply {
//                Glide.with(itemView).load(orderItem.)
//            }
//            bind.rewardItemCardView.setOnCL
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
    ): RewardViewHolder {
        binding = HistoryRewardsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return RewardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val orderItem =  differ.currentList[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}