package com.example.sesecoffee.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrderDetailViewModel( app: Application) : AndroidViewModel(app){
    private val _orderItems = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderItems : StateFlow<Resource<List<Order>>> = _orderItems

    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllOrderDetail()
    }
    fun fetchAllOrderDetail(){

    }
}