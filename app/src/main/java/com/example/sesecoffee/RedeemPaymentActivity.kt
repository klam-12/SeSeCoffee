package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sesecoffee.enums.PaymentMethod
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.example.sesecoffee.utils.Constant.REDEEM_COLLECTION
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RedeemPaymentActivity : AppCompatActivity() {
    private lateinit var userName : String
    private lateinit var userPhone : String
    private lateinit var userAddress : String

    val db = FirebaseFirestore.getInstance()
    val collectionProducts: CollectionReference = db.collection(PRODUCT_COLLECTION)
    val collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)
    val collectionRedeem: CollectionReference = db.collection(REDEEM_COLLECTION)
    val collectionUser: CollectionReference = db.collection("USER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_payment)

        val intent = intent
        val redeemId = intent.getStringExtra("redeem")
        val productId = intent.getStringExtra("productId")
        val orderId = intent.getStringExtra("orderId")

        Toast.makeText(this, "Redeem: $redeemId, Product: $productId, Order: $orderId", Toast.LENGTH_SHORT).show()

        val avatar = findViewById<ShapeableImageView>(R.id.redeemPaymentAvatar)
        val name = findViewById<TextView>(R.id.redeemPaymentName)
        val phone = findViewById<TextView>(R.id.redeemPaymentPhone)
        val address = findViewById<TextView>(R.id.redeemPaymentAddress)
        val price = findViewById<TextView>(R.id.redeemPaymentPrice)

        val productImg = findViewById<ImageView>(R.id.redeemProductImage)
        val productName = findViewById<TextView>(R.id.redeemProductName)
        val productPrice = findViewById<TextView>(R.id.redeemProductPrice)

        collectionProducts.document(productId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val product = documentSnapshot.toObject(Product::class.java)
                    productName.setText(product!!.name)
                    Glide.with(this).load(product.imageUrl).into(productImg)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        collectionRedeem.document(redeemId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val redeem = documentSnapshot.toObject(Redeem::class.java)
                    productPrice.setText("${redeem?.point} points")
                    price.setText("${redeem?.point} points")

                    findViewById<Button>(R.id.redeemPaymentProceedBtn).setOnClickListener {
                        val paidOrder = Order(
                            orderId,
                            redeem?.point!!,
                            Timestamp.now(),
                            userAddress,
                            UserSingleton.instance?.id.toString(),
                            userPhone,
                            PaymentMethod.REDEEM.value,
                            "",
                            true,
                            false,
                            0,
                            ""
                        )
                        collectionOrders.document(orderId!!).set(paidOrder)
                        val intent = Intent(
                            applicationContext,
                            SuccessOrderActivity::class.java
                        )
                        intent.putExtra("orderId", orderId)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)

//                        val documentRef = collectionUser.document(UserSingleton.instance?.id.toString())
//                        val updates = hashMapOf(
//                            "redeemPoint " to (UserSingleton.instance?.redeemPoint!! - redeem.point!!)
//                        )
//                        documentRef.update(updates as Map<String, Any>)
//                            .addOnSuccessListener {
//                                val intent = Intent(
//                                    applicationContext,
//                                    SuccessOrderActivity::class.java
//                                )
//                                intent.putExtra("orderId", orderId)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                startActivity(intent)
//                            }
//                            .addOnFailureListener { exception ->
//                                Log.i("R","sai roi")
//                                println("Error updating fields: $exception")
//                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        userName = UserSingleton.instance?.fullName.toString()
        userPhone = UserSingleton.instance?.phone.toString()
        userAddress = UserSingleton.instance?.address.toString()

        name.setText(userName)
        phone.setText("Phone: $userPhone")
        address.setText("Address: $userAddress")

        findViewById<ImageButton>(R.id.redeemPaymentBackBtn).setOnClickListener {
            collectionOrders.document(orderId!!).delete()
            finish()
        }
    }
}