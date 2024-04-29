package com.example.sesecoffee.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.ProductRedeemOrderActivity
import com.example.sesecoffee.databinding.RedeemItemBinding
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.model.UserSingleton

class RedeemAdapter (val context: Context?)
    : RecyclerView.Adapter<RedeemAdapter.RedeemViewHolder>()
{
    lateinit var binding : RedeemItemBinding
    inner class RedeemViewHolder(var binding: RedeemItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(redeemItem: Redeem){
        binding.redeemItem = redeemItem
        binding.apply {
            print(redeemItem)
            Glide.with(itemView).load(redeemItem.imageUrl).into(imageView)

        }
        binding.pointsBtn.setOnClickListener {
            val intent = Intent(context, ProductRedeemOrderActivity::class.java)
            intent.putExtra("redeem", redeemItem.id)
            intent.putExtra("productId", redeemItem.productId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context!!.startActivity(intent)
//            if(UserSingleton.instance?.redeemPoint != null && UserSingleton.instance?.redeemPoint!! >= redeemItem.point!!){
//                val intent = Intent(context, ProductRedeemOrderActivity::class.java)
//                intent.putExtra("redeem", redeemItem.id)
//                intent.putExtra("productId", redeemItem.productId)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context!!.startActivity(intent)
//            }
//            else{
//                Toast.makeText(context, "You don't have enough points", Toast.LENGTH_SHORT).show()
//            }
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