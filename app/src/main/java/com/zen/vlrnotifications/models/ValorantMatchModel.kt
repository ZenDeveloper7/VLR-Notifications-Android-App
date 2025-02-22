package com.zen.vlrnotifications.models

import com.google.gson.Gson

data class ValorantMatchModel(
    val date: String,
    val event: String,
    val href: String,
    val status: String,
    val team1: String,
    val team2: String,
    val time: String
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun fromJson(jsonString: String): ValorantMatchModel {
        return Gson().fromJson(jsonString, ValorantMatchModel::class.java)
    }
}