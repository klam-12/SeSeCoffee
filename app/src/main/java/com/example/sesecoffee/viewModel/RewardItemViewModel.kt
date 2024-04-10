package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RewardItemViewModel ( app: Application) : AndroidViewModel(
    app
) {
    private val _orderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    val orders : StateFlow<Resource<List<OrderItem>>> = _orderItems
    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllProducts()
    }

    fun fetchAllProducts()  {
        viewModelScope.launch { _orderItems.emit(Resource.Loading()) }

        fbSingleton.db.collection(Constant.ORDER_ITEM_COLLECTION).get()
            .addOnSuccessListener {
                    result ->
                val ordersList = result.toObjects(OrderItem::class.java)
                viewModelScope.launch {
                    _orderItems.emit(Resource.Success(ordersList))
                }

            }.addOnFailureListener() {
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _orderItems.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchAProduct(proName: String){

    }
}