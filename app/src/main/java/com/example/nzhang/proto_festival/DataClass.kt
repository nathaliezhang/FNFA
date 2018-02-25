package com.example.nzhang.proto_festival

import com.example.nzhang.proto_festival.model.Events

/**
 * Created by mel on 15/02/2018.
 */
class DataClass(private val listEvent: List<Events.Event>) {

    val allDays = LinkedHashSet(listEvent.map({it.getFullStartingDate()})).toList()
    private val finalItemsList = getFinalItemsList()

    // complete list of headers and events
    fun getFinalItemsList(): List<Any> {
        val finalItemsList = mutableListOf<Any>()
        for (event in listEvent) {
            val position = listEvent.indexOf(event)
            for (day in allDays) {
                if (position == listEvent.indexOfFirst({it.getFullStartingDate() == day})){
                    finalItemsList.add(day)
                }
            }
            finalItemsList.add(event)
        }
        return finalItemsList
    }

    fun getHeaderPosition(): List<Int> {
        return allDays.map({day -> finalItemsList.indexOfFirst({it is String && it == day})})
    }

    private fun getDayName(day: String): String {
        return day.substring(0, day.indexOf(" "))
    }
}
