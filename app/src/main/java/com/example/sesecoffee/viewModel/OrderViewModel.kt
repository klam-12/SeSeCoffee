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
import com.example.sesecoffee.model.User
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Resource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(app: Application) : AndroidViewModel(
    app
) {
    private var ordersList: MutableList<Order>?=null
    private var usersList : MutableList<User>?=null
    private val fbSingleton = FirebaseSingleton.getInstance()
    private val _order = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()

    // Receive Errors
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun fetchAllOrders()  {
//        Toast.makeText(getApplication(),"Fetch all products", Toast.LENGTH_LONG).show()

        viewModelScope.launch { _order.emit(Resource.Loading()) }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .orderBy("createAt", Query.Direction.DESCENDING)
            .get()
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
        val collectionReference: CollectionReference = fbSingleton.db.collection("Orders")

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

    fun approveOrder(id: String?,position: Int){
        viewModelScope.launch {
            _updateOrder.emit(Resource.Loading())
            _order.emit(Resource.Loading())
        }

        val documentRef = fbSingleton.db.collection(Constant.ORDER_COLLECTION).document(id!!)
        fbSingleton.db.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                transaction.update(documentRef, "delivered", true)
            }
        } .addOnSuccessListener {

            viewModelScope.launch {
                ordersList?.get(position)?.delivered = true
                _updateOrder.emit(Resource.Success(ordersList?.get(position)))
                _order.emit(Resource.Success(ordersList))
            }
        } .addOnFailureListener{
            viewModelScope.launch {
                _updateOrder.emit(Resource.Error(it.message.toString()))
                _order.emit(Resource.Error(it.message.toString()))
            }
        }

    }

}