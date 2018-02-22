package com.example.nzhang.proto_festival

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.content.res.AppCompatResources.getDrawable
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.SpannableStringBuilder

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

        val sb = SpannableStringBuilder(" ")

        if (position == 0) {

            val drawable = getDrawable(mContext, R.drawable.bouton_tp_clique)
            drawable!!.setBounds(0, 0, 500, 165)
            val imageSpan = ImageSpan(drawable)
            //to make our tabs icon only, set the Text as blank string with white space
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (position == 1) {
            val drawable = getDrawable(mContext, R.drawable.bouton_pro)
            drawable!!.setBounds(0, 0, 500, 165)
            val imageSpan = ImageSpan(drawable)
            //to make our tabs icon only, set the Text as blank string with white space
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return sb
    }

}
