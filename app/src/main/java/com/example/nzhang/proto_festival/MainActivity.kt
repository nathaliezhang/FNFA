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
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Events
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
        val eventResponse = parseLoadJson()

        // Order by date and by name
        val orderedEvents = eventResponse!!.events.sortedWith(compareBy({it.getStartingDate().time}, {it.name}))

        // this.recycleView = findViewById(R.id.container_list)
        this.recycleView = findViewById(R.id.expandable_container_list)
        this.recycleView.layoutManager = mLayoutManager
        this.recycleView.adapter = ExpandableEventAdapter(mLayoutManager, orderedEvents)

        this.divideItemDecoration = DividerItemDecoration(this.recycleView.context, mLayoutManager.orientation)
        this.recycleView.addItemDecoration(divideItemDecoration)

    }

    private fun loadJsonFromAssets() : String {
        val bytesStream: InputStream
        try {
            bytesStream = baseContext.assets.open("events.json")
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

    private fun parseLoadJson() : Events? {
        val eventsJson = loadJsonFromAssets()
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Events::class.java)
        return adapter.fromJson(eventsJson)
    }

    fun putInFav(view: View) {
        println(view.parent.parent.parent)
    }
}

class ExpandableEventAdapter(
        layout : LinearLayoutManager,
        private val events: List<Events.Event>
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
        holder.titleView.text = event.name
        holder.timeView.text = typeFormatShort.format(event.getStartingDate())
        holder.durationView.text = typeFormatLong.format(event.getTimeDuration())
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
    }
}
