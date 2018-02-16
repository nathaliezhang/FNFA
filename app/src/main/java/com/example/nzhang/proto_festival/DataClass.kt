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
                    finalItemsList.add(getDayName(day))
                }
            }
            finalItemsList.add(event)
        }
        return finalItemsList
    }

    fun getDaysLimits(): Map<Int, String> {
        val dayLimits = mutableMapOf<Int, String>()
        dayLimits.putAll(allDays.map({ day -> finalItemsList.indexOfFirst({it is String && it == day }) to day}))
        dayLimits.putAll(allDays.map({ day -> finalItemsList.indexOfLast({it is Events.Event && it.getFullStartingDate() == day}) to day }))
        return dayLimits
    }

    fun getHeaderPosition(): List<Int> {
        return allDays.map({day -> finalItemsList.indexOfFirst({it is String && it == getDayName(day)})})
    }

    private fun getDayName(day: String): String {
        return day.substring(0, day.indexOf(" "))
    }
}
