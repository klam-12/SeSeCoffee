package com.example.sesecoffee

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.sesecoffee.databinding.ActivityEditProductBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

class EditProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditProductBinding
    private var imageUri: Uri? = null
    lateinit var productsViewModel: ProductsViewModel
    private var oldProduct : Product? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_product)
        productsViewModel = ProductsViewModel(application)

        oldProduct = intent.getParcelableExtra("product")!!
        binding.product = oldProduct

        lifecycleScope.launchWhenStarted {
            productsViewModel.updateProduct.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        if (it.data == null){
                            Toast.makeText(applicationContext,"Delete product successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext,"Edit product successfully", Toast.LENGTH_SHORT).show()
                        }
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
            Glide.with(applicationContext).load(oldProduct?.imageUrl).into(productImage)

            val selectImageActivityForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                    if(result.resultCode == AppCompatActivity.RESULT_OK){
                        val intent = result.data
                        imageUri = intent?.data

                        imageUri?.let {
                            root.context.contentResolver.takePersistableUriPermission(
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
                updateProduct()
            }

            productDeleteBtn.setOnClickListener(){
                val context = it.context
                val alertDialog: AlertDialog? = this.let {
                    val builder = AlertDialog.Builder(context)
                    builder.apply {
                        setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                            if(oldProduct != null){
                                oldProduct?.id?.let { it1 -> productsViewModel.deleteProduct(it1) }
                            }
                        })
                        setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->

                        })
                        // Set other dialog properties
                        setIcon(R.drawable.ic_warning_yellow)
                        setTitle("Do you want to delete this product?")
                    }
                    // Create the AlertDialog
                    builder.create()
                }

                if (alertDialog != null) {
                    alertDialog!!.show()
                }

            }
        }
    }

    private fun showLoading(){
        binding.progressBarEditPro.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBarEditPro.visibility = View.INVISIBLE
    }

    private fun updateProduct() {
        if (oldProduct!= null) {
            val id = oldProduct?.id
            var proName = binding.productInputName.text.toString()
            var proDesc = binding.productInputDescription.text.toString()
            var proPrice = binding.productInputPrice.text.toString()
            var imageUriString: String = imageUri.toString()
            var timeStamp: Timestamp = Timestamp(Date())
            var proPriceInt = if (proPrice.isNotEmpty()) proPrice.toInt() else 0
            var product = Product(id, proName,proDesc, proPriceInt, imageUriString, timeStamp)

            productsViewModel.updateProduct(product, imageUri, oldProduct?.imageUrl)
        }
    }

}