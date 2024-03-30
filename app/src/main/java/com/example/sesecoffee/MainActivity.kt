package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sesecoffee.model.Product

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val product = Product("Example Product")
        Log.i("hdLog","Product ID: ${product.id}")
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)

    }
}