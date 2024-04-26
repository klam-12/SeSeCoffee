package com.example.sesecoffee

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sesecoffee.databinding.OrderDetailItemBinding


class OrderDetailActivity : AppCompatActivity() {
    var binding: OrderDetailItemBinding?=null
    private val TAG = "OrderDetailActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= OrderDetailItemBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        getIncomingIntent();
    }
    private fun getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.")
        if (intent.hasExtra("image_url") && intent.hasExtra("image_name")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.")
            val imageUrl = intent.getStringExtra("image_url")
            val imageName = intent.getStringExtra("image_name")
//            setImage(imageUrl, imageName)
        }
    }


}