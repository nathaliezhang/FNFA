package com.example.nzhang.proto_festival

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.content.res.AppCompatResources
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places


class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    val tabunselected = arrayListOf(R.drawable.programme, R.drawable.plus, R.drawable.favoris)
    val tabselected = arrayListOf(R.drawable.programme_clique, R.drawable.plus_clique, R.drawable.favoris_clique)
    lateinit var dataController: DataController
    lateinit var places: List<Places.Place>
    lateinit var categories: List<Categories.Category>
    lateinit var finalItemsList: List<List<Events.Event>>
    lateinit var orderedList: List<Events.Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataController = DataController(this)
        places = dataController.places
        categories = dataController.categories
        finalItemsList = dataController.data
        orderedList = dataController.orderedEvents

        // Set the content of the activity to use the  activity_main.xml layout file
        setContentView(R.layout.activity_main)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager: ViewPager = findViewById(R.id.viewpager)

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = FragmentPagerAdapter(this, this.supportFragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        tabLayout = findViewById(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTabs(0, tab!!.position == 0)
                changeTabs(1, tab.position == 1)
                changeTabs(2, tab.position == 2)
            }
        })
    }

    fun changeTabs(position: Int, selected: Boolean) {
        if (selected){
            val sb = SpannableStringBuilder(" ")
            val drawable = AppCompatResources.getDrawable(this@MainActivity, tabselected[position])
            drawable!!.setBounds(0, 10, 160, 160)
            val imageSpan = ImageSpan(drawable)
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tabLayout.getTabAt(position)!!.text = sb
        } else {
            val sb = SpannableStringBuilder(" ")
            val drawable = AppCompatResources.getDrawable(this@MainActivity, tabunselected[position])
            drawable!!.setBounds(0, 10, 160, 160)
            val imageSpan = ImageSpan(drawable)
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tabLayout.getTabAt(position)!!.text = sb
        }
    }
}


