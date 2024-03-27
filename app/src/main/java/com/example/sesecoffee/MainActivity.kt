package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.fragments.HomeFragment
import com.example.sesecoffee.fragments.OrderTrackingFragment
import com.example.sesecoffee.fragments.RewardFragment
import com.example.sesecoffee.model.Product
import com.google.firestore.v1.StructuredQuery.Order

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(HomeFragment())
                R.id.rewardFragment -> replaceFragment(RewardFragment())
                R.id.orderTrackingFragment -> replaceFragment(OrderTrackingFragment())

                else -> {
                    Toast.makeText(this,"Invalid navigation",Toast.LENGTH_SHORT).show()
                }
            }
            true

        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainHostFragment,fragment)
        fragmentTransaction.commit()
    }
}