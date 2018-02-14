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
        private val headerPosition: Map<String, Int>,
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
            val typeFormatShort = SimpleDateFormat("HH'h'mm", Locale.FRANCE)
            val typeFormatLong = SimpleDateFormat("HH'h'mm'min'", Locale.FRANCE)
            val placeSb = StringBuilder()
            val categorySb = StringBuilder()

            holder.titleView.text = event.name
            holder.timeView.text = typeFormatShort.format(event.getStartingDate())
            holder.durationView.text = typeFormatLong.format(event.getTimeDuration())
            holder.descriptionView.text = event.description

            val isExpanded = position == mExpandedPosition
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded)
                previousExpandedPosition = position

            holder.itemView.setOnClickListener {
                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(previousExpandedPosition)
                notifyItemChanged(position)
                holder.itemView.alpha = 1f
                holder.itemView.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                holder.itemView.animate().alpha(1f).setDuration(100).setListener(null)
                            }
                        })
            }

            for (i in event.placeIds.indices ) { // Event id in Events
                for (placeId: Places.Place in places) { // Places id
                    val convertPlaceId = placeId.id.toInt()
                    if (event.placeIds[i] == convertPlaceId) {
                        if (i == event.placeIds.size - 1) {
                            placeSb.append(placeId.name)
                        } else {
                            placeSb.append(placeId.name + " / ")
                        }
                    }
                }
                holder.placeView.text = placeSb.toString()
            }

            for (i in event.categoryIds.indices ) { // Category id in Categories
                for (categoryId: Categories.Category in categories) { // Category id
                    val convertCategoryId = categoryId.id.toInt()
                    if (event.categoryIds[i] == convertCategoryId) {
                        if (i == event.categoryIds.size - 1) {
                            categorySb.append(categoryId.name)
                        } else {
                            categorySb.append(categoryId.name + " / ")
                        }
                    }
                }
                holder.categoryView.text = categorySb.toString()
            }

            holder.imageButton.setOnClickListener({
                println(event.name)
            })

            if (event.getEndingDate().time >= System.currentTimeMillis()) {
                holder.itemView.alpha = 1.0f
            } else {
                holder.itemView.alpha = 0.5f
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (isHeader(position)) {
           return TYPE_HEADER
        } else {
            return TYPE_ITEM
        }
    }

    fun closeExpandedItem() {
        mExpandedPosition = -1
        previousExpandedPosition = -1
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
