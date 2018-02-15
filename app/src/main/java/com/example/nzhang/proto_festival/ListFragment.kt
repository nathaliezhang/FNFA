package com.example.nzhang.proto_festival

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import android.support.v7.widget.LinearSmoothScroller
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*




class ListFragment : Fragment() {

    private var AutomaticScrollInProgress = false
    private var ManualScrollInProgress = false

    lateinit private var tabBar: TabLayout
    lateinit private var recycleView: RecyclerView
    lateinit private var tickReceiver: BroadcastReceiver
    lateinit private var places: List<Places.Place>
    lateinit private var categories: List<Categories.Category>
    lateinit private var finalItemsList: List<Any>
    lateinit private var daysLimits: Map<Int, String>
    lateinit private var headerPosition: List<Int>
    lateinit private var days: List<String>

    companion object {
        private val typeList = "public"

        fun newInstance(type: String): ListFragment {
            val instance = ListFragment()
            val args = Bundle()
            args.putString(typeList, type)
            instance.arguments = args

            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val type = arguments.getString(typeList)
            val dataController = DataController(activity)
            places = dataController.places
            categories = dataController.categories
            val dataClass = dataController.get(type)
            finalItemsList = dataClass.getFinalItemsList()
            daysLimits = dataClass.getDaysLimits()
            days = dataClass.allDays
            headerPosition = dataClass.getHeaderPosition()
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_list, container, false)
        tabBar = view.findViewById(R.id.tab_bar)

        days.forEach({
            val tab: TabLayout.Tab = tabBar.newTab()
            tab.text = getDayName(it)
            tabBar.addTab(tab)
        })

        val mLayoutManager = LinearLayoutManager(context)

        recycleView = view.findViewById(R.id.container_list)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(headerPosition, finalItemsList, places, categories)

        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        tabBar.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                initTabsText()
                if(!ManualScrollInProgress) {
                    val position =
                            if(days[tab!!.position] != getCurrentDay()) finalItemsList.indexOfFirst({it is String && it == getDayName(days[tab.position])})
                            else finalItemsList.indexOfFirst({it is Events.Event && it.getFullStartingDate() == getCurrentDay() && it.getStartingDate().time > System.currentTimeMillis()})
                    smoothScroller.targetPosition = position
                    recycleView.layoutManager.startSmoothScroll(smoothScroller)
                    AutomaticScrollInProgress = true
                } else {
                    ManualScrollInProgress = false
                }
                updateTabsText(tab!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                initTabsText()
                val position =
                        if(days[tab!!.position] != getCurrentDay()) finalItemsList.indexOfFirst({it is String && it == getDayName(days[tab.position])})
                        else finalItemsList.indexOfFirst({it is Events.Event && it.getFullStartingDate() == getCurrentDay() && it.getStartingDate().time > System.currentTimeMillis()})
                smoothScroller.targetPosition = position
                recycleView.layoutManager.startSmoothScroll(smoothScroller)
                updateTabsText(tab)
            }
        })

        recycleView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if(!AutomaticScrollInProgress) {
                    val firstVisibleItemPosition: Int = mLayoutManager.findFirstVisibleItemPosition()
                    if (daysLimits.containsKey(firstVisibleItemPosition)
                            && tabBar.selectedTabPosition != days.indexOf(daysLimits[firstVisibleItemPosition])) {
                        ManualScrollInProgress = true
                        val tab = tabBar.getTabAt(days.indexOf(daysLimits[firstVisibleItemPosition]))
                        tab!!.select()
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    AutomaticScrollInProgress = false
                }
            }
        })

        if (getCurrentDay() in days)
            tabBar.getTabAt(days.indexOf(getCurrentDay()))!!.select()
        else
            tabBar.getTabAt(0)!!.select()


        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateEventAdapter()
            }
        }
        //Register the broadcast receiver to receive TIME_TICK
        activity.registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        return view
    }

    private fun getDayName(day: String): String {
        return day.substring(0, day.indexOf(" "))
    }

    private fun initTabsText() {
        days.forEach({
            tabBar.getTabAt(days.indexOf(it))!!.text = getDayName(it)
        })
    }

    private fun updateTabsText(tab: TabLayout.Tab) {
        tabBar.getTabAt(tab.position)!!.text = days[tab.position]
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

}// Required empty public constructor
