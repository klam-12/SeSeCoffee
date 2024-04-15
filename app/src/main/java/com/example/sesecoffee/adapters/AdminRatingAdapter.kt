package com.example.sesecoffee.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.AdminRatingFragment
import com.example.sesecoffee.databinding.AdminOrderItemBinding
import com.example.sesecoffee.databinding.AdminRatingItemBinding
import com.example.sesecoffee.model.Order

class AdminRatingAdapter
    : RecyclerView.Adapter<AdminRatingAdapter.RatingViewHolder>()
{
        inner class RatingViewHolder(private val itemBinding : AdminRatingItemBinding):RecyclerView.ViewHolder(itemBinding.root){
            fun bind(order: Order){

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        return RatingViewHolder(
            AdminRatingItemBinding.inflate(
                LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val currentOrder = differ.currentList[position]
        holder.bind(currentOrder);


    }


}