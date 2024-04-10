package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RedeemPaymentActivity : AppCompatActivity() {
    val productId = "5eecd988-9d08-495a-9c03-c8fe50d0b51d"

    val db = FirebaseFirestore.getInstance()
    val collectionProducts: CollectionReference = db.collection(PRODUCT_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_payment)

        val avatar = findViewById<ShapeableImageView>(R.id.redeemPaymentAvatar)
        val name = findViewById<TextView>(R.id.redeemPaymentName)
        val phoneNumber = findViewById<TextView>(R.id.redeemPaymentPhone)
        val address = findViewById<TextView>(R.id.redeemPaymentAddress)
        val price = findViewById<TextView>(R.id.redeemPaymentPrice)

        val productImg = findViewById<ImageView>(R.id.redeemProductImage)
        val productName = findViewById<TextView>(R.id.redeemProductName)
        val productPrice = findViewById<TextView>(R.id.redeemProductPrice)

        name.setText(UserSingleton.instance?.fullName)
        address.setText(UserSingleton.instance?.address)

        collectionProducts.document(productId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val product = documentSnapshot.toObject(Product::class.java)

                    Glide.with(this).load(product?.imageUrl).into(productImg)
                    productName.setText(product?.name)
                    productPrice.setText("${product?.price} VNĐ")

                    val redeem = product?.price?.div(1000)
                    price.setText("${redeem} points")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        findViewById<ImageButton>(R.id.redeemPaymentBackBtn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.redeemPaymentProceedBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                SuccessOrderActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }
}