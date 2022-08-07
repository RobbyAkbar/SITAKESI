package com.zeecodeku.sitakesi.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val mFragmentList : ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList : ArrayList<String> = ArrayList()
    private lateinit var bundle : Bundle

    fun addFragment(fragment: Fragment, args: String, title: String, position: Int) {
        mFragmentList.add(position, fragment)
        mFragmentTitleList.add(position, title)
        bundle = Bundle()
        bundle.putString("args", args)
        bundle.putString("title", title)
        fragment.arguments = bundle
    }

    fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}
