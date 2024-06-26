package com.example.sesecoffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.fragments.ChatFragment
import com.example.sesecoffee.fragments.HomeFragment
import com.example.sesecoffee.fragments.OrderTrackingFragment
import com.example.sesecoffee.fragments.RatingFragment
import com.example.sesecoffee.fragments.RewardFragment
import com.example.sesecoffee.model.UserSingleton
import com.example.sesecoffee.viewModel.MessageViewModel
import com.example.sesecoffee.viewModel.OrderItemsViewModel
import com.example.sesecoffee.viewModel.OrderViewModel
import com.example.sesecoffee.viewModel.ProductsViewModel
import com.example.sesecoffee.viewModel.RewardItemViewModel
import com.example.sesecoffee.viewModel.OrderTrackingViewModel
class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var productsViewModel: ProductsViewModel
    lateinit var rewardViewModel: RewardItemViewModel
    lateinit var orderViewModel: OrderViewModel
    lateinit var orderItemViewModel: OrderItemsViewModel
    lateinit var orderTrackingViewModel: OrderTrackingViewModel
    lateinit var messageViewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> replaceFragment(HomeFragment())
                R.id.rewardFragment -> replaceFragment(RewardFragment())
                R.id.orderTrackingFragment -> replaceFragment(OrderTrackingFragment())
                R.id.ratingFragment -> replaceFragment(RatingFragment())
                R.id.chatFragment -> replaceFragment(ChatFragment())

                else -> {
                    Toast.makeText(this,"Invalid navigation",Toast.LENGTH_SHORT).show()
                }
            }
            true

        }


        val user = UserSingleton.instance
        Log.i("User",user.toString())

        productsViewModel = ProductsViewModel(application);
        rewardViewModel = RewardItemViewModel(application);
        orderViewModel = OrderViewModel(application);
        orderItemViewModel = OrderItemsViewModel(application)
        orderTrackingViewModel = OrderTrackingViewModel(application)
        messageViewModel= MessageViewModel(application)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainHostFragment,fragment)
        fragmentTransaction.commit()
    }
}