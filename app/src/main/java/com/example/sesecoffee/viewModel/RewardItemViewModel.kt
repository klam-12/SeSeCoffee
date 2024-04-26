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
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RewardItemViewModel ( app: Application) : AndroidViewModel(
    app
) {
    private val _orderItems = MutableStateFlow<Resource<List<OrderItem>>>(Resource.Unspecified())
    val orders : StateFlow<Resource<List<OrderItem>>> = _orderItems
    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllOrders()
    }

    private fun fetchAllOrders() {
        viewModelScope.launch {
            _orderItems.emit(Resource.Loading())
        }

        fbSingleton.db.collection(Constant.ORDER_COLLECTION)
            .whereEqualTo("done", true)
            .whereEqualTo("delivered", false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ordersList = mutableListOf<Order>()
                val itemsList = mutableListOf<OrderItem>()
                println("HELLO $querySnapshot")

                for (document in querySnapshot.documents) {
                    val order = document.toObject(Order::class.java)
                    val itemsCollection = document.reference.collection("OrderItem")

                    itemsCollection.get()
                        .addOnSuccessListener { subCollectionSnapshot ->
                            for (subDocument in subCollectionSnapshot.documents) {
                                val item = subDocument.toObject(OrderItem::class.java)

                                item?.productId = order?.createAt?.let {
                                    timestampToFormattedString(
                                        it
                                    )
                                }
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
                                    _orderItems.emit(Resource.Success(itemsList))
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
                    _orderItems.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun timestampToFormattedString(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }


    fun fetchAProduct(proName: String){

    }
}