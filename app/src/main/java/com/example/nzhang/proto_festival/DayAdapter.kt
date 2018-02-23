package com.example.nzhang.proto_festival

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places

class DayAdapter (
        private val days: List<List<Events.Event>>,
        private val places: List<Places.Place>,
        private val categories: List<Categories.Category>,
        private val whichDayClickedInterface: WhichDayClickedInterface
) : RecyclerView.Adapter<DayAdapter.HeaderViewHolder>() {

    lateinit private var context: Context
    private var expandedDay: String = "none"

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

        holder.dayTitleView.setOnClickListener{
            expandedDay = if (expandedDay != dayValue) dayValue else "none"
            notifyItemChanged(position)
            whichDayClickedInterface.onDayClicked(position)
        }

        val recycleView = holder.list
        val mLayoutManager = LinearLayoutManager(context)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = EventAdapter(days[position], places, categories)
        recycleView.visibility = if (expandedDay == dayValue) View.VISIBLE else View.GONE
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTitleView = view.findViewById<TextView>(R.id.text_list_header_title)
        val list = view.findViewById<RecyclerView>(R.id.container_list)
    }

    interface WhichDayClickedInterface {
        fun onDayClicked(number: Int)
    }
}
