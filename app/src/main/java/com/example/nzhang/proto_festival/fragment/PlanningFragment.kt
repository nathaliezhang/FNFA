package com.example.nzhang.proto_festival.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nzhang.proto_festival.*
import android.support.v7.content.res.AppCompatResources
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan


class PlanningFragment : Fragment() {

    lateinit private var tabLayout: TabLayout
    private val tabunselected = arrayListOf(R.drawable.bouton_tp, R.drawable.bouton_pro)
    private val tabselected = arrayListOf(R.drawable.bouton_tp_clique, R.drawable.bouton_pro_clique)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_planning, container, false)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager: ViewPager = view.findViewById(R.id.planning_viewpager)

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = ListPagerAdapter(context, childFragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        tabLayout = view.findViewById(R.id.planning_tabs)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTabs(0, tab!!.position == 0)
                changeTabs(1, tab.position == 1)
            }
        })

        return view
    }

    fun changeTabs(position: Int, selected: Boolean) {
        if (selected){
            val sb = SpannableStringBuilder(" ")
            val drawable = AppCompatResources.getDrawable(context, tabselected[position])
            drawable!!.setBounds(0, 0, 500, 165)
            val imageSpan = ImageSpan(drawable)
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tabLayout.getTabAt(position)!!.text = sb
        } else {
            val sb = SpannableStringBuilder(" ")
            val drawable = AppCompatResources.getDrawable(context, tabunselected[position])
            drawable!!.setBounds(0, 0, 500, 165)
            val imageSpan = ImageSpan(drawable)
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tabLayout.getTabAt(position)!!.text = sb
        }
    }
}


