package com.example.nzhang.proto_festival

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ListPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // This determines the fragment for each tab
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment.newInstance("public")
            1 -> ListFragment.newInstance("pro")
            else -> ListFragment.newInstance("public")
        }
    }

    // This determines the number of tabs
    override fun getCount(): Int {
        return 2
    }

    // This determines the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Public"
            1 -> "Professional"
            else -> "Public"
        }
    }

}
