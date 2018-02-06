package com.example.nzhang.proto_festival.model

/**
 * Created by nathalie on 05/02/2018.
 */

data class Places (val places: List<Place>) {
    data class Place (val id: String, val name: String) {

    }
}

