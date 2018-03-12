package com.example.nzhang.proto_festival.fragment

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nzhang.proto_festival.FavoriteAdapter
import com.example.nzhang.proto_festival.MainActivity
import com.example.nzhang.proto_festival.R
import com.example.nzhang.proto_festival.model.Categories
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places

class FavoriteFragment : Fragment(), FavoriteAdapter.EmptyFavoriteInterface {

    lateinit private var preferences: SharedPreferences
    lateinit private var favorites: MutableMap<String, *>
    lateinit private var recycleView: RecyclerView

    lateinit private var mainActivity: MainActivity
    lateinit private var places: List<Places.Place>
    lateinit private var categories: List<Categories.Category>
    lateinit private var finalItemsList: List<Events.Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = activity as MainActivity
        places = mainActivity.places
        categories = mainActivity.categories
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        recycleView = view.findViewById(R.id.favorite_list)
        val layout = LinearLayoutManager(context)
        recycleView.layoutManager = layout

        updateFavorite()

        recycleView.adapter = FavoriteAdapter(mainActivity, finalItemsList, places, categories, this)
        return view
    }

    private fun updateFavorite() {
        preferences = context.getSharedPreferences("favorites", 0)
        favorites = preferences.all
        finalItemsList = mainActivity.orderedList.filter { it.id in favorites }
    }

    override fun onEmptyFavorite() {
        updateFavorite()
        recycleView.adapter = FavoriteAdapter(mainActivity, finalItemsList, places, categories, this)
    }
}
