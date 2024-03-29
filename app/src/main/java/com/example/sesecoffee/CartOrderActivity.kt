package com.example.sesecoffee

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.enums.HotCold
import com.example.sesecoffee.enums.Milk
import com.example.sesecoffee.enums.Size
import com.example.sesecoffee.model.OrderItem
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class CartOrderActivity : AppCompatActivity() {
    lateinit var orderAdapter: OrderAdapter
    var orderItemList : MutableList<OrderItem> = mutableListOf()
    val totalPrice = 10.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)

        orderItemList.add(OrderItem("", "", "Capuccino", HotCold.HOT, Size.SMALL, Milk.NOMILK, 2, 30000, false))
        orderItemList.add(OrderItem("", "", "Latte", HotCold.COLD, Size.MEDIUM, Milk.SMALLMILK, 1, 20000, false))
        orderItemList.add(OrderItem("", "", "Mocha", HotCold.HOT, Size.LARGE, Milk.LARGEMILK, 1, 25000, false))
        orderItemList.add(OrderItem("", "", "Americano", HotCold.COLD, Size.SMALL, Milk.NOMILK, 1, 15000, false))
        orderItemList.add(OrderItem("", "", "Espresso", HotCold.HOT, Size.MEDIUM, Milk.SMALLMILK, 1, 10000, false))

        val orderRecyclerView = findViewById<RecyclerView>(R.id.cartItemList) as RecyclerView
        orderRecyclerView.setHasFixedSize(true)
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        orderAdapter = OrderAdapter(this, orderItemList)
        orderRecyclerView.adapter = orderAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                orderAdapter.deleteItem(viewHolder.adapterPosition)
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

        val price = findViewById<TextView>(R.id.cartPrice)
        price.setText("$${this.totalPrice}")

        findViewById<ImageButton>(R.id.cartBackBtn).setOnClickListener {
            finish()
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



    private fun setUpRecyclerViewProducts(){
        if(orderItemList.isNotEmpty()){
            orderItemList.clear()
        }


//        orderAdapter.notifyDataSetChanged()
    }
}