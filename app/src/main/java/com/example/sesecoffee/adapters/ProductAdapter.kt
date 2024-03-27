package com.example.sesecoffee.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.ProductItemBinding
import com.example.sesecoffee.model.Product

class ProductAdapter(val context: Context ,val itemList: List<Product>)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()
{
        lateinit var binding : ProductItemBinding
        inner class ProductViewHolder(var binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(product: Product){
                binding.product = product
                binding.apply {
                    Glide.with(itemView).load(product.imageUrl).into(proImg)
                }
            }
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductViewHolder {
            binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
            return ProductViewHolder(binding)
        }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = itemList[position]
            holder.bind(product)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}