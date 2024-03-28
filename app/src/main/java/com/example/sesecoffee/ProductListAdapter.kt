//package com.example.sesecoffee
//
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.example.sesecoffee.model.Product
//
//class ProductItem(
//    val name: String,
//    val preferences: String,
//    val quantity: Int,
//    val price: Int) {
//    companion object {
//        fun createProductItemList(productList: ArrayList<Product>): ArrayList<ProductItem> {
//            val productItemList = ArrayList<ProductItem>()
//            for (product in productList) {
//                productItemList.add(ProductItem(product.name, product.description, 1, product.price))
//            }
//            return productItemList
//        }
//    }
//}
//class ProductListAdapter (
//    private val productItemList : ArrayList<ProductItem>,
//    productList: ArrayList<Product>,
//    context: AppCompatActivity) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
//        private val mContext: AppCompatActivity
//        var products = ArrayList<Product>()
//        init {
//            mContext = context
//            products = productList
//        }
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val productName: TextView = itemView.findViewById(R.id.productName)
//        val productPreference: TextView = itemView.findViewById(R.id.productPreference)
//        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
//        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListAdapter.ViewHolder {
//        val context = parent.context
//        val inflater = LayoutInflater.from(context)
//        val productView = inflater.inflate(R.layout.order_item, parent, false)
//        return ViewHolder(productView)
//    }
//    override fun getItemCount(): Int {
//        return productItemList.size
//    }
//    override fun onBindViewHolder(holder: ProductListAdapter.ViewHolder, position: Int) {
//        val product: ProductItem = productItemList.get(position)
//        val name = holder.productName
//        val preferences = holder.productPreference
//        val quantity = holder.productQuantity
//        val price = holder.productPrice
//
//        name.setText(product.name)
//        preferences.setText(product.preferences)
//        quantity.setText(product.quantity)
//        price.setText(product.price)
//
//        holder.itemView.setOnClickListener{
//            Toast.makeText(mContext, "You clicked on ${product.name}", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
