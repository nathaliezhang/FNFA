package com.example.nzhang.proto_festival.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.nzhang.proto_festival.*
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


class PlanningFragment : Fragment(), DayAdapter.WhichDayClickedInterface {

    lateinit private var recycleView: RecyclerView
    lateinit private var tickReceiver: BroadcastReceiver
    lateinit private var places: List<Places.Place>
    lateinit private var categories: List<Categories.Category>
    lateinit private var finalItemsList: List<List<Events.Event>>
    lateinit private var smoothScroller: RecyclerView.SmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataController = DataController(activity)
        places = dataController.places
        categories = dataController.categories
        finalItemsList = dataController.data
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_planning, container, false)

        recycleView = view.findViewById(R.id.day_list)
        val mLayoutManager = LinearLayoutManager(context)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "")


        val btn_pro = view.findViewById<ImageButton>(R.id.btn_list_item_pro)
        val btn_public = view.findViewById<ImageButton>(R.id.btn_list_item_public)
        btn_pro.setOnClickListener {
            btn_pro.isSelected = !btn_pro.isSelected
            btn_public.isSelected = false
            if (btn_pro.isSelected) {
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "pro")
            } else {
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "")
            }
        }
        btn_public.setOnClickListener {
            btn_public.isSelected = !btn_public.isSelected
            btn_pro.isSelected = false
            if (btn_public.isSelected) {
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "public")
            } else {
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "")
            }
        }


        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateEventAdapter()
            }
        }
        //Register the broadcast receiver to receive TIME_TICK
        activity.registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        return view
    }

    override fun onDayClicked(number: Int) {
        smoothScroller.targetPosition = number
        recycleView.layoutManager.startSmoothScroll(smoothScroller)
    }

    private fun updateEventAdapter() {
        recycleView.adapter.notifyDataSetChanged()
    }

    private fun getCurrentDay(): String {
        val current = System.currentTimeMillis()
        val date = Date(current)
        return convertDay(date)
    }

    private fun convertDay(date: Date): String {
        return SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(date).toString()
    }

}


