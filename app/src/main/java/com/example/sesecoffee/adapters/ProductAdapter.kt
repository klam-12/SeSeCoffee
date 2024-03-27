package com.example.sesecoffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.databinding.ProductItemBinding
import com.example.sesecoffee.model.Product

class ProductAdapter(val context: Context ,val itemList: List<Product>)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()
{
        lateinit var binding : ProductItemBinding
        inner class ProductViewHolder(var binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(product: Product){
                binding.product = product
            }
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductViewHolder {
//                val view : View = LayoutInflater.from(context).inflate(R.layout.product_item,parent,false)
            binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
            return ProductViewHolder(binding)
        }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//            val product : Product = itemList.get(position)
//            val imageUrl : String = ""

            val product = itemList[position]
            holder.bind(product)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }


}