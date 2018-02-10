package com.example.nzhang.proto_festival

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.ViewTreeObserver
import java.sql.Timestamp


class MainActivity : AppCompatActivity() {

    lateinit private var recycleView: RecyclerView
    lateinit private var divideItemDecoration: DividerItemDecoration
    lateinit private var tabBar: TabLayout
    private var noSelectedTab: Boolean = true
    private var notUpdatingTab: Boolean = true
    private var previousTabPosition : Int = 0 //will change if not the first day...
    private var tickReceiver: BroadcastReceiver? = null
    private var isInit: Boolean = true
    private val daysNumber = hashMapOf("Mercredi" to " 4 Avril", "Jeudi" to " 5 Avril", "Vendredi" to " 6 Avril", "Samedi" to " 7 Avril", "Dimanche" to " 8 Avril")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabBar = findViewById<TabLayout>(R.id.tab_bar)
        val mLayoutManager = ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, 100)

        // Load and parse JSON
        val eventResponse = parseEventLoadJson()
        val placeResponse = parsePlaceLoadJson()

        // Order by date and by name
        val orderedEvents = eventResponse!!.events.sortedWith(compareBy({it.getStartingDate().time}, {it.name}))

        val places = placeResponse!!.places
        val days = arrayListOf("Mercredi","Jeudi","Vendredi","Samedi","Dimanche")
        val positionalDays = mutableMapOf<Int, String>()
        val invertPosDays = mutableMapOf<String, Int>()

        this.recycleView = findViewById(R.id.container_list)
        for (day in days) {
            positionalDays.put(orderedEvents.indexOfFirst({it.getDay() == day}), day)
            invertPosDays.put(day, orderedEvents.indexOfFirst({it.getDay() == day}))
            positionalDays.put(orderedEvents.indexOfLast({it.getDay() == day}), day)
        }

        this.recycleView.layoutManager = mLayoutManager
        this.recycleView.adapter = EventAdapter(orderedEvents, places)

        this.divideItemDecoration = DividerItemDecoration(this.recycleView.context, mLayoutManager.orientation)
        this.recycleView.addItemDecoration(divideItemDecoration)

        this.recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                // Get the first visible item
                if (noSelectedTab) {
                    val firstVisibleItem: Int = mLayoutManager.findFirstVisibleItemPosition()
                    if (positionalDays.containsKey(firstVisibleItem)) {
                        if (tabBar.getTabAt(tabBar.selectedTabPosition)!!.text != positionalDays[firstVisibleItem]) {
                            val index = days.indexOf(positionalDays[firstVisibleItem])
                            val tab = tabBar.getTabAt(index)
                            notUpdatingTab = false
                            tab!!.select()
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noSelectedTab = true
                }
            }
        })

        this.tabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //do stuff here
                initTabsText()
                if (notUpdatingTab) {
                    noSelectedTab = false
                    if (previousTabPosition < tab.position) {
                        if (tab.text == getDay()) {
                            val index = orderedEvents.indexOfFirst({it.getEndingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                            this@MainActivity.recycleView.smoothScrollToPosition(index+4)
                        } else {
                            this@MainActivity.recycleView.smoothScrollToPosition(invertPosDays[tab.text]!!+4)
                        }
                    } else {
                        if (tab.text == getDay()) {
                            val index = orderedEvents.indexOfFirst({it.getEndingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                            this@MainActivity.recycleView.smoothScrollToPosition(index)
                        } else {
                            this@MainActivity.recycleView.smoothScrollToPosition(invertPosDays[tab.text]!!)
                        }
                    }
                    previousTabPosition = tab.position
                } else {
                    notUpdatingTab = true
                    previousTabPosition = tab.position
                }

                updateTabsText(tab)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                initTabsText()
                if (notUpdatingTab) {
                    noSelectedTab = false
                    if (tab.text == getDay()) {
                        val index = orderedEvents.indexOfFirst({it.getEndingDate().time >= System.currentTimeMillis() && it.getDay() == tab.text})
                        if (index < mLayoutManager.findFirstVisibleItemPosition()) {
                            this@MainActivity.recycleView.smoothScrollToPosition(index)
                        } else {
                            this@MainActivity.recycleView.smoothScrollToPosition(index+4)
                        }
                    } else {
                        val index = invertPosDays[tab.text]
                        if (index!! < mLayoutManager.findFirstVisibleItemPosition()) {
                            this@MainActivity.recycleView.smoothScrollToPosition(index)
                        } else {
                            this@MainActivity.recycleView.smoothScrollToPosition(index+4)
                        }
                    }
                    previousTabPosition = tab.position
                } else {
                    notUpdatingTab = true
                    previousTabPosition = tab.position
                }
                updateTabsText(tab)
            }
        })

        tickReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateExpandableAdapter()
            }
        }

        //Register the broadcast receiver to receive TIME_TICK
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        this.recycleView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (isInit) {
                        isInit = false
                        val index = days.indexOf(getDay())

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
    }

    private fun initTabsText() {
        tabBar.getTabAt(0)!!.text = "Mercredi"
        tabBar.getTabAt(1)!!.text = "Jeudi"
        tabBar.getTabAt(2)!!.text = "Vendredi"
        tabBar.getTabAt(3)!!.text = "Samedi"
        tabBar.getTabAt(4)!!.text = "Dimanche"
    }

    private fun updateTabsText(tab: TabLayout.Tab) {
        val sb = StringBuilder()
        sb.append(tabBar.getTabAt(tab.position)!!.text)
        sb.append(daysNumber[tabBar.getTabAt(tab.position)!!.text])
        tabBar.getTabAt(tab.position)!!.text = sb
    }

    private fun updateExpandableAdapter() {
        this.recycleView.adapter.notifyDataSetChanged()
    }

    private fun loadJsonFromAssets(filename: String) : String {
        val bytesStream: InputStream
        try {
            bytesStream = baseContext.assets.open(filename)
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

    private fun getDay(): String {
        val current = System.currentTimeMillis()
        val date = Timestamp(current).toString()
        return convertDay(date)
    }

    private fun convertDay(date: String): String {
        val endTrunc = date.indexOf(" ")
        val day = date.substring(0, endTrunc)
        when (day) {
            "2018-04-04" -> return "Mercredi"
            "2018-04-05" -> return "Jeudi"
            "2018-04-06" -> return "Vendredi"
            "2018-04-07" -> return "Samedi"
            "2018-04-08" -> return "Dimanche"
            else -> return "None"
        }
    }
}

class EventAdapter(
        private val events: List<Events.Event>,
        private val places: List<Places.Place>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : EventViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_event, parent, false)
        return EventAdapter.EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val typeFormatShort = SimpleDateFormat("HH'h'mm", Locale.FRANCE)
        val typeFormatLong = SimpleDateFormat("HH'h'mm'min'", Locale.FRANCE)
        val sb = StringBuilder()
        holder.titleView.text = event.name
        holder.timeView.text = typeFormatShort.format(event.getStartingDate())
        holder.durationView.text = typeFormatLong.format(event.getTimeDuration())

        for (i in event.placeIds.indices ) { // Event id in Events
            for (placeId: Places.Place in places) { // Places id
                val convertPlaceId = placeId.id.toInt()
                if (event.placeIds[i] == convertPlaceId) {
                    if (i == event.placeIds.size - 1) {
                        sb.append(placeId.name)
                    } else {
                        sb.append(placeId.name + " / ")
                    }
                }
            }
        }
        if (event.getEndingDate().time >= System.currentTimeMillis()) {
            holder.itemView.alpha = 1.0f
        } else {
            holder.itemView.alpha = 0.5f
        }
        holder.placeView.text = sb.toString()
        holder.imageButton.setOnClickListener({
            println(event.name)
        })
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_list_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
    }
}
