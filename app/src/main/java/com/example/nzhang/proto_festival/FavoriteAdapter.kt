package com.example.nzhang.proto_festival

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

/**
 * Created by nathalie on 18/02/2018.
 */

class FavoriteAdapter(private val activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val favorites = FavoriteController(activity).getFavorites()

    override fun getItemCount(): Int = favorites.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)

        val view = layoutInflater.inflate(R.layout.item_favorite_row, parent, false)
        return FavoriteAdapter.FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val events = DataController(activity).orderedEvents
        //println(events)
        //println(favorites)
/*        for(id in events) {

        }*/
    }

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView = view.findViewById<TextView>(R.id.text_fav_item_category)
        val titleView = view.findViewById<TextView>(R.id.text_fav_item_title)

        val dateView = view.findViewById<TextView>(R.id.text_fav_item_date)

        val durationView = view.findViewById<TextView>(R.id.text_fav_item_duration)
        val placeView = view.findViewById<TextView>(R.id.text_fav_item_place)
        val imageButton = view.findViewById<ImageButton>(R.id.image_fav_item)

        val details = view.findViewById<ConstraintLayout>(R.id.fav_item_details)
        val group = view.findViewById<ConstraintLayout>(R.id.fav_item_group)
        val descriptionView = view.findViewById<TextView>(R.id.text_fav_item_description)
    }

}
