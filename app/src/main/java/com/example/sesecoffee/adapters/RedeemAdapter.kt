package com.example.sesecoffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.OrderItemBinding
import com.example.sesecoffee.databinding.RedeemItemBinding
import com.example.sesecoffee.model.Redeem

class RedeemAdapter (val context: Context?)
    : RecyclerView.Adapter<RedeemAdapter.RedeemViewHolder>()
{
    lateinit var binding : RedeemItemBinding
    inner class RedeemViewHolder(var binding: RedeemItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(redeemItem: Redeem){
//        binding.imageView = redeemItem
        binding.apply {
            Glide.with(itemView).load(redeemItem.imageUrl).into(imageView)
        }
        binding.pointsBtn.setOnClickListener {
//            Chuyển đến trang thanh toán
        }
    }
}

    private val diffCallback = object : DiffUtil.ItemCallback<Redeem>(){
        override fun areItemsTheSame(oldItem: Redeem, newItem: Redeem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Redeem, newItem: Redeem): Boolean {
            return  oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RedeemViewHolder {
        binding = RedeemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return RedeemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RedeemViewHolder, position: Int) {
        val redeemItem =  differ.currentList[position]
        holder.bind(redeemItem)
    }


}