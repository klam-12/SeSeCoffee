package com.example.sesecoffee.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.OrderDetailActivity
import com.example.sesecoffee.ProductUpdateOrderActivity
import com.example.sesecoffee.databinding.OrderDetailItemBinding
import com.example.sesecoffee.model.OrderItem


class OrderItemDetailAdapter(val context: Context):
    RecyclerView.Adapter<OrderItemDetailAdapter.OrderItemViewHolder>(){
    lateinit var binding : OrderDetailItemBinding
    private var onClickListener: OnClickListener? = null
    private val TAG = "OrderItemDetailAdapter"
    private val mContext: Context? = null
    inner class OrderItemViewHolder(var binding: OrderDetailItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
            binding.apply {
                Glide.with(itemView).load(orderItem.productImage).into(productImage)
            }
            binding.orderItemDetailCardView.setOnClickListener {
                val intent = Intent(context, ProductUpdateOrderActivity::class.java)
                intent.putExtra("OrderItems", orderItem.toString())
                context.startActivity(intent)
            }

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
    ): OrderItemDetailAdapter.OrderItemViewHolder {
        binding = OrderDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return OrderItemViewHolder(binding)
    }

    interface OnClickListener {
        fun onClick(position: Int, model: OrderItem)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.");

//        Glide.with(mContext)
//            .asBitmap()
//            .load(mImages.get(position))
//            .into(holder.image);

//        holder.imageName.setText(mImageNames.get(position));
        val orderItem =  differ.currentList[position]
        holder.bind(orderItem)
        holder.itemView.setOnClickListener (View.OnClickListener {
            Log.d(TAG, "onClick: clicked on: " + holder.itemView.toString())
            val intent: Intent = Intent(mContext, OrderDetailActivity::class.java)
//            intent.putExtra("image_url", mImages.get(position))
//            intent.putExtra("image_name", mImageNames.get(position))
            mContext?.startActivity(intent)
        })
//        holder.parentLayout.setOnClickListener
    }
}