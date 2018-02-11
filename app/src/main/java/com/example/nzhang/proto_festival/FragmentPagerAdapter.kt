package com.example.nzhang.proto_festival

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.nzhang.proto_festival.fragment.InfoFragment
import com.example.nzhang.proto_festival.fragment.PlanningFragment


/**
 * Created by mel on 11/02/2018.
 */
class FragmentPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // This determines the fragment for each tab
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PlanningFragment()
            else -> InfoFragment()
        }
    }

    // This determines the number of tabs
    override fun getCount(): Int {
        return 2
    }

    // This determines the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return when (position) {
            0 -> "Planning"
            else -> "Infos"
        }
    }

}
