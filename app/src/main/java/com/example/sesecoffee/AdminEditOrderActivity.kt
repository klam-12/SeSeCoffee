package com.example.sesecoffee

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.AdminOrderAdapter
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.databinding.ActivityAdminEditOrderBinding
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

class AdminEditOrderActivity : AppCompatActivity() {
    private var orderId: String? = null
    private var position: Int?=null
    lateinit var binding: ActivityAdminEditOrderBinding
    lateinit var orderViewModel: OrderViewModel
    lateinit var orderItemsViewModel: OrderItemsViewModel
    lateinit var orderAdapter: OrderAdapter

    private val db = FirebaseFirestore.getInstance()
    private val collectionOrders: CollectionReference = db.collection(Constant.ORDER_COLLECTION)
    private val collectionUser: CollectionReference = db.collection("USER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_admin_edit_order)
        binding = ActivityAdminEditOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = OrderViewModel(application)
        orderItemsViewModel = OrderItemsViewModel(application)

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
                        orderViewModel.approveOrder(orderId,position!!)
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

    override fun onResume() {
        super.onResume()
        val intent = intent
        orderId = intent.getStringExtra("orderId")
        position = intent.getIntExtra("position", 0)
        orderId?.let {
            getOrderDetails(orderId)
            setUpRecyclerViewOrder()
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

                        // Display appropriate image based on delivery status
//                        if (delivered == true) {
//                            Glide.with(this).load(R.drawable.order_success).into(binding.imageView)
//                        } else {
//                            Glide.with(this).load(R.drawable.order_pending).into(binding.imageView)
//                        }
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
                        binding.totalBill.text = "$ ${total}"


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
//                                val recyclerView: RecyclerView = binding.rvlistOrderItem
//                                val itemOffsetDecoration = ItemOffsetDecoration(16) // Set your desired gap here
//                                recyclerView.addItemDecoration(itemOffsetDecoration)
//
//                                val layoutManager = LinearLayoutManager(this)
//                                recyclerView.layoutManager = layoutManager
//                                val adapter = OrderDetailItemAdapter(orderItemsList)
//                                recyclerView.adapter = adapter
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

    private fun setUpRecyclerViewOrder() {
        orderAdapter = OrderAdapter(this)
        binding.rvlistOrderItem.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL ,false)
            adapter = orderAdapter
        }
    }


}