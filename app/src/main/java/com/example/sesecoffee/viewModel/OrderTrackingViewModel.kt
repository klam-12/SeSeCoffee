package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Order
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Format
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicInteger

class OrderTrackingViewModel (app: Application) : AndroidViewModel(
    app
) {

//    private val _onGoingOrderItems = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    private val _onGoingOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    private val _historyOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val onGoingOrder : StateFlow<Resource<List<Order>>> = _onGoingOrder
    val historyOrderItems : StateFlow<Resource<List<Order>>> = _historyOrder
    private val fbSingleton = FirebaseSingleton.getInstance()
    private val format: Format = Format()
    init {
//        fetchAllOnGoingOrderItems()
//        fetchAllHistoryOrderItems()
    }


    fun fetchAllOnGoingOrderItems(){
        viewModelScope.launch {
            _onGoingOrder.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("userId", UserSingleton.instance?.id)
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val itemsList = mutableListOf<OrderItem>()
                val ordersList = mutableListOf<Order>()

                if (querySnapshot.documents.isEmpty()) {
                    viewModelScope.launch {
                        _onGoingOrder.emit(Resource.Success(ordersList))
                    }
                } else {
                    val processedDocuments = AtomicInteger(0)

                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        val itemsCollection = document.reference.collection("OrderItem")

                        itemsCollection.get().addOnSuccessListener { subCollectionSnapshot ->
                            if (!subCollectionSnapshot.isEmpty) {
                                for (subDocument in subCollectionSnapshot.documents) {
                                    val item = subDocument.toObject(OrderItem::class.java)
                                    // Example: Assigning values to order fields based on item data
                                    order?.phoneNumber = item?.productName
                                    order?.userId = item?.quantity.toString()
                                    // You might need to adjust this logic based on your requirements
                                    order?.comment = subCollectionSnapshot.documents.size.toString()
                                    if (order?.paymentMethod != "Redeem") {
                                        order?.paymentMethod = order?.total?.let { format.formatToDollars(it) }
                                    } else {
                                        order?.paymentMethod = "${order?.total.toString()} pts"
                                    }
                                }
                            }

                            order?.let {
                                ordersList.add(it)
                            }

                            if (processedDocuments.incrementAndGet() == querySnapshot.documents.size) {
                                // Sort ordersList based on creation timestamp
                                ordersList.sortByDescending { it.createAt } // Assuming createAt is the field representing the creation timestamp
                                viewModelScope.launch {
                                    _onGoingOrder.emit(Resource.Success(ordersList))
                                }
                            }
                        }
                            .addOnFailureListener { exception ->
                                // Handle failure
                                println("Error fetching subcollection: $exception")
                                if (processedDocuments.incrementAndGet() == querySnapshot.documents.size) {
                                    viewModelScope.launch {
                                        _onGoingOrder.emit(Resource.Error(exception.message ?: "Unknown error"))
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println(exception)
                Toast.makeText(
                    getApplication(),
                    exception.toString(),
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _onGoingOrder.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
    fun fetchAllHistoryOrderItems(){
        viewModelScope.launch {
            _historyOrder.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", true)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val ordersList = mutableListOf<Order>()
                if (querySnapshot.documents.isEmpty())
                {
                    viewModelScope.launch {
                        _historyOrder.emit(Resource.Success(ordersList))
                    }
                } else {
                    val ordersList = mutableListOf<Order>()
                    val processedDocuments = AtomicInteger(0)
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        val itemsCollection = document.reference.collection("OrderItem")


                        itemsCollection.get()
                            .addOnSuccessListener { subCollectionSnapshot ->
                                if (!subCollectionSnapshot.isEmpty) {
                                    val subDocument = subCollectionSnapshot.documents[0]
                                    val item = subDocument.toObject(OrderItem::class.java)
                                    order?.phoneNumber = item?.productName
                                    order?.userId = item?.quantity.toString()
                                    order?.comment = subCollectionSnapshot.documents.size.toString()
                                    if (order?.paymentMethod != "Redeem"){
                                        order?.paymentMethod = order?.total?.let { format.formatToDollars(it) }
                                    }
                                    else order?.paymentMethod = "${order?.total.toString()} pts"


                                }

                                order?.let {
                                    ordersList.add(it)
                                }

                                if (processedDocuments.incrementAndGet() == querySnapshot.documents.size) {
                                    ordersList.sortByDescending { it.createAt }
                                    viewModelScope.launch {
                                        _historyOrder.emit(Resource.Success(ordersList))
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _historyOrder.emit(Resource.Error(exception.message.toString()))
                }
            }
    }



}