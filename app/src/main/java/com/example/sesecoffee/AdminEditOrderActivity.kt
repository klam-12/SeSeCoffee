package com.example.sesecoffee

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest

class AdminEditOrderActivity : AppCompatActivity() {
    lateinit var approveBtn: Button
    lateinit var backBtn: Button
    lateinit var orderViewModel: OrderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_order)
        orderViewModel = OrderViewModel(application)
        val intent = intent
        val orderId = intent.getStringExtra("orderId")
        val position = intent.getIntExtra("position",0)

        approveBtn = findViewById(R.id.approveBtn)
        backBtn = findViewById(R.id.backBtn)

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


        approveBtn.setOnClickListener(){
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        orderViewModel.approveOrder(orderId,position)
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

        backBtn.setOnClickListener(){
            finish()
        }
    }
}