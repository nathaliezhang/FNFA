package com.example.nzhang.proto_festival.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nzhang.proto_festival.*


class PlanningFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_planning, container, false)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager: ViewPager = view.findViewById(R.id.planning_viewpager)

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = ListPagerAdapter(context, fragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        val tabLayout: TabLayout = view.findViewById(R.id.planning_tabs)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}


