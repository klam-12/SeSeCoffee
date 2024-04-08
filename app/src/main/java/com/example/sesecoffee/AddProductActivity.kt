package com.example.sesecoffee

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.sesecoffee.databinding.ActivityAddProductBinding
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.utils.Constant.PRODUCT_COLLECTION
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding

    // Credentials
    var currentUserId : String = ""
    var currentUserName : String = ""

    // Firebase
    lateinit var auth : FirebaseAuth
    lateinit var user : FirebaseUser

    // Firebase Firestore
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference : CollectionReference = db.collection(PRODUCT_COLLECTION)
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_product)

        storageReference = FirebaseStorage.getInstance().reference
        auth = Firebase.auth

        binding.apply {
            progressBarNewPro.visibility = View.INVISIBLE

            val selectImageActivityForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                    if(result.resultCode == RESULT_OK){
                        val intent = result.data
                        imageUri = intent?.data
                        binding.productImage.setImageURI(imageUri)
                    }
            }

            // Get image from Gallery
            productImageBtn.setOnClickListener(){
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
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
            }
        }
    }

    private fun addNewProduct() {
        var proName = binding.productInputName.text.toString().trim()
        var proPrice = binding.productInputPrice.text.toString().trim()

        binding.progressBarNewPro.visibility = View.VISIBLE

        if( !TextUtils.isEmpty(proName) && !TextUtils.isEmpty(proPrice) &&  imageUri != null){
            // Saving the path of images in Storage
            // .../product_images/our_image.png
            val filePath : StorageReference = storageReference.child("product_images")
                .child(proName + "_" + Timestamp.now().seconds)

            // Uploading the image
            filePath.putFile(imageUri!!)
                .addOnSuccessListener() {
                    filePath.downloadUrl
                        .addOnSuccessListener {
                            var imageUriString : String = it.toString()

                            var timeStamp: Timestamp = Timestamp(Date())
                            var proPriceInt = proPrice.toInt()

                            // Creating the object of product
                            var product : Product = Product(proName,proPriceInt,imageUriString,timeStamp)
                            collectionReference.add(product)
                                .addOnSuccessListener {
                                    binding.progressBarNewPro.visibility = View.INVISIBLE
                                    var intent = Intent(this,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                        .addOnFailureListener(){
                            binding.progressBarNewPro.visibility = View.INVISIBLE
                        }
                }
        }
        else{
            binding.progressBarNewPro.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
//        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if(auth != null){
            // Do some remove,...
        }
    }
}