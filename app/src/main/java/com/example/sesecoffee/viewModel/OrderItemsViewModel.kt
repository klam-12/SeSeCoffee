package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderItemsViewModel(private val fbSingleton: FirebaseSingleton, app: Application) : AndroidViewModel(
    app
) {

    private val _orderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    val orderItems : StateFlow<Resource<List<OrderItem>>> = _orderItems

    init {
        fetchAllOrderItems()
    }

    fun fetchAllOrderItems()  {
        viewModelScope.launch { _orderItems.emit(Resource.Loading()) }

        fbSingleton.db.collection(ORDER_ITEM_COLLECTION).get()
            .addOnSuccessListener {
                    result ->
                val orderItemList = result.toObjects(OrderItem::class.java)
                viewModelScope.launch {
                    _orderItems.emit(Resource.Success(orderItemList))
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

    fun addOrderItem(orderItem: OrderItem){
        fbSingleton.db.collection(ORDER_ITEM_COLLECTION).add(orderItem)
            .addOnSuccessListener {
                Toast.makeText(
                    getApplication(),
                    "Order item added successfully",
                    Toast.LENGTH_LONG
                ).show()
            }.addOnFailureListener() {
                Toast.makeText(
                    getApplication(),
                    "Errors happen when adding the order item",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun deleteOrderItem(orderItem: OrderItem){
        //Delete
    }
}