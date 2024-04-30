package com.example.sesecoffee

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
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
    lateinit var redeemItemViewModel: RedeemItemViewModel
    private var oldRedeem : Redeem? = null

    lateinit var inputValid : EditText
    lateinit var inputName : TextView
    lateinit var inputPoint : EditText
    lateinit var saveRedeemBtn : Button
    lateinit var delRedeemBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_redeem)
        redeemItemViewModel = RedeemItemViewModel(application)

        oldRedeem = intent.getParcelableExtra("redeem")!!
        val position = intent.getIntExtra("position",0)


        inputName = findViewById(R.id.redeem_name)
        inputPoint = findViewById(R.id.redeem_input_point)
        inputValid = findViewById(R.id.redeem_input_valid)
        saveRedeemBtn = findViewById(R.id.saveRedeemBtn)
        delRedeemBtn = findViewById(R.id.delRedeemBtn)

        inputName.text = oldRedeem?.productName
        inputPoint.setText(oldRedeem?.point.toString())
        inputValid.setText(convertTimestampToString(oldRedeem?.untilAt))

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
                        finish()
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

        saveRedeemBtn.setOnClickListener(){
            updateRedeem(position)

        }

        delRedeemBtn.setOnClickListener(){
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        if(oldRedeem!= null){
                            oldRedeem!!.id?.let { it1 -> redeemItemViewModel.deleteRedeem(it1) }
                        }
                    })
                    setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->

                    })
                    // Set other dialog properties
                    setIcon(R.drawable.ic_warning_yellow)
                    setTitle("Do you want to delete this redeem?")
                }
                // Create the AlertDialog
                builder.create()
            }

            if (alertDialog != null) {
                alertDialog!!.show()
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
            inputValid.setText(sdf.format(cal.time))
        }

        inputValid.setOnClickListener() {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateRedeem(position: Int) {
        if(oldRedeem != null){
            val id = oldRedeem?.id
            val proId = oldRedeem?.productId
            val proName = oldRedeem?.productName
            val proImg = oldRedeem?.imageUrl
            val point = inputPoint.text.toString()
            val validTimeString = inputValid.text.toString()
            val pointInt : Int
            try {
                pointInt = Integer.parseInt(point)
            }catch (e: NumberFormatException){
                inputPoint.error = "Invalid point"
                return
            }
            if(pointInt <= 0){
                inputPoint.error = "Point must be a positive integer"
                return
            }
            if(validTimeString == ""){
                inputValid.error = "Invalid date"
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
                inputValid.error = "Invalid date"
                return
            }
            val redeem = Redeem(id,proId,proName,proImg,pointInt,validTime)
            redeemItemViewModel.updateRedeem(redeem,position)

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

}