package com.example.sesecoffee

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sesecoffee.databinding.ActivityAdminMainBinding
import com.example.sesecoffee.fragments.AdminChatFragment
import com.example.sesecoffee.fragments.AdminHomeFragment
import com.example.sesecoffee.fragments.AdminOrderFragment
import com.example.sesecoffee.fragments.AdminRedeemFragment
import com.example.sesecoffee.viewModel.MessageViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.example.sesecoffee.viewModel.RedeemItemViewModel

class AdminMainActivity : AppCompatActivity() {
    lateinit var binding : ActivityAdminMainBinding
    lateinit var productsViewModel: ProductsViewModel
    lateinit var redeemItemViewModel: RedeemItemViewModel
    lateinit var orderViewModel: OrderViewModel
    lateinit var messageViewModel: MessageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin_main)
        binding.adminBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(AdminHomeFragment())
                R.id.rewardFragment -> replaceFragment(AdminRedeemFragment())
                R.id.orderTrackingFragment -> replaceFragment(AdminOrderFragment())
                R.id.ratingFragment -> replaceFragment(AdminRatingFragment())
                R.id.chatFragment -> replaceFragment(AdminChatFragment())

                else -> {
                    Toast.makeText(this,"Invalid navigation", Toast.LENGTH_SHORT).show()
                }
            }
            true

        }
        productsViewModel = ProductsViewModel(application)
        redeemItemViewModel = RedeemItemViewModel(application)
        orderViewModel = OrderViewModel(application)
        messageViewModel= MessageViewModel(application)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.adminMainHostFragment,fragment)
        fragmentTransaction.commit()
    }
}