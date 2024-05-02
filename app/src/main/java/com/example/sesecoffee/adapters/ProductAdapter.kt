package com.example.sesecoffee.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.ProductOrderActivity
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.EditProductActivity
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.ActivityAdminMainBinding
import com.example.sesecoffee.databinding.ProductItemBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Format
import java.util.Random

class ProductAdapter()
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()
{
    inner class ProductViewHolder(var itemBinding: ProductItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
        var format : Format = Format()
        fun bind(product: Product){
            itemBinding.product = product
            itemBinding.proPrice.text = product.price?.let { format.formatToDollars(it) }
            itemBinding.apply {
                Glide.with(itemView).load(product.imageUrl).into(proImg)
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
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
            // If normal user
            if(UserSingleton.instance?.isAdmin == 0){
                val intent = Intent(it.context,ProductOrderActivity::class.java)
                intent.putExtra("productId",currentProduct.id.toString())
                it.context.startActivity(intent)
            } else{
                // If admin
                val intent = Intent(it.context,EditProductActivity::class.java)
                intent.putExtra("product",currentProduct)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}