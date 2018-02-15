package com.example.nzhang.proto_festival

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import java.text.SimpleDateFormat
import java.util.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter

class EventAdapter(
        private val headerPosition: List<Int>,
        private val events: List<Any>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_HEADER: Int = 0
    private val TYPE_ITEM: Int = 1
    private var mExpandedPosition: Int = -1
    private var previousExpandedPosition: Int = -1

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
            val placeSb = StringBuilder()
            val categorySb = StringBuilder()

            holder.titleView.text = event.name
            holder.timeView.text = event.getStartingHour()
            holder.durationView.text = event.getTimeDurationHour()
            holder.descriptionView.text = event.description
            holder.itemView.alpha = if (event.getEndingDate().time >= System.currentTimeMillis()) 1.0f else 0.5f

            val isExpanded = position == mExpandedPosition
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded)
                previousExpandedPosition = position

            holder.itemView.setOnClickListener {
                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(previousExpandedPosition)
                notifyItemChanged(position) 
            }

            event.placeIds.forEach({
                id -> val name = places[places.indexOfFirst({it.id == id.toString()})].name
                if (event.placeIds.indexOf(id) == event.placeIds.size - 1 ){
                    placeSb.append(name)
                } else {
                    placeSb.append(name + " / ")
                }
            })
            holder.placeView.text = placeSb.toString()

            event.categoryIds.forEach({
                id -> val name = categories[categories.indexOfFirst({it.id == id.toString()})].name
                if (event.categoryIds.indexOf(id) == event.categoryIds.size - 1 ){
                    placeSb.append(name)
                } else {
                    placeSb.append(name + " / ")
                }
            })
            holder.categoryView.text = categorySb.toString()

            holder.imageButton.setOnClickListener({
                println(event.name)
            })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position in headerPosition) TYPE_HEADER else TYPE_ITEM
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView = view.findViewById<TextView>(R.id.text_list_item_category)
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_list_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details)
        val group = view.findViewById<ConstraintLayout>(R.id.item_group)
        val descriptionView = view.findViewById<TextView>(R.id.text_list_item_description)
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTitleView = view.findViewById<TextView>(R.id.text_list_header_title)
    }
}
