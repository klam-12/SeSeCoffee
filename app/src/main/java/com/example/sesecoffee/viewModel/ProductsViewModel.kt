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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID


class ProductsViewModel(app: Application) : AndroidViewModel(
    app
) {
    private var productsList : MutableList<Product>? = null
    private  val fbSingleton = FirebaseSingleton.getInstance()

    // For list of products
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
//    val products : StateFlow<Resource<List<Product>>> = _products
    val products = _products.asStateFlow()

    // For update product
    private val _originalProduct = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val originalProduct = _originalProduct.asStateFlow()

    private val _updateProduct = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val updateProduct = _updateProduct.asStateFlow()

    // Receive Errors
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()


//    init {
//        fetchAllProducts()
//    }

    fun fetchAllProducts()  {
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

    fun addAProduct(product: Product, imageUri: Uri?) {
        val isValidate = validateInput(product)
        if(!isValidate || imageUri == null){
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
            return
        }

        viewModelScope.launch { _products.emit(Resource.Loading()) }
        val collectionReference : CollectionReference = fbSingleton.db.collection(PRODUCT_COLLECTION)

        // Saving the path of images in Storage
        // .../product_images/our_image.png
        val filePath : StorageReference = fbSingleton.storageReference.child("product_images")
            .child(product.name + "_" + product.id)

        // Uploading the image
        if (imageUri != null) {
            filePath.putFile(imageUri)
                .addOnSuccessListener() {
                    filePath.downloadUrl
                        .addOnSuccessListener {
                            collectionReference.document(product.id!!).set(product)
                                .addOnSuccessListener {

                                    productsList?.add(product)
                                    viewModelScope.launch {
                                        _products.emit(Resource.Success(productsList))
                                    }
                                }
                                .addOnFailureListener(){
                                    viewModelScope.launch {
                                        _products.emit(Resource.Error(it.message.toString()))
                                    }
                                }
                        }
                        .addOnFailureListener(){
                            Log.i("Err",it.toString())
                            viewModelScope.launch {
                                _products.emit(Resource.Error(it.message.toString()))
                            }
                        }
                }
                .addOnFailureListener() {
                    Log.i("Err",it.toString())
                    viewModelScope.launch {
                        _products.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun validateInput(product: Product): Boolean{
        return product.name!!.trim().isNotEmpty() &&
                product.price!! > 0 &&
                product.imageUrl!!.trim().isNotEmpty()
    }


    fun updateProduct(newProduct: Product, imageUri: Uri?, oldImageUrl : String?){
        val isValidate = validateInput(newProduct)
        if(!isValidate){
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
            return
        }
        viewModelScope.launch { _updateProduct.emit(Resource.Loading()) }

        if(imageUri == null){
            updateProductInfo(newProduct,true,oldImageUrl)
        } else {
            updateProductWithNewImg(newProduct,imageUri)
        }
    }

    private fun updateProductWithNewImg(newProduct: Product, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val filePath : StorageReference = fbSingleton.storageReference.child("product_images")
                    .child(newProduct.name + "_" + UUID.randomUUID().toString())

                val result = filePath.putFile(imageUri).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                updateProductInfo(newProduct.copy(imageUrl = imageUrl),false,null)

            } catch (e:Exception){
                viewModelScope.launch {
                    _updateProduct.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun updateProductInfo(newProduct: Product, shouldGetOldImage: Boolean, oldImageUrl: String?) {
        val documentRef = fbSingleton.db.collection(PRODUCT_COLLECTION).document(newProduct.id!!)
        fbSingleton.db.runTransaction{transaction ->
            val snapshot = transaction.get(documentRef)
            if(snapshot.exists()){
                if(shouldGetOldImage){
                    newProduct.imageUrl = oldImageUrl ?: ""
                }
                documentRef.set(newProduct)
            }

        } .addOnSuccessListener {
            viewModelScope.launch {
                _updateProduct.emit(Resource.Success(newProduct))
            }

        } .addOnFailureListener{
            viewModelScope.launch {
                _updateProduct.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun deleteProduct(productID:String){
        viewModelScope.launch {
            fbSingleton.db.collection(PRODUCT_COLLECTION).document(productID).delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _updateProduct.emit(Resource.Success(null))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _updateProduct.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}