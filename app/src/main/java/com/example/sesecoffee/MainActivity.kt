package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.sesecoffee.model.Product

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val product = Product("Example Product")
//        Log.i("hdLog","Product ID: ${product.id}")

        findViewById<Button>(R.id.order).setOnClickListener {
            val intent = Intent(
                applicationContext,
                ProductOrderActivity::class.java
            )
            startActivity(intent)
        }
    }
}