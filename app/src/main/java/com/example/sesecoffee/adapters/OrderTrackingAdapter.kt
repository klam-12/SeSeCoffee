package com.example.sesecoffee.adapters

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class OrderTrackingAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val fragmentArrayList = ArrayList<Fragment>()
    private val fragmentTitle = ArrayList<String>()
    init {
//        super(fm)
    }
    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }
    fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        fragmentTitle.add(title)
    }
    override fun getPageTitle(position: Int): CharSequence? {
        val spannableString = SpannableString(fragmentTitle[position])
        spannableString.setSpan(RelativeSizeSpan(1.2f), 0, spannableString.length, 0)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#001833")), 0, spannableString.length, 0)
        return spannableString
    }
//    override fun getPageTitle(position: Int): CharSequence? {
//        return fragmentTitle[position]
//    }

}