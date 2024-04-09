package com.example.sesecoffee

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.OrderAdapter
import com.example.sesecoffee.adapters.RedeemAdapter
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.RedeemItemViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.flow.collectLatest

class RedeemActivity : AppCompatActivity() {
    lateinit var viewModel : RedeemItemViewModel
    lateinit var redeemAdapter: RedeemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_redeem)
        viewModel = RedeemItemViewModel(application)

        val redeemRecyclerView = findViewById<RecyclerView>(R.id.redeemList) as RecyclerView
        redeemRecyclerView.setHasFixedSize(true)
        redeemRecyclerView.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launchWhenStarted {
            viewModel.redeemItem.collectLatest {
                when(it){
                    is Resource.Success -> {
//                        price.setText("${calculateTotalPrice(it.data!!)}VNĐ")
                        print(it.data)
                        redeemAdapter.differ.submitList(it.data)
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
        redeemAdapter = RedeemAdapter(this)
        redeemRecyclerView.adapter = redeemAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                redeemAdapter.deleteItem(viewHolder.adapterPosition)
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
                            this@RedeemActivity,
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

        itemTouchHelper.attachToRecyclerView(redeemRecyclerView)

        findViewById<ImageButton>(R.id.redeemBackBtn).setOnClickListener {
            finish()
        }

    }





}