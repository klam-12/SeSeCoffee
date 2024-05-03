package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sesecoffee.adapters.AdminRatingAdapter
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.OrderViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.core.View
import kotlinx.coroutines.flow.collectLatest

class PersonalRatings : AppCompatActivity() {
    lateinit var orderViewModel: OrderViewModel
    lateinit var ratingAdapter: AdminRatingAdapter

    lateinit var backBtn: FloatingActionButton
    lateinit var rvPRatings : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_ratings)
        orderViewModel = OrderViewModel(application)
        backBtn = findViewById(R.id.backBtnPRating)
        rvPRatings = findViewById(R.id.rvPRatings)

        val userId = UserSingleton.instance?.id
        setUpRecyclerViewRating()

        lifecycleScope.launchWhenResumed {
            if (userId != null) {
                orderViewModel.fetchPersonalOrder(userId)
            }
            orderViewModel.personalOrder.collectLatest {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        val listData = it.data

                        // filter data with rating != 0
                        val filteredListData = listData?.filter { it.rating != 0F }
                        ratingAdapter.differ.submitList(filteredListData)

                        if (filteredListData != null) {
                            if(filteredListData.isEmpty()){
                                findViewById<ImageView>(R.id.imageNoti).visibility = android.view.View.VISIBLE
                            }
                        }

                    }
                    is Resource.Error -> {

                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        backBtn.setOnClickListener(){
            finish()
        }

    }

    private fun setUpRecyclerViewRating() {
        val context = this
        ratingAdapter = AdminRatingAdapter()
        rvPRatings.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
            adapter = ratingAdapter
        }
    }
}