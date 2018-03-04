package com.example.nzhang.proto_festival

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import android.widget.ImageView
import kotlinx.android.synthetic.main.item_event_row.view.*
import java.sql.Date


class EventAdapter (
        private val activity: Activity,
        private val events: List<Events.Event>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>,
        private val isEmpty: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mExpandedPosition: Int = -1
    private var previousExpandedPosition: Int = -1
    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var favorites: MutableMap<String, *>
    lateinit var editor: SharedPreferences.Editor

    override fun getItemCount(): Int {
        return if(isEmpty) 1 else events.size
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
                val view = layoutInflater.inflate(R.layout.item_event_row, parent, false)
                EventAdapter.EventViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is EventViewHolder) {
            val event = events[position]

            if (event.getEndingDate() < getCurrentTime()) {
                holder.itemView.alpha = 0.5f
            }

            if (event.getStartingDate() < getCurrentTime() && event.getEndingDate() > getCurrentTime()) {
                holder.currentArrowView.visibility = View.VISIBLE
            }


            val placeSb = StringBuilder()
            val categorySb = StringBuilder()

            val startingHour = event.getStartingHour()
            val hour = startingHour.substringBefore("h").toInt()

            when(hour) {
                in 6..10 -> holder.imageTime.setImageResource(R.drawable.picto_temps_1)
                in 11..14 -> holder.imageTime.setImageResource(R.drawable.picto_temps_2)
                in 15..18 -> holder.imageTime.setImageResource(R.drawable.picto_temps_3)
                else -> holder.imageTime.setImageResource(R.drawable.picto_temps_4)
            }

            // Set tag
            when(hour) {
                in 6..10 -> holder.imageTime.tag = R.drawable.picto_temps_1
                in 11..14 -> holder.imageTime.tag = R.drawable.picto_temps_2
                in 15..18 -> holder.imageTime.tag = R.drawable.picto_temps_3
                else -> holder.imageTime.tag = R.drawable.picto_temps_4
            }

            holder.bgView.setBackgroundResource(R.color.white)
            holder.bgDetailView.setBackgroundResource(R.color.white)
            holder.titleView.text = event.name
            holder.timeView.text = event.getStartingHour()
            holder.durationView.text = event.getTimeDurationHour()
            holder.descriptionView.text = event.description
            holder.itemView.alpha = if (event.getEndingDate().time >= System.currentTimeMillis()) 1.0f else 0.5f

            val isExpanded = position == mExpandedPosition
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded) {
                previousExpandedPosition = position
                when (holder.itemView.img_time_list_item.tag) {
                    R.drawable.picto_temps_1 -> holder.imageTime.setImageResource(R.drawable.picto_temps_1_cliquey)
                    R.drawable.picto_temps_2 -> holder.imageTime.setImageResource(R.drawable.picto_temps_2_cliquey)
                    R.drawable.picto_temps_3 -> holder.imageTime.setImageResource(R.drawable.picto_temps_3_cliquey)
                    else -> holder.imageTime.setImageResource(R.drawable.picto_temps_4_cliquey)
                }
                holder.bgView.setBackgroundResource(R.color.green)
                holder.bgDetailView.setBackgroundResource(R.color.green)
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

            holder.linkView.setOnClickListener {
                browserFNFAPage()
            }

            event.categoryIds.forEach({
                id -> val name = categories[categories.indexOfFirst({it.id == id.toString()})].name
                if (event.categoryIds.indexOf(id) == event.categoryIds.size - 1 ){
                    categorySb.append(name)
                } else {
                    categorySb.append(name + " / ")
                }
            })
            holder.categoryView.text = categorySb.toString()

            if (event.id in favorites) {
                holder.imageButton.setImageResource(R.drawable.favorite_on)
            } else {
                holder.imageButton.setImageResource(R.drawable.favorite_off)
            }

            holder.imageButton.setOnClickListener({
                editor = preferences.edit()
                if (event.id in favorites) {
                    editor.remove(event.id)
                    holder.imageButton.setImageResource(R.drawable.favorite_off)
                }
                else {
                    editor.putBoolean(event.id, true)
                    holder.imageButton.setImageResource(R.drawable.favorite_on)
                }
                editor.apply()
            })
        }

    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bgView = view.findViewById<ImageView>(R.id.bg_list_item)
        val bgDetailView = view.findViewById<ImageView>(R.id.bg_list_item_detail)
        val currentArrowView = view.findViewById<ImageView>(R.id.current_list_item_arrow)
        val categoryView = view.findViewById<TextView>(R.id.text_list_item_category)
        val imageTime = view.findViewById<ImageView>(R.id.img_time_list_item)
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_list_item_place)
        val linkView = view.findViewById<TextView>(R.id.text_list_item_web)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details)
        val group = view.findViewById<ConstraintLayout>(R.id.item_group)
        val descriptionView = view.findViewById<TextView>(R.id.text_list_item_description)
    }

    fun getCurrentTime(): Date {
        val current = System.currentTimeMillis()
        return Date(current)
    }

    private fun browserFNFAPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://festival-film-animation.fr"))
        activity.startActivity(browserIntent)
    }

}
