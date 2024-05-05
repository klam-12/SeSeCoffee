package com.example.sesecoffee

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
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
            viewModel.fetchAllRedeemWithValidDate()
            viewModel.redeems.collectLatest {
                when(it){
                    is Resource.Success -> {
//                        price.setText("${calculateTotalPrice(it.data!!)}VNĐ")
                        if(it.data?.isEmpty() == true) {
                            Toast.makeText(
                                applicationContext,
                                "No redeem yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else
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
        findViewById<ImageButton>(R.id.redeemBackBtn).setOnClickListener {
            finish()
        }

    }
}