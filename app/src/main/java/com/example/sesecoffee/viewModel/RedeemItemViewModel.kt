package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.OrderItem
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Constant
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.sesecoffee.utils.Resource
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RedeemItemViewModel(app: Application) : AndroidViewModel(app){
    private val _redeem = MutableStateFlow<Resource<List<Redeem>>>(Resource.Unspecified())
    val redeemItem: StateFlow<Resource<List<Redeem>>> = _redeem

    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllRedeemWithValidDate()
    }

    fun fetchAllRedeemWithValidDate() {
        viewModelScope.launch { _redeem.emit(Resource.Loading()) }

        val nowTimestamp = Timestamp.now()
        fbSingleton.db.collection(Constant.REDEEM_COLLECTION)
            .whereGreaterThan("untilAt", nowTimestamp)
            .get()
            .addOnSuccessListener { result ->
                println("RESULT AFTER GET: ${result.toObjects(Redeem::class.java)}")

                val redeemList = result?.toObjects(Redeem::class.java)
                viewModelScope.launch {
                    _redeem.emit(Resource.Success(redeemList))
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _redeem.emit(Resource.Error(exception.message.toString()))
                }
            }

    }
}
