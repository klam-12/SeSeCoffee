package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.model.Product
import com.google.firebase.Timestamp

class CartOrderActivity : AppCompatActivity() {
    val totalPrice = 10.0
//    var productList = ArrayList<Product>()
//    var productItemList = ArrayList<ProductItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)

//        productList.add(Product("Capuccino", 3, "capuccino.png", "Made out of high quality coffee beans",  Timestamp.now()))
//        productList.add(Product("Latte", 3, "latte.png", "Made out of high quality coffee beans", Timestamp.now()))
//        productList.add(Product("Mocha", 3, "mocha.png", "Made out of high quality coffee beans",  Timestamp.now()))

//        val cartItemList = findViewById<RecyclerView>(R.id.cartItemList) as RecyclerView
//        productItemList = ProductItem.createProductItemList(productList)
//        var listAdapter = ProductListAdapter(productItemList, productList, this)
//        cartItemList.adapter = listAdapter

        val price = findViewById<TextView>(R.id.cartPrice)

        price.setText("$${this.totalPrice}")

        findViewById<ImageButton>(R.id.cartBackBtn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.cartNextBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                PaymentActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}