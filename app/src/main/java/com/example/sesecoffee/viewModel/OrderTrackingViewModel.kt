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
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
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

    init {
//        fetchAllOnGoingOrderItems()
        fetchAllHistoryOrderItems()
    }


    fun fetchAllOnGoingOrderItems(){
        viewModelScope.launch {
            _onGoingOrder.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
//            .whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val itemsList = mutableListOf<OrderItem>()
                val ordersList = mutableListOf<Order>()
                if (querySnapshot.documents.isEmpty())
                {
                    viewModelScope.launch {
                        _onGoingOrder.emit(Resource.Success(ordersList))
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
                                    order?.rating = subCollectionSnapshot.documents.size
                                }

                                order?.let {
                                    ordersList.add(it)
                                }

                                if (processedDocuments.incrementAndGet() == querySnapshot.documents.size) {
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
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
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
//            .whereEqualTo("userId", UserSingleton.instance?.id.toString())
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", true)
            .get()
            .addOnSuccessListener { querySnapshot ->

//                val itemsList = mutableListOf<OrderItem>()
                val ordersList = mutableListOf<Order>()
                if (querySnapshot.documents.isEmpty())
                {
                    viewModelScope.launch {
                        _historyOrder.emit(Resource.Success(ordersList))
                    }
                } else {
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        val itemsCollection = document.reference.collection("OrderItem")


                        itemsCollection.get()
                            .addOnSuccessListener { subCollectionSnapshot ->
                                var subDocument = subCollectionSnapshot.documents[0]
                                val item = subDocument.toObject(OrderItem::class.java)
                                order?.phoneNumber = item?.productName;
                                order?.userId = item?.quantity.toString()
                                order?.rating = subCollectionSnapshot.documents.size


                            }
                        if (order != null) {
                            ordersList.add(order)
                        }
                        if (ordersList.size == querySnapshot.documents.size) {
                            viewModelScope.launch {
                                _historyOrder.emit(Resource.Success(ordersList))
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

    fun formatTimestamp(timestamp: Timestamp): String {
        val dateFormatter = SimpleDateFormat("dd MMMM YYYY") // "24 June"
        val timeFormatter = SimpleDateFormat("HH:mm")  // "12:30"

        val dateString = dateFormatter.format(timestamp.toDate())
        val timeString = timeFormatter.format(timestamp.toDate())

        return "$dateString | $timeString "
    }

    fun formatNumber(number: Int): String {
        val numberString = number.toString().reversed()

        val parts = mutableListOf<Char>()
        for (i in 0 until numberString.length) {
            parts.add(0, numberString[i])

            if ((i + 1) % 3 == 0 && i != numberString.lastIndex) {
                parts.add(0, ',')
            }
        }
        val result =  parts.joinToString("")
        println(result)
        return "$result VND"
    }


}