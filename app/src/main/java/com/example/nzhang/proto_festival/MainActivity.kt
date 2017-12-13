package com.example.nzhang.proto_festival

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.nzhang.proto_festival.model.Event


class MainActivity : AppCompatActivity() {

    lateinit var recycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.recycleView = findViewById(R.id.container_list)
        this.recycleView.layoutManager = LinearLayoutManager(this)
        this.recycleView.adapter = EventAdapter()

    }
}

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val events: List<Event> = listOf(
            Event("Séance scolaire","Mutafukaz"),
            Event("Atelier secret Fab", "Rencontre avec les réalisateurs")
    )

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : EventViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.categoryView.text = event.category
        holder.titleView.text = event.title
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView = view.findViewById<TextView>(R.id.text_list_item_category)
        val titleView = view.findViewById<TextView>(R.id.text_list_item_title)

    }

}

