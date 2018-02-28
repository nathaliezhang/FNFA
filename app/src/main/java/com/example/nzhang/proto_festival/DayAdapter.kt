package com.example.nzhang.proto_festival

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places

class DayAdapter (
        private val activity: Activity,
        private val days: List<List<Events.Event>>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>,
        private val whichDayClickedInterface: WhichDayClickedInterface,
        private val filter: String?
) : RecyclerView.Adapter<DayAdapter.HeaderViewHolder>() {

    lateinit private var context: Context
    lateinit private var recycleView: RecyclerView
    private var mExpandedPosition: Int = -1
    val animation_show = RotateAnimation(0f, 90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

    override fun getItemCount(): Int = days.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_event_header, parent, false)
        return DayAdapter.HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val dayValue = days[position][0].getFullStartingDate()
        holder.dayTitleView.text = dayValue  //"mercredi 4 avril"
        val filteredList = when (filter) {
            "pro" -> days[position].filter { it.pro == 1 }
            "public" -> days[position].filter { it.pro == 0 }
            else -> days[position]
        }
        val isEmpty = !filteredList.isNotEmpty()
        val isExpanded = position == mExpandedPosition

        if (isExpanded) {
            animation_show.interpolator = AccelerateDecelerateInterpolator()
            animation_show.repeatCount = 0
            animation_show.fillAfter = true
            animation_show.duration = 600
            holder.arrowView.startAnimation(animation_show)
        } else {

        }

        holder.dayTitleView.setOnClickListener{
            mExpandedPosition = if (mExpandedPosition != position) position else -1
            notifyItemChanged(position)
            whichDayClickedInterface.onDayClicked(position)
        }

        recycleView = holder.list
        val mLayoutManager = LinearLayoutManager(context)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(activity, filteredList, places, categories, isEmpty)
        recycleView.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTitleView = view.findViewById<TextView>(R.id.text_list_header_title)
        val arrowView = view.findViewById<ImageView>(R.id.arrow_list_header_title)
        val list = view.findViewById<RecyclerView>(R.id.container_list)
    }

    interface WhichDayClickedInterface {
        fun onDayClicked(number: Int)
    }
}
