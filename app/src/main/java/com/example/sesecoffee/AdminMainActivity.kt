package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sesecoffee.databinding.ActivityAdminMainBinding
import com.example.sesecoffee.fragments.AdminHomeFragment
import com.example.sesecoffee.model.FirebaseSingleton
import com.example.sesecoffee.viewModel.ProductsViewModel

class AdminMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityAdminMainBinding
    lateinit var productsViewModel: ProductsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin_main)
        binding.adminBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(AdminHomeFragment())
                R.id.rewardFragment -> replaceFragment(AdminHomeFragment())
                R.id.orderTrackingFragment -> replaceFragment(AdminHomeFragment())

                else -> {
                    Toast.makeText(this,"Invalid navigation", Toast.LENGTH_SHORT).show()
                }
            }
            true

        }
        productsViewModel = ProductsViewModel(application)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.adminMainHostFragment,fragment)
        fragmentTransaction.commit()
    }
}