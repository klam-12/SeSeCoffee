package com.example.sesecoffee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.sesecoffee.enums.PaymentMethod
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PaymentActivity : AppCompatActivity() {
    private lateinit var userName : String
    private lateinit var userOrderId : String
    private lateinit var userAddress : String
    private var totalPrice = 0

    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val avatar = findViewById<ShapeableImageView>(R.id.paymentAvatar)
        val name = findViewById<TextView>(R.id.paymentName)
        val orderId = findViewById<TextView>(R.id.paymentOrderId)
        val address = findViewById<TextView>(R.id.paymentAddress)
        val price = findViewById<TextView>(R.id.paymentPrice)
        val paymentRadioGroup = findViewById<RadioGroup>(R.id.paymentMethodChoice)

        val query = collectionOrders.whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done",false)
        query.get()
            .addOnSuccessListener { documentSnapshot->
                if(!documentSnapshot.isEmpty){
                    var order = documentSnapshot.toObjects(Order::class.java)
                    userName = UserSingleton.instance?.fullName.toString()
                    userOrderId = order[0].id!!
                    userAddress = UserSingleton.instance?.address.toString()

                    collectionOrders.document(userOrderId).collection(ORDER_ITEM_COLLECTION).get()
                        .addOnSuccessListener { result ->
                            val orderItemList = result.toObjects(OrderItem::class.java)
                            totalPrice = calculateTotalPrice(orderItemList)
                            price.setText("${totalPrice}VNĐ")
                        }.addOnFailureListener { exception ->
                            println("Error getting documents: $exception")
                        }

                    name.setText(this.userName)
                    orderId.setText("Order ID: ${this.userOrderId}")
                    address.setText(this.userAddress)

                    findViewById<Button>(R.id.paymentProceedBtn).setOnClickListener {
                        val paymentMethodChoice = paymentRadioGroup.checkedRadioButtonId
                        if(paymentMethodChoice == -1) {
                            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val paymentMethod = when(resources.getResourceEntryName(paymentMethodChoice)) {
                            "cash" -> PaymentMethod.CASH.value
                            "creditCard" -> PaymentMethod.CARD.value
                            else -> PaymentMethod.MOMO.value
                        }

                        val paidOrder = Order(
                            userOrderId,
                            totalPrice,
                            Timestamp.now(),
                            userAddress,
                            UserSingleton.instance?.id.toString(),
                            "0123456789",
                            paymentMethod,
                            "",
                            true,
                        )
                        collectionOrders.document(userOrderId).set(paidOrder)
                        val intent = Intent(
                            applicationContext,
                            SuccessOrderActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                }

            }.addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        findViewById<ImageButton>(R.id.paymentBackBtn).setOnClickListener {
            finish()
        }
    }

    private fun calculateTotalPrice(itemList : List<OrderItem>) : Int {
        var price = 0
        itemList.forEach {
            price += it.price!!
        }
        return price
    }
}