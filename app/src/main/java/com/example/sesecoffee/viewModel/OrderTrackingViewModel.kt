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
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class OrderTrackingViewModel (app: Application) : AndroidViewModel(
    app
) {

    private val _onGoingOrderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    private val _historyOrderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    val onGoingOrderItems : StateFlow<Resource<List<OrderItem>>> = _onGoingOrderItems
    val historyOrderItems : StateFlow<Resource<List<OrderItem>>> = _historyOrderItems
    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllOnGoingOrderItems()
        fetchAllHistoryOrderItems()
    }


    fun fetchAllOnGoingOrderItems(){
        viewModelScope.launch {
            _onGoingOrderItems.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ordersList = mutableListOf<Order>()
                val itemsList = mutableListOf<OrderItem>()


                for (document in querySnapshot.documents) {
                    val order = document.toObject(Order::class.java)
                    val itemsCollection = document.reference.collection("OrderItem")

                    itemsCollection.get()
                        .addOnSuccessListener { subCollectionSnapshot ->
                            for (subDocument in subCollectionSnapshot.documents) {
                                val item = subDocument.toObject(OrderItem::class.java)

                                item?.productId = order?.createAt?.let {
                                    formatTimestamp(
                                        it
                                    )
                                }
                                val price = item?.price?.times(item?.quantity!!)
                                item?.productImage = price?.let { formatNumber(it) }

                                item?.let {
                                    itemsList.add(it)
                                }
//                                println(item.toString())
                            }

                            order?.let {
                                ordersList.add(it)
                            }

                            if (ordersList.size == querySnapshot.documents.size) {
                                viewModelScope.launch {
                                    _onGoingOrderItems.emit(Resource.Success(itemsList))
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
                    _onGoingOrderItems.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
    fun fetchAllHistoryOrderItems(){
        viewModelScope.launch {
            _historyOrderItems.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ordersList = mutableListOf<Order>()
                val itemsList = mutableListOf<OrderItem>()

                for (document in querySnapshot.documents) {
                    val order = document.toObject(Order::class.java)
                    val itemsCollection = document.reference.collection("OrderItem")

                    itemsCollection.get()
                        .addOnSuccessListener { subCollectionSnapshot ->
                            for (subDocument in subCollectionSnapshot.documents) {
                                val item = subDocument.toObject(OrderItem::class.java)

                                item?.productId = order?.createAt?.let {
                                    formatTimestamp(
                                        it
                                    )
                                }
                                val price = item?.price?.times(item?.quantity!!)
                                item?.productImage = price?.let { formatNumber(it) }

                                item?.let {
                                    itemsList.add(it)
                                }
                                println(item.toString())
                            }

                            order?.let {
                                ordersList.add(it)
                            }

                            if (ordersList.size == querySnapshot.documents.size) {
                                viewModelScope.launch {
                                    _historyOrderItems.emit(Resource.Success(itemsList))
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
                    _historyOrderItems.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun formatTimestamp(timestamp: Timestamp): String {
        val dateFormatter = SimpleDateFormat("dd MMMM") // "24 June"
        val timeFormatter = SimpleDateFormat("HH:mm")  // "12:30"

        val dateString = dateFormatter.format(timestamp.toDate())
        val timeString = timeFormatter.format(timestamp.toDate())

        val deadline = timestamp.toDate()
        val deadlineString = timeFormatter.format(deadline)

        return "$dateString | $timeString | by $deadlineString"
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