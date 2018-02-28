package com.example.nzhang.proto_festival

import android.content.Context
import android.content.SharedPreferences
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
import java.sql.Date

class FavoriteAdapter (
        events: List<Events.Event>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>,
        private val emptyFavoriteInterface: EmptyFavoriteInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mExpandedPosition: Int = -1
    private var previousExpandedPosition: Int = -1
    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var favorites: MutableMap<String, *>
    lateinit var editor: SharedPreferences.Editor
    private var isEmpty = false
    private var tempEvents = events.toMutableList()

    override fun getItemCount(): Int {
        return when (tempEvents.isNotEmpty()) {
            true -> {
                tempEvents.size
            }
            false -> {
                isEmpty = true
                1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)

        // Favorites
        preferences = context.getSharedPreferences("favorites", 0)
        favorites = preferences.all

        return when (isEmpty) {
            true -> {
                val view = layoutInflater.inflate(R.layout.item_event_row_null, parent, false)
                EmptyViewHolder(view)
            }
            false -> {
                val view = layoutInflater.inflate(R.layout.item_favorite_row, parent, false)
                FavoriteAdapter.EventViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is EventViewHolder) {
            val event = tempEvents[position]

            if (event.getEndingDate() < getCurrentTime()) {
                holder.itemView.alpha = 0.5f
            }

            val placeSb = StringBuilder()
            val categorySb = StringBuilder()

            holder.titleView.text = event.name
            holder.timeView.text = event.getStartingHour()
            holder.dateView.text = event.getFullStartingDate()
            holder.durationView.text = event.getTimeDurationHour()
            holder.descriptionView.text = event.description
            holder.itemView.alpha = if (event.getEndingDate().time >= System.currentTimeMillis()) 1.0f else 0.5f

            val isExpanded = position == mExpandedPosition
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded) {
                previousExpandedPosition = position
            }

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
                    categorySb.append(name)
                } else {
                    categorySb.append(name + " / ")
                }
            })
            holder.categoryView.text = categorySb.toString()
            holder.imageButton.setImageResource(R.drawable.favorite_on)

            holder.imageButton.setOnClickListener({
                editor = preferences.edit()
                editor.remove(event.id)
                editor.apply()
                tempEvents.remove(event)
                if (tempEvents.isNotEmpty())
                    notifyDataSetChanged()
                else
                    emptyFavoriteInterface.onEmptyFavorite()
            })
        }

    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView = view.findViewById<TextView>(R.id.text_fav_item_category)
        val titleView = view.findViewById<TextView>(R.id.text_fav_item_title)
        val dateView = view.findViewById<TextView>(R.id.text_fav_item_date)
        val timeView = view.findViewById<TextView>(R.id.text_fav_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_fav_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_fav_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.image_fav_item)
        val details = view.findViewById<ConstraintLayout>(R.id.fav_item_details)
        val group = view.findViewById<ConstraintLayout>(R.id.fav_item_group)
        val descriptionView = view.findViewById<TextView>(R.id.text_fav_item_description)
    }

    interface EmptyFavoriteInterface {
        fun onEmptyFavorite()
    }

    fun getCurrentTime(): Date {
        val current = System.currentTimeMillis()
        return Date(current)
    }

}
