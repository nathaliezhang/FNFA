package com.example.nzhang.proto_festival

import android.app.Activity
import kotlin.reflect.jvm.internal.impl.javax.inject.Singleton

/**
 * Created by nathalie on 16/02/2018.
 */


@Singleton
class FavoriteController(activity: Activity) {

    private val preferences = activity.getPreferences(0).getStringSet("Events", mutableSetOf<String>())
    private val favorites = preferences

    fun setFavorite(id: String) {

        if (favorites.contains(id)) {
            favorites.remove(id)
        } else {
            favorites.add(id)
        }
    }

    fun getFavorites(): MutableSet<String> {
        return favorites
    }
}
