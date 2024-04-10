package com.example.sesecoffee.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.sesecoffee.R
import com.example.sesecoffee.databinding.ActivityMainBinding


class OrderTrackingFragment : Fragment(R.layout.fragment_order_tracking){
    private lateinit var  fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstantState: Bundle?){
        super.onCreate(savedInstantState)
//        binding = DataBindingUtil.setContentView(R.layout.fragment_order_tracking)

//        binding.onGoingBtn.setOnClickListener{
//            goToFragment(OnGoingFragment())
//        }
//        binding.historyBtn.setOnClickListener{
//            goToFragment(HistoryFragment())
//        }
    }

    private fun goToFragment(fragment: Fragment){
        fragmentManager;// = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit()
    }
    fun onGoingBtnClick(view: View) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.orderTrackingFragment, OnGoingFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    fun historyBtnClick(view: View) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.orderTrackingFragment, HistoryFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}