package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.fragments.HomeFragment
import com.example.sesecoffee.fragments.RewardFragment
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.viewModel.ProductsViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var productsViewModel: ProductsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(HomeFragment())
                R.id.rewardFragment -> replaceFragment(RewardFragment())
//                R.id.orderTrackingFragment -> replaceFragment(RewardFragment())

                else -> {
                    Toast.makeText(this,"Invalid navigation",Toast.LENGTH_SHORT).show()
                }
            }
            true

        }

        val user = UserSingleton.instance
        Log.i("User",user.toString())
        productsViewModel = ProductsViewModel(application)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainHostFragment,fragment)
        fragmentTransaction.commit()
    }
}