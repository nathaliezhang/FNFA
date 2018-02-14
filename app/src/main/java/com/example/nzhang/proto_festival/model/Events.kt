package com.example.nzhang.proto_festival.model

import android.annotation.TargetApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * Created by nathalie on 14/12/2017.
 */

data class Events(val events: List<Event>) {
    data class Event(val id: String, val name: String, val placeIds: List<Int>, val categoryIds: List<Int>, val startingDate: String, val endingDate: String, val pro: Int, val description: String) {
        fun getStartingDate(): Date {
            val typeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
            return typeFormat.parse(startingDate)
        }

        fun getEndingDate(): Date {
            val typeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.FRANCE)
            return typeFormat.parse(endingDate)
        }

        fun getTimeDuration(): Long {
            return this.getEndingDate().time - this.getStartingDate().time
        }

        fun getDay(): String {
            val startTrunc = startingDate.indexOf("-", 5)
            val endTrunc = startingDate.indexOf(" ")
            val day = startingDate.substring(startTrunc+1, endTrunc)
            when (day) {
                "04" -> return "Mercredi"
                "05" -> return "Jeudi"
                "06" -> return "Vendredi"
                "07" -> return "Samedi"
                else -> return "Dimanche"
            }
        }
    }
}

