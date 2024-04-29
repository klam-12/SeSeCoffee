package com.example.sesecoffee

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.flow.collectLatest

class CartOrderActivity : AppCompatActivity() {
    lateinit var orderAdapter: OrderAdapter
    lateinit var idOrder : String
    lateinit var orderItemsViewModel: OrderItemsViewModel

    var isEmpty = true
    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)
        orderAdapter = OrderAdapter(this)
        orderItemsViewModel = OrderItemsViewModel(application)

        val price = findViewById<TextView>(R.id.cartPrice)
        val orderRecyclerView = findViewById<RecyclerView>(R.id.cartItemList) as RecyclerView
        orderRecyclerView.setHasFixedSize(true)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        val query = collectionOrders.whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done",false)
        query.get()
            .addOnSuccessListener { documentSnapshot->
                if(!documentSnapshot.isEmpty){
                    showLoading()

                    for(document in documentSnapshot){
                        idOrder = document.getString("id").toString()
                    }

                    orderItemsViewModel.fetchOrderItemByOrderId(idOrder)

                    lifecycleScope.launchWhenStarted {
                        orderItemsViewModel.orderItems.collectLatest {
                            when(it){
                                is Resource.Loading -> {
                                    showLoading()
                                }
                                is Resource.Success -> {
                                    hideLoading()

                                    if(it.data!!.isEmpty()){
                                        isEmpty = true
                                        findViewById<TextView>(R.id.cartEmpty).visibility = View.VISIBLE
                                        findViewById<ImageView>(R.id.cartEmptyImage).visibility = View.VISIBLE
                                    }
                                    else{
                                        isEmpty = false
                                        findViewById<TextView>(R.id.cartEmpty).visibility = View.GONE
                                        findViewById<ImageView>(R.id.cartEmptyImage).visibility = View.GONE
                                    }

                                    val orderItems = it.data.toMutableList()
                                    price.setText("${calculateTotalPrice(it.data)}$")

                                    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                                        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                                            return false
                                        }

                                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                            val position = viewHolder.adapterPosition
                                            orderAdapter.deleteItem(position, application, idOrder)
                                            orderAdapter.notifyItemRemoved(position)

                                            val deleteItem = orderItems[position]
                                            orderItems.remove(deleteItem)
                                            if(orderItems.isEmpty()){
                                                isEmpty = true
                                                price.setText("0$")
                                            }
                                            else{
                                                price.setText("${calculateTotalPrice(orderItems)}$")
                                            }
                                        }

                                        override fun onChildDraw(
                                            c: Canvas,
                                            recyclerView: RecyclerView,
                                            viewHolder: RecyclerView.ViewHolder,
                                            dX: Float,
                                            dY: Float,
                                            actionState: Int,
                                            isCurrentlyActive: Boolean
                                        ) {

                                            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                                .addBackgroundColor(
                                                    ContextCompat.getColor(
                                                        this@CartOrderActivity,
                                                        R.color.item_delete
                                                    )
                                                )
                                                .addActionIcon(R.drawable.delete)
                                                .addCornerRadius(1, 28)
                                                .create()
                                                .decorate()
                                            super.onChildDraw(
                                                c,
                                                recyclerView,
                                                viewHolder,
                                                dX,
                                                dY,
                                                actionState,
                                                isCurrentlyActive
                                            )
                                        }
                                    })

                                    orderAdapter.differ.submitList(it.data)
                                    orderRecyclerView.adapter = orderAdapter
                                    itemTouchHelper.attachToRecyclerView(orderRecyclerView)
                                }
                                is Resource.Error -> {
                                    hideLoading()
                                    Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                                }
                                else -> Unit
                            }
                        }
                    }
                }
                else{
                    findViewById<TextView>(R.id.cartEmpty).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.cartEmptyImage).visibility = View.VISIBLE
                    price.setText("0$")
                }

            }.addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        findViewById<ImageButton>(R.id.cartBackBtn).setOnClickListener {
            val intent = Intent(
                applicationContext,
                MainActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        findViewById<Button>(R.id.cartNextBtn).setOnClickListener {
            if(isEmpty){
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(
                applicationContext,
                PaymentActivity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun hideLoading() {
        findViewById<ProgressBar>(R.id.cartProgressBar).visibility = View.GONE
    }

    private fun showLoading() {
        findViewById<ProgressBar>(R.id.cartProgressBar).visibility = View.VISIBLE
    }

    private fun calculateTotalPrice(itemList : List<OrderItem>) : Int {
        var price = 0
        itemList.forEach {
            price += it.price!!
        }
        return price
    }
}