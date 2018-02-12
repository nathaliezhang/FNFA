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
