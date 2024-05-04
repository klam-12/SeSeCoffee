package com.example.sesecoffee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.sesecoffee.databinding.ActivityAddProductBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import java.util.UUID


class AddProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding

    private var imageUri: Uri? = null
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_product)
        productsViewModel = ProductsViewModel(application)

        lifecycleScope.launchWhenStarted {
            productsViewModel.products.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        val intent = Intent(applicationContext,AdminMainActivity::class.java)
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            productsViewModel.error.collectLatest {
                Toast.makeText(applicationContext,it, Toast.LENGTH_SHORT).show()
            }
        }


        binding.apply {
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

            productSaveBtn.setOnClickListener(){
                addNewProduct()
            }

            backBtn.setOnClickListener(){
                val intent = Intent(applicationContext,AdminMainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(){
        binding.progressBarNewPro.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBarNewPro.visibility = View.INVISIBLE
    }


    private fun addNewProduct() {
        val id = UUID.randomUUID().toString()
        var proName = binding.productInputName.text.toString()
        var proDesc = binding.productInputDescription.text.toString()
        var proPrice = binding.productInputPrice.text.toString()
        var imageUriString: String = if (imageUri == null) "" else imageUri.toString()
        var timeStamp: Timestamp = Timestamp(Date())
        var proPriceInt = if (proPrice.isNotEmpty()) proPrice.toInt() else 0
        var product: Product = Product(id,proName,proDesc, proPriceInt, imageUriString, timeStamp)

        productsViewModel.addAProduct(product, imageUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null!!
    }
}