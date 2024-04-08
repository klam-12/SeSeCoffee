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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RedeemItemViewModel(app: Application) : AndroidViewModel(app){
    private val _redeem = MutableStateFlow<Resource<List<Redeem>>>(Resource.Unspecified())
    val redeemItem: StateFlow<Resource<List<Redeem>>> = _redeem

    private val fbSingleton = FirebaseSingleton.getInstance()

    init {
        fetchAllRedeem()
    }

    fun fetchAllRedeem() {
        viewModelScope.launch { _redeem.emit(Resource.Loading()) }

        fbSingleton.db.collection(Constant.REDEEM_COLLECTION).get()
            .addOnSuccessListener {
                    result ->
                val redeemList = result.toObjects(Redeem::class.java)
                viewModelScope.launch {
                    _redeem.emit(Resource.Success(redeemList))
                }

            }.addOnFailureListener() {
                Toast.makeText(
                    getApplication(),
                    "Errors happen when loading the database",
                    Toast.LENGTH_LONG
                ).show()
                viewModelScope.launch {
                    _redeem.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
