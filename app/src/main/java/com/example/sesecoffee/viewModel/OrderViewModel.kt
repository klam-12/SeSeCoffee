package com.example.sesecoffee.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Constant.ORDER_COLLECTION
import com.example.sesecoffee.utils.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(app: Application) : AndroidViewModel(
    app
) {
    private var ordersList: MutableList<Order>?=null
    private val fbSingleton = FirebaseSingleton.getInstance()
    private val _order = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()

    fun fetchAllOrders()  {
        Toast.makeText(getApplication(),"Fetch all products", Toast.LENGTH_LONG).show()

        viewModelScope.launch { _order.emit(Resource.Loading()) }

        fbSingleton.db.collection("Orders").get()
            .addOnSuccessListener {
                    result ->
                ordersList = result.toObjects(Order::class.java)
                viewModelScope.launch {
                    _order.emit(Resource.Success(ordersList))
                }
            }.addOnFailureListener() {
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _order.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun addOrders(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }
        val collectionReference: CollectionReference = fbSingleton.db.collection(ORDER_COLLECTION)

        // Uploading the image

        collectionReference.document(order.id!!).set(order)
            .addOnSuccessListener {
                ordersList?.add(order)
                viewModelScope.launch {
                    _order.emit(Resource.Success(ordersList))
                }
            }
            .addOnFailureListener() {
                viewModelScope.launch {
                    Log.i("R","sai roi")
                    _order.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateOrder(newOrder: Order, orderId : String) {
        val documentRef = fbSingleton.db.collection(ORDER_COLLECTION).document(orderId)
        fbSingleton.db.runTransaction{transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                documentRef.set(newOrder)
            }

        } .addOnSuccessListener {
            viewModelScope.launch {
                _updateOrder.emit(Resource.Success(newOrder))
            }

        } .addOnFailureListener{
            viewModelScope.launch {
                _updateOrder.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}