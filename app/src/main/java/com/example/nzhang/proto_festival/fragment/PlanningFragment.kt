package com.example.nzhang.proto_festival.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.example.nzhang.proto_festival.EventAdapter
import com.example.nzhang.proto_festival.R
import com.example.nzhang.proto_festival.ScrollingLinearLayoutManager
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.sql.Timestamp


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlanningFragment : Fragment() {

    lateinit private var recycleView: RecyclerView
    lateinit private var tabBar: TabLayout
    lateinit private var orderedEvents: List<Events.Event>
    lateinit private var places: List<Places.Place>
    lateinit private var daysLimitIndex: Map<Int, String>
    lateinit private var headerPosition: Map<String, Int>
    lateinit private var eventsAndHeaders: List<Any>

    private var days = arrayListOf("Mercredi","Jeudi","Vendredi","Samedi","Dimanche")
    private var AutomaticScrollInProgress: Boolean = false
    private var ManualScrollInProgress: Boolean = false
    private var notUpdatingTab: Boolean = true
    private var previousTabPosition : Int = -1 //will change if not the first day...
    private var tickReceiver: BroadcastReceiver? = null
    private var isInit: Boolean = true
    private val daysNumber = hashMapOf("Mercredi" to " 4 Avril", "Jeudi" to " 5 Avril", "Vendredi" to " 6 Avril", "Samedi" to " 7 Avril", "Dimanche" to " 8 Avril")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load and parse JSON
        val eventResponse = parseEventLoadJson()
        val placeResponse = parsePlaceLoadJson()

        // Order by date and by name
        orderedEvents = eventResponse!!.events.sortedWith(compareBy({it.getStartingDate().time}, {it.name}))
        places = placeResponse!!.places

        val computedDaysLimitIndex = mutableMapOf<Int, String>()
        val computedFirstIndexOfDay = mutableMapOf<String, Int>()

        for (day in days) {
            computedDaysLimitIndex.put(orderedEvents.indexOfFirst({it.getDay() == day}), day)
            computedFirstIndexOfDay.put(day, orderedEvents.indexOfFirst({it.getDay() == day}))
            computedDaysLimitIndex.put(orderedEvents.indexOfLast({it.getDay() == day}), day)
        }

        daysLimitIndex = computedDaysLimitIndex


        val computedEventAndHeaders = mutableListOf<Any>()

        for (event in orderedEvents) {
            val position = orderedEvents.indexOf(event)
            for (day in days) {
                if (position == computedFirstIndexOfDay[day]) {
                    computedEventAndHeaders.add(day)
                }
            }
            computedEventAndHeaders.add(event)
        }
        eventsAndHeaders = computedEventAndHeaders
        println(eventsAndHeaders)

        val headerMapPosition = mutableMapOf<String, Int>()
        for (event in eventsAndHeaders) {
            var index = eventsAndHeaders.indexOf(event)
            when (event) {
                "Mercredi" -> headerMapPosition.put("Mercredi", index)
                "Jeudi" -> headerMapPosition.put("Jeudi", index)
                "Vendredi" -> headerMapPosition.put("Vendredi", index)
                "Samedi" -> headerMapPosition.put("Samedi", index)
                "Dimanche" -> headerMapPosition.put("Dimanche", index)
                else -> print("None")
            }
        }
        headerPosition = headerMapPosition
        ///println(headerPosition)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_planning, container, false)
        tabBar = view.findViewById(R.id.tab_bar)
        val mLayoutManager = ScrollingLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false, 100)


        recycleView = view.findViewById(R.id.container_list)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(eventsAndHeaders, places, headerPosition)


        val divideItemDecoration = DividerItemDecoration(recycleView.context, mLayoutManager.orientation)
        recycleView.addItemDecoration(divideItemDecoration)

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
        this.recycleView.adapter.notifyDataSetChanged()
    }

    private fun loadJsonFromAssets(filename: String) : String {
        val bytesStream: InputStream
        try {
            bytesStream = activity.baseContext.assets.open(filename)
            if (bytesStream != null) {
                val streamSize: Int = bytesStream.available() // Returns an estimate of the number of bytes that can be read
                val buffer: ByteArray = kotlin.ByteArray(streamSize)
                bytesStream.read(buffer)
                bytesStream.close()
                return String(buffer)
            }
        } catch (e: Exception) {
            Log.e("error",  e.toString())
        }
        return ""
    }

    private fun parseEventLoadJson() : Events? {
        val eventsJson = loadJsonFromAssets("events.json")
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Events::class.java)
        return adapter.fromJson(eventsJson)
    }
    private fun parsePlaceLoadJson() : Places? {
        val placesJson = loadJsonFromAssets("places.json")
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Places::class.java)
        return adapter.fromJson(placesJson)
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
} //Une demi heure


