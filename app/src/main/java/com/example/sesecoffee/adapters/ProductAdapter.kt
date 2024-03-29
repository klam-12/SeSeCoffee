package com.example.sesecoffee.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.ProductItemBinding
import com.example.sesecoffee.fragments.AdminHomeFragmentDirections
import com.example.sesecoffee.model.Product


class ProductAdapter()
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()
{
    inner class ProductViewHolder(var itemBinding: ProductItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(product: Product){
            itemBinding.product = product
            itemBinding.apply {
                Glide.with(itemView).load(product.imageUrl).into(proImg)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.price == newItem.price
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductViewHolder {
            return ProductViewHolder(ProductItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
        }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//            val product = itemList[position]

        val currentProduct = differ.currentList[position]
        holder.bind(currentProduct)

        holder.itemView.setOnClickListener(){
            Toast.makeText(holder.itemView.context,"Item click",Toast.LENGTH_SHORT).show()

            // If normal user

            // If admin
//            val direction = AdminHomeFragmentDirections.actionAdminHomeToEditProductFragment(currentProduct)
//            it.findNavController().navigate(direction)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}