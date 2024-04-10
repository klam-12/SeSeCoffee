package com.example.sesecoffee.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Constant
import com.example.sesecoffee.utils.Resource
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RedeemItemViewModel(app:Application) : AndroidViewModel(app) {

    var redeemsList : MutableList<Redeem>? = null
    private val _redeems = MutableStateFlow<Resource<List<Redeem>>>(Resource.Unspecified())
    val redeems= _redeems.asSharedFlow()

    private val _updateRedeem = MutableStateFlow<Resource<Redeem>>(Resource.Unspecified())
    val updateRedeem = _updateRedeem.asStateFlow()

    // Receive Errors
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private val fbSingleton = FirebaseSingleton.getInstance()

    init{
        fetchAllRedeems()
    }

    fun fetchAllRedeems(){
        viewModelScope.launch{_redeems.emit(Resource.Loading())}

        fbSingleton.db.collection(Constant.REDEEM_COLLECTION).get()
            .addOnSuccessListener {
                result ->
                redeemsList = result.toObjects(Redeem::class.java)
                viewModelScope.launch {
                    _redeems.emit(Resource.Success(redeemsList))
                }
            }
            .addOnFailureListener{
                Log.i("SSErr","Errors happen when loading the database")
                viewModelScope.launch {
                    _redeems.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun addRedeem(redeem: Redeem){
        viewModelScope.launch { _redeems.emit(Resource.Loading()) }
        val collectionReference : CollectionReference = fbSingleton.db.collection(Constant.REDEEM_COLLECTION)
        collectionReference.document(redeem.id!!).set(redeem)
            .addOnSuccessListener {
                redeemsList?.add(redeem)
                viewModelScope.launch {
                    _redeems.emit(Resource.Success(redeemsList))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _redeems.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun updateRedeem(newRedeem : Redeem){
        val documentRef = fbSingleton.db.collection(Constant.REDEEM_COLLECTION).document(newRedeem.id!!)
        fbSingleton.db.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                documentRef.set(newRedeem)
            }
        } .addOnSuccessListener {
            viewModelScope.launch {
                _updateRedeem.emit(Resource.Success(newRedeem))
            }
        } .addOnFailureListener {
            viewModelScope.launch {
                _updateRedeem.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun deleteRedeem(redeemId: String){
        viewModelScope.launch {
            fbSingleton.db.collection(Constant.REDEEM_COLLECTION).document(redeemId).delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _updateRedeem.emit(Resource.Success(null))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _updateRedeem.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

}