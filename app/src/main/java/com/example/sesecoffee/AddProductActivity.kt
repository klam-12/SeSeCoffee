package com.example.sesecoffee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.sesecoffee.databinding.ActivityAddProductBinding
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.util.Date


class AddProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding

    // Credentials
    var currentUserId : String = ""
    var currentUserName : String = ""

    private var imageUri: Uri? = null
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_product)
        val firebaseSingleton = FirebaseSingleton.getInstance()
        productsViewModel = ProductsViewModel(firebaseSingleton,application)


        binding.apply {
            progressBarNewPro.visibility = View.INVISIBLE

            val selectImageActivityForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                    if(result.resultCode == RESULT_OK){
                        val intent = result.data
                        imageUri = intent?.data

                        imageUri?.let {
                            contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }
                        binding.productImage.setImageURI(imageUri)

                    }
            }

            // Get image from Gallery
            productImageBtn.setOnClickListener(){
                var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                selectImageActivityForResult.launch(intent)
            }

            // Check user and get info
            if(UserSingleton.instance != null){
                // User info : Bug
//                currentUserId = UserSingleton.instance!!.id.toString()
//                currentUserName = UserSingleton.instance!!.fullName.toString()

            }

            productSaveBtn.setOnClickListener(){
                addNewProduct()
                val intent = Intent(applicationContext,AdminMainActivity::class.java)
                startActivity(intent)

            }
        }
    }

    private fun addNewProduct() {
        lifecycleScope.launch {
            var proName = binding.productInputName.text.toString().trim()
            var proPrice = binding.productInputPrice.text.toString().trim()
            var imageUriString: String = imageUri.toString()
            var timeStamp: Timestamp = Timestamp(Date())
            var proPriceInt = proPrice.toInt()
            var product: Product = Product(proName, proPriceInt, imageUriString, timeStamp)

            binding.progressBarNewPro.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(proName) && !TextUtils.isEmpty(proPrice) && imageUri != null) {
                var isSuccess = false

                isSuccess = productsViewModel.addAProduct(product, imageUri!!)
                if (isSuccess) {
                    Toast.makeText(
                        applicationContext,
                        "Add product successfully",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(applicationContext, "Fail to add product", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                binding.progressBarNewPro.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "Please fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
//        if(auth != null){
            // Do some remove,...
//        }
    }
}