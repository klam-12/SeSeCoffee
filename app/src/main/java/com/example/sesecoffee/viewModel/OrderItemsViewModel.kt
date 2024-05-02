package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Constant.ORDER_ITEM_COLLECTION
import com.example.sesecoffee.utils.Resource
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderItemsViewModel(app: Application) : AndroidViewModel(
    app
) {
    private val _orderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    val orderItems : StateFlow<Resource<List<OrderItem>>> = _orderItems

    private val _originalOrderItem = MutableStateFlow<Resource<OrderItem>>(Resource.Unspecified())
    val originalOrderItem = _originalOrderItem.asStateFlow()

    private val _updateOrderItem = MutableStateFlow<Resource<OrderItem>>(Resource.Unspecified())
    val updateOrderItem = _updateOrderItem.asStateFlow()

    private var orderItemList : MutableList<OrderItem>? = null
    private val fbSingleton = FirebaseSingleton.getInstance()
    var collectionOrders : CollectionReference = fbSingleton.db.collection("Orders")
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

    fun fetchOrderItemByOrderId(orderId: String) {
        println(orderId)
        viewModelScope.launch { _orderItems.emit(Resource.Loading()) }
        collectionOrders.document(orderId).collection(ORDER_ITEM_COLLECTION).get()
            .addOnSuccessListener {
                    result ->

                orderItemList = result.toObjects(OrderItem::class.java)
                println("orderList $orderItemList")
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

    fun addOrderItem(orderItem : OrderItem, orderId : String){
        viewModelScope.launch { _orderItems.emit(Resource.Loading()) }
        val subCollectionRef = collectionOrders.document(orderId).collection("OrderItem")
        subCollectionRef.document(orderItem.id!!).set(orderItem)
            .addOnSuccessListener {
                Toast.makeText(
                    getApplication(),
                    "Add to cart successfully",
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

    fun updateOrderItemInfo(newOrderItem: OrderItem, orderId : String) {
        val subCollectionRef = collectionOrders.document(orderId).collection("OrderItem")
        val documentRef = subCollectionRef.document(newOrderItem.id!!)
        fbSingleton.db.runTransaction{transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                documentRef.set(newOrderItem)
            }

        } .addOnSuccessListener {
            viewModelScope.launch {
                _updateOrderItem.emit(Resource.Success(newOrderItem))
            }

        } .addOnFailureListener{
            viewModelScope.launch {
                _updateOrderItem.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun deleteOrderItem(orderItemId : String, orderId : String){
        val subCollectionRef = collectionOrders.document(orderId).collection("OrderItem")
        viewModelScope.launch {
            subCollectionRef.document(orderItemId).delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _updateOrderItem.emit(Resource.Success(null))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _updateOrderItem.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}