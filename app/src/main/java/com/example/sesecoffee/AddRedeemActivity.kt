package com.example.sesecoffee

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.sesecoffee.databinding.ActivityAddRedeemBinding
import com.example.sesecoffee.model.Product
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.example.sesecoffee.viewModel.RedeemItemViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddRedeemActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddRedeemBinding
    lateinit var productsViewModel: ProductsViewModel
    private var listProducts : List<Product>? = null
    lateinit var redeemItemViewModel : RedeemItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_redeem)
        productsViewModel = ProductsViewModel(application)
        redeemItemViewModel = RedeemItemViewModel(application)

        lifecycleScope.launchWhenStarted {
            productsViewModel.fetchAllProducts()
            productsViewModel.products.collectLatest {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        listProducts = it.data!!
                        it.data?.let { it1 -> setUpSpinnerProduct(it1) }
                    }
                    is Resource.Error -> {
                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }

            redeemItemViewModel.redeems.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        Toast.makeText(applicationContext,"Success",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(applicationContext, "Products: $it", Toast.LENGTH_SHORT).show()
            }

            redeemItemViewModel.error.collectLatest {
                Toast.makeText(applicationContext, "Redeems: $it", Toast.LENGTH_SHORT).show()
            }
        }

        setUpInputValid()

        binding.apply {

            backBtn.setOnClickListener(){
                finish()
            }

            submitBtn.setOnClickListener(){
                addRedeem();
                binding.apply {
                    redeemInputPoint.setText("")
                    redeemInputValid.setText("")
                }
                Toast.makeText(applicationContext,"Add successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addRedeem(){
        val id = UUID.randomUUID().toString();
        val productName = binding.spinnerProduct.selectedItem.toString()
        val productId = listProducts?.find { it.name == productName}?.id
        val productImg = listProducts?.find { it.name == productName}?.imageUrl
        val point = binding.redeemInputPoint.text.toString()
        val validTimeString = binding.redeemInputValid.text.toString()
        if(productId == null){
            Toast.makeText(this,"productId not found",Toast.LENGTH_SHORT).show()
            return
        }

        val pointInt : Int
        try {
            pointInt = Integer.parseInt(point)
        }catch (e: NumberFormatException){
            binding.redeemInputPoint.error = "Invalid point"
            return
        }
        if(pointInt <= 0){
            binding.redeemInputPoint.error = "Point must be a positive integer"
            return
        }
        if(validTimeString == ""){
            binding.redeemInputValid.error = "Invalid date"
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        var validTime : Timestamp? = null
        try {
            // Parse the string into a Date object
            val date = dateFormat.parse(validTimeString)

            // Convert Date to Timestamp
            validTime = date?.let { Timestamp(it) }!!
        } catch (e: Exception) {
            binding.redeemInputValid.error = "Invalid date"
            return
        }
        val redeem = Redeem(id,productId,productName,productImg,pointInt,validTime)
        redeemItemViewModel.addRedeem(redeem);
    }

    private fun setUpSpinnerProduct(listPro : List<Product>){
        val listProductsName = mutableListOf<String>()
        listPro.forEach { pro ->
            pro.name?.let { listProductsName.add(it) }
        }

        // Sort the list alphabetically
        listProductsName.sort()

        ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,listProductsName)
            .also{ adapter ->
                adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                binding.spinnerProduct.adapter = adapter
            }
    }

    private fun setUpInputValid(){
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // format date
            val dateFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            binding.redeemInputValid.setText(sdf.format(cal.time))
        }


        binding.redeemInputValid.setOnClickListener() {
            val datePickerDialog = DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            // Set minimum date to today's date
            datePickerDialog.datePicker.minDate = cal.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun showLoading(){
        binding.progressBarAddRedeem.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBarAddRedeem.visibility = View.INVISIBLE
    }

}