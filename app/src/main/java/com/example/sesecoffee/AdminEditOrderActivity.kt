package com.example.sesecoffee

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sesecoffee.adapters.AdminOrderAdapter
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.databinding.ActivityAdminEditOrderBinding
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.fragments.OrderDetailItemAdapter
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format
import com.example.sesecoffee.utils.ItemOffsetDecoration
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

class AdminEditOrderActivity : AppCompatActivity() {
    private var orderId: String? = null
    private var position: Int?=null
    private var userId : String = ""
    private var total : Int = 0
    private var payment: String= ""

    lateinit var binding: ActivityAdminEditOrderBinding
    lateinit var orderViewModel: OrderViewModel
    lateinit var orderItemsViewModel: OrderItemsViewModel
    lateinit var orderAdapter: OrderAdapter

    private val db = FirebaseFirestore.getInstance()
    private val collectionOrders: CollectionReference = db.collection(Constant.ORDER_COLLECTION)
    private val collectionUser: CollectionReference = db.collection("USER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = OrderViewModel(application)
        orderItemsViewModel = OrderItemsViewModel(application)

        val intent = intent
        orderId = intent.getStringExtra("orderId")
        position = intent.getIntExtra("position", 0)
        orderId?.let {
            orderAdapter = OrderAdapter(this)
            getOrderDetails(orderId)

        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.updateOrder.collectLatest {
                when(it){
                    is Resource.Loading -> {
//                        showLoading()
                    }
                    is Resource.Success -> {
//                        hideLoading()
                        finish()
                    }
                    is Resource.Error -> {
//                        hideLoading()
                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        binding.approveBtn.setOnClickListener(){
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        orderViewModel.approveOrder(orderId,position!!,userId,total,payment)
                    })
                    setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->

                    })
                    // Set other dialog properties
                    setIcon(R.drawable.ic_warning_yellow)
                    setTitle("Do you want to approve this order?")
                }
                // Create the AlertDialog
                builder.create()
            }

            if (alertDialog != null) {
                alertDialog!!.show()
            }
        }

        binding.backBtn.setOnClickListener(){
            finish()
        }
    }

    private fun getOrderDetails(orderId: String?) {
        val format = Format()
        try {
            collectionOrders
                .document(orderId.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val createAt = documentSnapshot.getTimestamp("createAt")
                        binding.createdAt.text = createAt?.let { format.timestampToFormattedString(createAt) }
                        val delivered = documentSnapshot.getBoolean("delivered")

//                         Display appropriate image based on delivery status
                        if (delivered == true) {
                            Glide.with(this).load(R.drawable.order_success).into(binding.imageView)
                            binding.approveBtn.visibility = View.GONE
                        }
                        else{
                            Glide.with(this).load(R.drawable.order_pending).into(binding.imageView)
                        }
                        var fullName = "Not found"
                        userId = documentSnapshot.getString("userId").toString()
                        collectionUser.document(userId!!)
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
                        payment = documentSnapshot.getString("paymentMethod")!!
                        binding.paymentMethod.text = payment
                        total = documentSnapshot.getLong("total")!!.toInt()
                        binding.totalBill.text = total?.let { format.formatToDollars(it) }
                        if (payment != "Redeem") {
                            binding.totalBill.text = total?.let { format.formatToDollars(it) }
                        } else {
                            binding.totalBill.text = "${total} pts"
                        }


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
                                val recyclerView: RecyclerView = binding.rvlistOrderItem
                                val itemOffsetDecoration = ItemOffsetDecoration(16) // Set your desired gap here
                                recyclerView.addItemDecoration(itemOffsetDecoration)

                                val layoutManager = LinearLayoutManager(this)
                                recyclerView.layoutManager = layoutManager
                                val adapter = OrderDetailItemAdapter(orderItemsList)
                                recyclerView.adapter = adapter
                                orderAdapter.differ.submitList(orderItemsList)
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