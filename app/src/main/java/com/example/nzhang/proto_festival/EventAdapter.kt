package com.example.nzhang.proto_festival

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mel on 11/02/2018.
 */
class EventAdapter(
        private val events: List<Any>,
        private val places: List<Places.Place>,
        private val headerPosition: Map<String, Int>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_HEADER: Int = 0
    private val TYPE_ITEM: Int = 1

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)

        if (viewType == TYPE_HEADER) {
            val view = layoutInflater.inflate(R.layout.item_event_header, parent, false)
            return EventAdapter.HeaderViewHolder(view)
        } else if (viewType == TYPE_ITEM) {
            val view = layoutInflater.inflate(R.layout.item_event_row, parent, false)
            return EventAdapter.EventViewHolder(view)
        }
        throw RuntimeException("Not match type for" + viewType + ".")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HeaderViewHolder) {
            holder.dayTitleView.text = events[position] as String

        } else if (holder is EventViewHolder) {
            val event = events[position] as Events.Event
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

    }

    override fun getItemViewType(position: Int): Int {
        if (isHeader(position)) {
           return TYPE_HEADER
        } else {
            return TYPE_ITEM
        }
    }

    private fun isHeader(position: Int): Boolean {
        return when(position) {
            headerPosition["Mercredi"] -> true
            headerPosition["Jeudi"] -> true
            headerPosition["Vendredi"] -> true
            headerPosition["Samedi"] -> true
            headerPosition["Dimanche"] -> true
            else -> false
        }
        // return position == 0 // match only for 0 -> get the first position of each day
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_list_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTitleView = view.findViewById<TextView>(R.id.text_list_header_title)
    }
}
