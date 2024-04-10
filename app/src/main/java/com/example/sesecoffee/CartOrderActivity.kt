package com.example.sesecoffee

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class CartOrderActivity : AppCompatActivity() {
    lateinit var orderAdapter: OrderAdapter
    lateinit var idOrder : String

    var db = FirebaseFirestore.getInstance()
    var collectionOrders: CollectionReference = db.collection(ORDER_COLLECTION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)
        orderAdapter = OrderAdapter(this)

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

                    collectionOrders.document(idOrder).collection(ORDER_ITEM_COLLECTION).get()
                        .addOnSuccessListener { result ->
                            val orderItemList = result.toObjects(OrderItem::class.java)
                            if(orderItemList.isEmpty()){
                                findViewById<TextView>(R.id.cartEmpty).visibility = View.VISIBLE
                            }
                            else{
                                findViewById<TextView>(R.id.cartEmpty).visibility = View.GONE
                            }

                            price.setText("${calculateTotalPrice(orderItemList)}VNĐ")
                            orderAdapter.differ.submitList(orderItemList)
                            orderRecyclerView.adapter = orderAdapter

                            hideLoading()

                            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                                    return false
                                }

                                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                    val position = viewHolder.adapterPosition
                                    orderAdapter.deleteItem(position, application, idOrder)
                                    orderAdapter.notifyItemRemoved(position);
                                    orderItemList.removeAt(position)
                                    price.setText("${calculateTotalPrice(orderItemList)}VNĐ")
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

                            itemTouchHelper.attachToRecyclerView(orderRecyclerView)
                        }.addOnFailureListener { exception ->
                            println("Error getting documents: $exception")
                        }
                    }
                else{
                    findViewById<TextView>(R.id.cartEmpty).visibility = View.VISIBLE
                    price.setText("0VNĐ")
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