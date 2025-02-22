package com.zen.vlrnotifications.models

import com.google.gson.Gson
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

data class ValorantMatchModel(
    val date: String,
    val event: String,
    val series: String,
    val href: String,
    val status: String,
    val team1: String,
    val team2: String,
    val time: String
) {
    fun getISTTime(): String {
        return convertCSTtoIST("$date $time")
//        return "Sat, February 22, 2025 10:55 PM"
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun fromJson(jsonString: String): ValorantMatchModel {
        return Gson().fromJson(jsonString, ValorantMatchModel::class.java)
    }
}


fun convertCSTtoIST(cstDateTime: String, pattern: String = "EEE, MMMM d, yyyy h:mm a"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    val cstZone = ZoneId.of("America/Chicago")
    val localDateTime = LocalDateTime.parse(cstDateTime, formatter)
    val cstZonedDateTime = localDateTime.atZone(cstZone)

    val istZone = ZoneId.of("Asia/Kolkata")
    val istZonedDateTime = cstZonedDateTime.withZoneSameInstant(istZone)

    return istZonedDateTime.format(formatter)
}