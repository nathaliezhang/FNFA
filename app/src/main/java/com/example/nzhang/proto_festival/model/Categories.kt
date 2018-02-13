package com.example.nzhang.proto_festival.model

/**
 * Created by nathalie on 13/02/2018.
 */

data class Categories (val categories: List<Category>) {
    data class Category (val id: String, val name: String) {

    }
}
