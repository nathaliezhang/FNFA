package com.example.nzhang.proto_festival

import android.content.Context
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


class EventAdapter (
        private val events: List<Events.Event>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>,
        private val filter: String?
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

    private var mExpandedPosition: Int = -1
    private var previousExpandedPosition: Int = -1
    lateinit var context: Context

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : EventViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_event_row, parent, false)
        return EventAdapter.EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val event = events[position]
        val params = holder.itemView.layoutParams
        val height = params.height

        if ( (filter == "pro" && event.pro == 0) || (filter == "public" && event.pro == 1)) {
            params.height = 0
        } else {
            params.height = height
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

        holder.imageButton.setOnClickListener({
            //println(event.id)
            holder.imageButton.setImageResource(R.drawable.favorite_on)
        })
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView = view.findViewById<TextView>(R.id.text_list_item_category)
        val imageTime = view.findViewById<ImageView>(R.id.img_time_list_item)
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)
        val timeView = view.findViewById<TextView>(R.id.text_list_item_time)
        val durationView = view.findViewById<TextView>(R.id.text_list_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_list_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details)
        val group = view.findViewById<ConstraintLayout>(R.id.item_group)
        val descriptionView = view.findViewById<TextView>(R.id.text_list_item_description)
    }


}
