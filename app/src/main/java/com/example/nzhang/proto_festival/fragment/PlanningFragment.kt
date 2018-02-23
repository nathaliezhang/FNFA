package com.example.nzhang.proto_festival.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nzhang.proto_festival.*
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Places
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


class PlanningFragment : Fragment() {

    lateinit private var recycleView: RecyclerView
    lateinit private var tickReceiver: BroadcastReceiver
    lateinit private var places: List<Places.Place>
    lateinit private var categories: List<Categories.Category>
    lateinit private var finalItemsList: List<Any>
    lateinit private var daysLimits: Map<Int, String>
    lateinit private var headerPosition: List<Int>
    lateinit private var days: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataController = DataController(activity)
        places = dataController.places
        categories = dataController.categories
        val dataClass = dataController.getData()
        finalItemsList = dataClass.getFinalItemsList()
        daysLimits = dataClass.getDaysLimits()
        days = dataClass.allDays
        headerPosition = dataClass.getHeaderPosition()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_planning, container, false)

        val mLayoutManager = LinearLayoutManager(context)

        recycleView = view.findViewById(R.id.container_list)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(headerPosition, finalItemsList, places, categories)

        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateEventAdapter()
            }
        }
        //Register the broadcast receiver to receive TIME_TICK
        activity.registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        return view
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


