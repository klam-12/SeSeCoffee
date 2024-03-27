package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.model.Product

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//       val navController = findNavController(R.id.mainHostFragment)
//        binding.bottomNavigationView.setupWithNavController(navController)

    }
}