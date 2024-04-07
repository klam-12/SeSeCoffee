package com.example.sesecoffee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sesecoffee.databinding.ActivityMainBinding
import com.example.sesecoffee.fragments.HistoryFragment
import com.example.sesecoffee.fragments.OnGoingFragment
import android.view.View
class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var  fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstantState: Bundle?){
        super.onCreate(savedInstantState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.onGoingBtn.setOnClickListener{
//            goToFragment(OnGoingFragment())
//        }
//        binding.historyBtn.setOnClickListener{
//            goToFragment(HistoryFragment())
//        }
    }

    private fun goToFragment(fragment: Fragment){
        fragmentManager = supportFragmentManager
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