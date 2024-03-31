package com.example.sesecoffee.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.AdminMainActivity
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.example.sesecoffee.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class ProductsViewModel(private val fbSingleton: FirebaseSingleton, app: Application) : AndroidViewModel(
    app
) {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products : StateFlow<Resource<List<Product>>> = _products
    private var productsList : MutableList<Product>? = null
//    val p = _products.asStateFlow()

    init {

    }

    fun fetchAllProducts()  {
        Toast.makeText(getApplication(),"Fetch all products",Toast.LENGTH_LONG).show()

        viewModelScope.launch { _products.emit(Resource.Loading()) }

            fbSingleton.db.collection(PRODUCT_COLLECTION).get()
                .addOnSuccessListener {
                    result ->
                    productsList = result.toObjects(Product::class.java)
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

    fun addAProduct(product: Product, imageUri: Uri) : Boolean{
        var isSuccess = true
        val collectionReference : CollectionReference = fbSingleton.db.collection(PRODUCT_COLLECTION)

        // Saving the path of images in Storage
        // .../product_images/our_image.png
        val filePath : StorageReference = fbSingleton.storageReference.child("product_images")
            .child(product.name + "_" + Timestamp.now().seconds)

        // Uploading the image
        filePath.putFile(imageUri)
            .addOnSuccessListener() {
                filePath.downloadUrl
                    .addOnSuccessListener {
                        collectionReference.add(product)
                            .addOnSuccessListener {

                                productsList?.add(product)
                                viewModelScope.launch {
                                    _products.emit(Resource.Success(productsList))
                                }
                            }
                    }
                    .addOnFailureListener(){
                        isSuccess = false
                        Log.i("Err",it.toString())
                    }
            }
            .addOnFailureListener() {
                Log.i("Err",it.toString())
            }

        return isSuccess;
    }

    fun addAProductNew(product: Product, imageUri: Uri) : Boolean{
        viewModelScope.launch {
            addAProduct(product,imageUri)
        }
        return true
    }
}