package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView

class PaymentActivity : AppCompatActivity() {
    private val name = "Trần Minh Anh"
    private val orderId = "123456"
    private val address = "21 Trương Công Định, phường 14, Tân Bình, Thành phố Hồ Chí Minh"
    private val totalPrice = 9.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val avatar = findViewById<ShapeableImageView>(R.id.paymentAvatar)
        val name = findViewById<TextView>(R.id.paymentName)
        val orderId = findViewById<TextView>(R.id.paymentOrderId)
        val address = findViewById<TextView>(R.id.paymentAddress)
        val price = findViewById<TextView>(R.id.paymentPrice)

        name.setText(this.name)
        orderId.setText("Order ID: ${this.orderId}")
        address.setText(this.address)
        price.setText("$${this.totalPrice}")

        findViewById<ImageButton>(R.id.paymentBackBtn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.paymentProceedBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                SuccessOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}