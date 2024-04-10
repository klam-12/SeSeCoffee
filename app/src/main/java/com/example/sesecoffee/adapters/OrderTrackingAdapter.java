package com.example.sesecoffee.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class OrderTrackingAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
private val fragmentArrayList = ArrayList<Fragment>()
private val fragmentTitle = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
        }

        override fun getCount(): Int {
        return fragmentArrayList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        fragmentTitle.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
        }
        }

