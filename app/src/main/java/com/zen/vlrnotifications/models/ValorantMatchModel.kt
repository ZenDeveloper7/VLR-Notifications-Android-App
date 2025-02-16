package com.zen.vlrnotifications.models

data class ValorantMatchModel(
    val date: String,
    val event: String,
    val href: String,
    val status: String,
    val team1: String,
    val team2: String,
    val time: String
)