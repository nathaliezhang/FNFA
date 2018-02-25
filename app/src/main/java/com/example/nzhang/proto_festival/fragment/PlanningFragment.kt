package com.example.nzhang.proto_festival.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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

        val cover = view.findViewById<ConstraintLayout>(R.id.black_opacity_cover)
        val btnPro = view.findViewById<ImageButton>(R.id.btn_list_item_pro)
        val btnProContainer = view.findViewById<LinearLayout>(R.id.btn_container_pro)
        val btnPublic = view.findViewById<ImageButton>(R.id.btn_list_item_public)
        val btnPublicContainer = view.findViewById<LinearLayout>(R.id.btn_container_public)
        val bottomSheet = view.findViewById<ConstraintLayout>(R.id.bottom_sheet)
        val bottomSheetText = view.findViewById<TextView>(R.id.bottom_sheet_text)
        val bottomBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val handler = Handler()

        btnPro.setOnClickListener {
            btnPro.isSelected = !btnPro.isSelected
            btnPublic.isSelected = false
            btnPublic.setBackgroundResource(R.drawable.bouton_tp)
            btnPublicContainer.setBackgroundResource(R.drawable.borderlines)
            cover.visibility = View.VISIBLE

            if (btnPro.isSelected) {
                btnProContainer.setBackgroundResource(R.drawable.borderlines_full_green)
                btnPro.setBackgroundResource(R.drawable.bouton_pro_clique)
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "pro")
                bottomSheetText.text = resources.getString(R.string.bottom_sheet_filter_pro)
            } else {
                btnProContainer.setBackgroundResource(R.drawable.borderlines)
                btnPro.setBackgroundResource(R.drawable.bouton_pro)
                recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "")
                bottomSheetText.text = resources.getString(R.string.bottom_sheet_no_filter)
            }
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            handler.postDelayed({
                bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                cover.visibility = View.GONE
            }, 4000)
        }
        btnPublic.setOnClickListener {
            btnPublic.isSelected = !btnPublic.isSelected
            btnPro.isSelected = false
            btnPro.setBackgroundResource(R.drawable.bouton_pro)
            btnProContainer.setBackgroundResource(R.drawable.borderlines)
            cover.visibility = View.VISIBLE

                if (btnPublic.isSelected) {
                    btnPublicContainer.setBackgroundResource(R.drawable.borderlines_full_green)
                    btnPublic.setBackgroundResource(R.drawable.bouton_tp_clique)
                    recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "public")
                    bottomSheetText.text = resources.getString(R.string.bottom_sheet_filter_public)
                } else {
                    btnPublicContainer.setBackgroundResource(R.drawable.borderlines)
                    btnPublic.setBackgroundResource(R.drawable.bouton_tp)
                    recycleView.adapter = DayAdapter(finalItemsList, places, categories, this, "")
                    bottomSheetText.text = resources.getString(R.string.bottom_sheet_no_filter)
                }
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            handler.postDelayed({
                bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                cover.visibility = View.GONE
            }, 4000)
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


