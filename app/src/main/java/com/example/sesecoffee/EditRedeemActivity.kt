package com.example.sesecoffee

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.sesecoffee.databinding.ActivityEditProductBinding
import com.example.sesecoffee.databinding.ActivityEditRedeemBinding
import com.example.sesecoffee.model.Redeem
import com.example.sesecoffee.utils.Resource
import com.example.sesecoffee.viewModel.RedeemItemViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRedeemActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditRedeemBinding
    lateinit var redeemItemViewModel: RedeemItemViewModel
    private var oldRedeem : Redeem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_redeem)
        redeemItemViewModel = RedeemItemViewModel(application)

        oldRedeem = intent.getParcelableExtra("redeem")!!
        binding.redeem = oldRedeem
        binding.redeemInputValid.setText(convertTimestampToString(oldRedeem?.untilAt))

        lifecycleScope.launchWhenStarted {
            redeemItemViewModel.updateRedeem.collectLatest {
                when(it){
                    is Resource.Loading -> {
//                        showLoading()
                    }
                    is Resource.Success -> {
//                        hideLoading()
                        if (it.data == null){
                            Toast.makeText(applicationContext,"Delete redeem successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext,"Edit redeem successfully", Toast.LENGTH_SHORT).show()
                        }
                        val intent = Intent(applicationContext,AdminMainActivity::class.java)
                        startActivity(intent)
                    }
                    is Resource.Error -> {
//                        hideLoading()
                        Toast.makeText(applicationContext,it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            redeemItemViewModel.error.collectLatest {
                Toast.makeText(applicationContext,it, Toast.LENGTH_SHORT).show()
            }
        }

        setUpInputValid()
        binding.apply {
            saveRedeemBtn.setOnClickListener(){
                updateRedeem()
            }

            delRedeemBtn.setOnClickListener(){
                if(oldRedeem!= null){
                    oldRedeem!!.id?.let { it1 -> redeemItemViewModel.deleteRedeem(it1) }
                }
            }
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
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateRedeem() {
        if(oldRedeem != null){
            val id = oldRedeem?.id
            val proId = oldRedeem?.productId
            val proName = oldRedeem?.productName
            val proImg = oldRedeem?.imageUrl
            val point = binding.redeemInputPoint.text.toString()
            val validTimeString = binding.redeemInputValid.text.toString()
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
            val redeem = Redeem(id,proId,proName,proImg,pointInt,validTime)
            redeemItemViewModel.updateRedeem(redeem)

        }
    }

    private fun convertTimestampToString(timestamp: Timestamp?) : String{
        // Convert Timestamp to Date
        val date = timestamp?.toDate()

        // Define the desired date format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        // Format the Date object into a string
        var validDateString = "Err"
        if(date != null){
            validDateString = dateFormat.format(date)
        }
        return validDateString
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null!!
    }
}