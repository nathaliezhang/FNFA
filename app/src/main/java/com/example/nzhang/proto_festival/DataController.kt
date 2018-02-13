package com.example.nzhang.proto_festival

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.nzhang.proto_festival.model.Events
import com.example.nzhang.proto_festival.model.Places
import com.squareup.moshi.Moshi
import java.io.InputStream

/**
 * Created by mel on 13/02/2018.
 */

class DataController(activity: Activity) {

    private var days = arrayListOf("Mercredi","Jeudi","Vendredi","Samedi","Dimanche")
    // Load and parse JSON
    private val eventResponse = parseEventLoadJson(activity)
    private val placeResponse = parsePlaceLoadJson(activity)

    // Order by date and by name
    private val orderedEvents = eventResponse!!.events.sortedWith(compareBy({it.getStartingDate().time}, {it.name}))
    val publicEvents: List<Events.Event> = orderedEvents.filter({it.pro == 0})
    val proEvents: List<Events.Event> = orderedEvents.filter({it.pro == 1})
    val places: List<Places.Place> = placeResponse!!.places

    fun getDaysLimitIndex(listEvents: List<Events.Event>): Map<Int, String> {
        val computedDaysLimitIndex = mutableMapOf<Int, String>()
        for (day in days) {
            computedDaysLimitIndex.put(listEvents.indexOfFirst({it.getDay() == day}), day)
            computedDaysLimitIndex.put(listEvents.indexOfLast({it.getDay() == day}), day)
        }
        return computedDaysLimitIndex
    } // publicdayslimitindex = DataController.getDaysLimitIndex(DataController.publicEvents)

    fun getAllEvents(listEvents: List<Events.Event>): List<Any> {
        val computedFirstIndexOfDay = mutableMapOf<String, Int>()
        for (day in days) {
            computedFirstIndexOfDay.put(day, listEvents.indexOfFirst({it.getDay() == day}))
        }

        val computedEventAndHeaders = mutableListOf<Any>()
        for (event in listEvents) {
            val position = listEvents.indexOf(event)
            for (day in days) {
                if (position == computedFirstIndexOfDay[day]) {
                    computedEventAndHeaders.add(day)
                }
            }
            computedEventAndHeaders.add(event)
        }

       return computedEventAndHeaders
    } // = allproEvents

    fun getHeadersPosition(list: List<Any>): Map<String, Int> {
        val headerMapPosition = mutableMapOf<String, Int>()
        for (event in list) {
            val index = list.indexOf(event)
            when (event) {
                "Mercredi" -> headerMapPosition.put("Mercredi", index)
                "Jeudi" -> headerMapPosition.put("Jeudi", index)
                "Vendredi" -> headerMapPosition.put("Vendredi", index)
                "Samedi" -> headerMapPosition.put("Samedi", index)
                "Dimanche" -> headerMapPosition.put("Dimanche", index)
                else -> print("None")
            }
        }
        return headerMapPosition
    } // DataController.getHeadersPosition(allproEvents)

    private fun loadJsonFromAssets(filename: String, activity: Activity) : String {
        val bytesStream: InputStream
        try {
            bytesStream = activity.baseContext.assets.open(filename)
            if (bytesStream != null) {
                val streamSize: Int = bytesStream.available() // Returns an estimate of the number of bytes that can be read
                val buffer: ByteArray = kotlin.ByteArray(streamSize)
                bytesStream.read(buffer)
                bytesStream.close()
                return String(buffer)
            }
        } catch (e: Exception) {
            Log.e("error",  e.toString())
        }
        return ""
    }

    private fun parseEventLoadJson(activity: Activity) : Events? {
        val eventsJson = loadJsonFromAssets("events.json", activity)
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Events::class.java)
        return adapter.fromJson(eventsJson)
    }
    private fun parsePlaceLoadJson(activity: Activity) : Places? {
        val placesJson = loadJsonFromAssets("places.json", activity)
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Places::class.java)
        return adapter.fromJson(placesJson)
    }
}
