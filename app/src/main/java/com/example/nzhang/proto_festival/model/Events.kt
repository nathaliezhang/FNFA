package com.example.nzhang.proto_festival.model

import android.annotation.TargetApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * Created by nathalie on 14/12/2017.
 */

//@TargetApi(26)
data class Events(val events: List<Event>) {
    data class Event(val id: String, val name: String, val startingDate: String, val endingDate: String) {
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
    }
}

