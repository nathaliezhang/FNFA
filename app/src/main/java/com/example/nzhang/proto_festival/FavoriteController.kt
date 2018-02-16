package com.example.nzhang.proto_festival

import kotlin.reflect.jvm.internal.impl.javax.inject.Singleton

/**
 * Created by nathalie on 16/02/2018.
 */


@Singleton
class FavoriteController() {

    private val favorites: MutableList<Int> = mutableListOf()

    fun setFavorite(id: Int) {

        if (favorites.contains(id)) {
            favorites.remove(id)
        } else {
            favorites.add(id)
        }
    }

    fun getFavorites(): MutableList<Int> {
        return favorites
    }

}
