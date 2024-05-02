package com.example.sesecoffee

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.OrderDetailBinding
import com.example.sesecoffee.databinding.OrderDetailItemBinding
import com.example.sesecoffee.fragments.OrderDetailItemAdapter
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format
import com.example.sesecoffee.utils.ItemOffsetDecoration
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: OrderDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val collectionOrders: CollectionReference = db.collection(Constant.ORDER_COLLECTION)
    private val collectionUser: CollectionReference = db.collection("USER")
//    lateinit var viewModel:Order
    private val format: Format = Format()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.order_detail)
//        viewModel = OrderItemsViewModel(application)

        val intent = intent
        val orderId = intent.getStringExtra("order_id")

        binding.textView6.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order, 0, 0, 0)

        getOrderDetails(orderId)
    }

    private fun getOrderDetails(orderId: String?) {
        try {

            collectionOrders
                .document(orderId.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val createAt = documentSnapshot.getTimestamp("createAt")
                        binding.createdAt.text = createAt?.let { format.timestampToFormattedString(it) }
                        val delivered = documentSnapshot.getBoolean("delivered")

                        // Display appropriate image based on delivery status
                        if (delivered == true) {
                            Glide.with(this).load(R.drawable.order_success).into(binding.imageView)
                        } else {
                            Glide.with(this).load(R.drawable.order_pending).into(binding.imageView)
                        }
                        var fullName = "Not found"
                        val userId = documentSnapshot.getString("userId").toString()
                        collectionUser.document(userId)
                            .get()
                            .addOnSuccessListener { userDocument ->
                                if (userDocument.exists()) {
                                    fullName = userDocument.getString("fullname").toString()
                                    println("Fullname: $fullName")

                                    binding.fullNameOrder.text = "FullName: $fullName"
                                } else {
                                    println("User document does not exist")
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Error getting user document: $exception")
                            }

                        val address = documentSnapshot.getString("address")
                        binding.address.text = "Address: $address"
                        val phoneNumber = documentSnapshot.getString("phoneNumber")
                        binding.phoneNumber.text = "Phone Number: $phoneNumber"
                        val paymentMethod = documentSnapshot.getString("paymentMethod")
                        binding.paymentMethod.text = paymentMethod
                        val total = documentSnapshot.getLong("total")?.toString()?.toInt()
                        binding.price.text = total?.let { format.formatToDollars(it) }


                        // Retrieve order items from subcollection
                        val orderItemsCollection = documentSnapshot.reference.collection("OrderItem")
                        orderItemsCollection.get()
                            .addOnSuccessListener { orderItemsSnapshot ->
                                val orderItemsList = mutableListOf<OrderItem>()
                                for (orderItemDocument in orderItemsSnapshot.documents) {
                                    val orderItem = orderItemDocument.toObject(OrderItem::class.java)
                                    orderItem?.let {
                                        orderItemsList.add(it)
                                    }
                                }
                                val recyclerView: RecyclerView = binding.listOrderItem
                                val itemOffsetDecoration = ItemOffsetDecoration(25) // Set your desired gap here
                                recyclerView.addItemDecoration(itemOffsetDecoration)

                                val layoutManager = LinearLayoutManager(this)
                                recyclerView.layoutManager = layoutManager
                                val adapter = OrderDetailItemAdapter(orderItemsList)
                                recyclerView.adapter = adapter
                            }
                            .addOnFailureListener { exception ->
                                println("Error getting order items: $exception")
                            }
                    }
                }


        } catch (t: Throwable) {
            Log.i("Err", "$t")
        }
    }

}




