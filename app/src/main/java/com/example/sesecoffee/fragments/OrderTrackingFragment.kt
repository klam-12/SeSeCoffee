package com.example.sesecoffee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.sesecoffee.R
import com.example.sesecoffee.adapters.OrderTrackingAdapter
import com.google.android.material.tabs.TabLayout



class OrderTrackingFragment : Fragment(R.layout.fragment_order_tracking){
    private lateinit var tabLayout: TabLayout;
    private lateinit var viewPager: ViewPager;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_tracking, container, false)

        addFragment(view)
        return view
    }

    private fun addFragment(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        val adapter = OrderTrackingAdapter(childFragmentManager)
        adapter.addFragment(OnGoingFragment(), "On Going")
        adapter.addFragment(HistoryFragment(), "History")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

}