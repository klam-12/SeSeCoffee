package com.example.sesecoffee.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.example.sesecoffee.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProductsViewModel(private val fbSingleton: FirebaseSingleton, app: Application) : AndroidViewModel(
    app
) {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products : StateFlow<Resource<List<Product>>> = _products

    init {
        fetchAllProducts()
    }

    fun fetchAllProducts()  {
        viewModelScope.launch { _products.emit(Resource.Loading()) }

            fbSingleton.db.collection(PRODUCT_COLLECTION).get()
                .addOnSuccessListener {
                    result ->
                    val productsList = result.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _products.emit(Resource.Success(productsList))
                    }

                }.addOnFailureListener() {
                    Toast.makeText(
                        getApplication(),
                        "Errors happen when loading the database",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModelScope.launch {
                        _products.emit(Resource.Error(it.message.toString()))
                    }
                }
    }

    fun fetchAProduct(proName: String){

    }
}