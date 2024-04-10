package com.example.sesecoffee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.sesecoffee.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

import com.google.android.material.tabs.TabLayout



class OrderTrackingFragment : Fragment(R.layout.fragment_order_tracking){
    lateinit var tabLayout: TabLayout;
    lateinit var viewPager: ViewPager;

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
        tabLayout = view.findViewById(R.id.tabOnGoing)
        viewPager = view.findViewById(R.id.viewOnGoing)
        val adapter = ViewOnGoingAdapter(getCli)
        adapter.addFragment(OnGoingFragment(), "On Going")
        adapter.addFragment(HistoryFragment(), "History")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

}