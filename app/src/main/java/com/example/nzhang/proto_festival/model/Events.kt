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
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).parse(startingDate)
        }

        fun getEndingDate(): Date {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.FRANCE).parse(endingDate)
        }

        fun getTimeDurationHour(): String {
            return SimpleDateFormat("HH'h'mm'min'", Locale.FRANCE).format(this.getEndingDate().time - this.getStartingDate().time)
        }

        fun getFullStartingDate(): String { //ie : mercredi 4 avril
            return SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(getStartingDate()).toString()
        }

        fun getStartingHour(): String {
            return SimpleDateFormat("HH'h'mm", Locale.FRANCE).format(getStartingDate()).toString()
        }
    }
}

