package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.sesecoffee.model.UserSingleton

class SuccessOrderActivity : AppCompatActivity() {
    lateinit var orderId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_order)

        val intent = intent
        orderId = intent.getStringExtra("orderId")!!

        findViewById<Button>(R.id.successRateBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                RatingActivity::class.java
            )
            intent.putExtra("orderIdRating", orderId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<Button>(R.id.successHomeBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                MainActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}