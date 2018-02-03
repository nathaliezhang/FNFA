package com.example.nzhang.proto_festival

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import com.hendraanggrian.widget.ExpandableRecyclerView
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit private var recycleView: RecyclerView
    lateinit private var divideItemDecoration: DividerItemDecoration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mLayoutManager = LinearLayoutManager(this)

        // Load and parse JSON
        val eventResponse = parseEventLoadJson()
        val placeResponse = parsePlaceLoadJson()

        // Order by date and by name
        val orderedEvents = eventResponse!!.events.sortedWith(compareBy({it.getStartingDate().time}, {it.name}))
        val places = placeResponse!!.places

        // this.recycleView = findViewById(R.id.container_list)
        this.recycleView = findViewById(R.id.expandable_container_list)
        this.recycleView.layoutManager = mLayoutManager
        this.recycleView.adapter = ExpandableEventAdapter(mLayoutManager, orderedEvents, places)

        this.divideItemDecoration = DividerItemDecoration(this.recycleView.context, mLayoutManager.orientation)
        this.recycleView.addItemDecoration(divideItemDecoration)

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

    fun putInFav(view: View) {
        println(view.parent.parent.parent)
    }
}

class ExpandableEventAdapter(
        layout : LinearLayoutManager,
        private val events: List<Events.Event>,
        private val places: List<Places.Place>
) : ExpandableRecyclerView.Adapter<ExpandableEventAdapter.EventViewHolder>(layout){

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : EventViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_event, parent, false)
        return ExpandableEventAdapter.EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val event = events[position]
        val typeFormatShort = SimpleDateFormat("HH'H'mm", Locale.FRANCE)
        val typeFormatLong = SimpleDateFormat("HH'H'mm'min'", Locale.FRANCE)
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
                        sb.append(placeId.name + " & ")
                    }
                }
            }
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
        val imageButton = view.findViewById<ImageButton>(R.id.img_list_item_heart)
    }
}
