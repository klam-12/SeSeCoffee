package com.example.sesecoffee.viewModel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.MainActivity
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(app: Application) : AndroidViewModel(
    app
) {
    private var ordersList: MutableList<Order>?=null
    private var personalOrderList : MutableList<Order>?=null
    private val fbSingleton = FirebaseSingleton.getInstance()
    private val _order = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()

    private val _personalOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val personalOrder = _personalOrder.asStateFlow()

    // Receive Errors
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun fetchAllOrders()  {
//        Toast.makeText(getApplication(),"Fetch all products", Toast.LENGTH_LONG).show()

        viewModelScope.launch { _order.emit(Resource.Loading()) }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("done",true)
            .get()
            .addOnSuccessListener {
                    result ->
                ordersList = result.toObjects(Order::class.java)
                if (ordersList != null){
                    ordersList!!.sortByDescending { it.createAt }
                }
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

    fun fetchPersonalOrder(userId : String){
//        Log.i("KL",userId)
        viewModelScope.launch { _personalOrder.emit(Resource.Loading()) }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("done",true)
            .whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener {
                    result ->
                personalOrderList = result.toObjects(Order::class.java)
                viewModelScope.launch {
                    _personalOrder.emit(Resource.Success(personalOrderList))
                }
            }.addOnFailureListener() {
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
                    Toast.LENGTH_LONG
                ).show()
                Log.i("KL",it.message.toString())
                viewModelScope.launch {
                    _personalOrder.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun addOrders(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }
        val collectionReference: CollectionReference = fbSingleton.db.collection(Constant.ORDER_COLLECTION)

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

    fun approveOrder(id: String?,position: Int,userId: String,total: Int,payment: String){
        viewModelScope.launch {
            _updateOrder.emit(Resource.Loading())
            _order.emit(Resource.Loading())
        }
        val collectionUser = fbSingleton.db.collection(Constant.USER_COLLECTION)
        Log.i("KL",ordersList?.get(position).toString())

        val documentRef = fbSingleton.db.collection(Constant.ORDER_COLLECTION).document(id!!)
        fbSingleton.db.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                transaction.update(documentRef, "delivered", true)
                editRedeemForUser(userId,total,payment)
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

    private fun editRedeemForUser(userId: String, total: Int,payment:String) {
        fbSingleton.db.collection(Constant.USER_COLLECTION).document(userId)
            .get()
            .addOnSuccessListener { document ->
                val oldRedeemPoint = document.getLong("redeemPoint")?.toInt() ?: 0
                var redeemPoint = 0;
                if(payment == "Redeem"){
                    redeemPoint = oldRedeemPoint.toInt() - total
                } else{
                    redeemPoint = oldRedeemPoint.toInt() + total
                }
                UserSingleton.instance?.redeemPoint = redeemPoint

                // Update the redeemPoint in the database after the value has been retrieved and updated
                fbSingleton.db.collection(Constant.USER_COLLECTION).document(userId)
                    .update("redeemPoint", redeemPoint)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->
                        Log.e("Err", "Error updating redeemPoint: $e")
                    }
            }
            .addOnFailureListener { e ->
                // Handle any errors that may occur while fetching the document
                // You may want to add logging or further handling here
                Log.e("Err", "Error fetching document: $e")
            }
    }


    fun findOrderById(id: String, callback: (Order?) -> Unit) {
        fbSingleton.db.collection(Constant.ORDER_COLLECTION).document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val order = document.toObject(Order::class.java)
                    callback(order)
                } else {
                    Log.d("Order", "No such document")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Order", "Get failed with ", exception)
                callback(null)
            }
    }


    fun updateOrder(newOrder: Order, orderId : String) {
        val documentRef = fbSingleton.db.collection(Constant.ORDER_COLLECTION).document(orderId)
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