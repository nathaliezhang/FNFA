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
import android.view.ViewTreeObserver
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import java.sql.Timestamp

class ListFragment : Fragment() {

    private var isInit = true
    private var AutomaticScrollInProgress = false
    private var ManualScrollInProgress = false
    private var notUpdatingTab = true

    private var previousTabPosition = -1
    private var days = arrayListOf("Mercredi","Jeudi","Vendredi","Samedi","Dimanche")
    private val daysNumber = hashMapOf("Mercredi" to " 4 Avril", "Jeudi" to " 5 Avril", "Vendredi" to " 6 Avril", "Samedi" to " 7 Avril", "Dimanche" to " 8 Avril")

    lateinit private var tabBar: TabLayout
    lateinit private var recycleView: RecyclerView
    lateinit private var tickReceiver: BroadcastReceiver
    lateinit private var places: List<Places.Place>
    lateinit private var categories: List<Categories.Category>
    lateinit private var orderedEvents: List<Events.Event>
    lateinit private var eventsAndHeaders: List<Any>
    lateinit private var daysLimitIndex: Map<Int, String>
    lateinit private var headerPosition: Map<String, Int>

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
            val dataClass = DataController(activity)
            places = dataClass.places
            categories = dataClass.categories
            val eventsList = dataClass.get(type)
            orderedEvents = eventsList
            daysLimitIndex = dataClass.getDaysLimitIndex(eventsList)
            val computedEventsAndHeaders = dataClass.getAllEvents(eventsList)
            headerPosition = dataClass.getHeadersPosition(computedEventsAndHeaders)
            eventsAndHeaders = computedEventsAndHeaders
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_list, container, false)
        tabBar = view.findViewById(R.id.tab_bar)
        val mLayoutManager = ScrollingLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false, 100)

        recycleView = view.findViewById(R.id.container_list)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(headerPosition, eventsAndHeaders, places, categories)

        recycleView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isInit) {
                    isInit = false
                    val index = days.indexOf(getCurrentDay())

                    if (index >= 0) {
                        val tab = tabBar.getTabAt(index)
                        tab!!.select()
                    } else {
                        val tab = tabBar.getTabAt(0)
                        tab!!.select()
                    }
                }
            }
        })

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (!AutomaticScrollInProgress) {
                    val firstVisibleItemIndex: Int = mLayoutManager.findFirstVisibleItemPosition()
                    if (daysLimitIndex.containsKey(firstVisibleItemIndex)) {
                        if (tabBar.getTabAt(tabBar.selectedTabPosition)!!.text != daysLimitIndex[firstVisibleItemIndex]) {
                            val index = days.indexOf(daysLimitIndex[firstVisibleItemIndex])
                            val tab = tabBar.getTabAt(index)
                            notUpdatingTab = false
                            tab!!.select()
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    AutomaticScrollInProgress = false
                }
            }
        })
        tabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //do stuff here
                initTabsText()
                if (!ManualScrollInProgress) {
                    AutomaticScrollInProgress = true
                    if (tab.text == getCurrentDay()) {
                        if (previousTabPosition < tab.position) {
                            val index = orderedEvents.indexOfFirst({it.getStartingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                            recycleView.smoothScrollToPosition(index+4)
                        } else {
                            val index = orderedEvents.indexOfFirst({it.getStartingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                            recycleView.smoothScrollToPosition(index)
                        }
                    } else {
                        if (previousTabPosition < tab.position) {
                            recycleView.smoothScrollToPosition(headerPosition[tab.text]!!+4)
                        } else {
                            recycleView.smoothScrollToPosition(headerPosition[tab.text]!!)
                        }
                    }
                } else {
                    ManualScrollInProgress = false
                }
                previousTabPosition = tab.position
                updateTabsText(tab)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                initTabsText()
                if (!ManualScrollInProgress) {
                    AutomaticScrollInProgress = true
                    if (tab.text == getCurrentDay()) {
                        val index = orderedEvents.indexOfFirst({it.getStartingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                        if (index < mLayoutManager.findFirstVisibleItemPosition()) {
                            recycleView.smoothScrollToPosition(index)
                        } else {
                            recycleView.smoothScrollToPosition(index+4)
                        }
                    } else {
                        val index = headerPosition[tab.text]
                        if (index!! < mLayoutManager.findFirstVisibleItemPosition()) {
                            recycleView.smoothScrollToPosition(index)
                        } else {
                            recycleView.smoothScrollToPosition(index+4)
                        }
                    }
                } else {
                    ManualScrollInProgress = false
                }
                previousTabPosition = tab.position
                updateTabsText(tab)
            }
        })
        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateEventAdapter()
            }
        }
        //Register the broadcast receiver to receive TIME_TICK
        activity.registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        return view
    }

    private fun initTabsText() {
        for (day in days) {
            tabBar.getTabAt(days.indexOf(day))!!.text = day
        }
    }

    private fun updateTabsText(tab: TabLayout.Tab) {
        val sb = StringBuilder()
        sb.append(tabBar.getTabAt(tab.position)!!.text)
        sb.append(daysNumber[tabBar.getTabAt(tab.position)!!.text])
        tabBar.getTabAt(tab.position)!!.text = sb
    }

    private fun updateEventAdapter() {
        recycleView.adapter.notifyDataSetChanged()
    }

    private fun getCurrentDay(): String {
        val current = System.currentTimeMillis()
        val date = Timestamp(current).toString()
        return convertDay(date)
    }

    private fun convertDay(date: String): String {
        val endTrunc = date.indexOf(" ")
        val day = date.substring(0, endTrunc)
        return when (day) {
            "2018-04-04" -> "Mercredi"
            "2018-04-05" -> "Jeudi"
            "2018-04-06" -> "Vendredi"
            "2018-04-07" -> "Samedi"
            "2018-04-08" -> "Dimanche"
            else -> "None"
        }
    }

}// Required empty public constructor
