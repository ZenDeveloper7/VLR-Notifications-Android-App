package com.zen.vlrnotifications.helpers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.zen.vlrnotifications.models.ValorantMatchModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationScheduler(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(valorantMatchModel: ValorantMatchModel) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dateFormat = SimpleDateFormat("EEE, MMMM dd, yyyy hh:mm a", Locale.ENGLISH)
        val date: Date =
            dateFormat.parse("${valorantMatchModel.date} ${valorantMatchModel.time}")
                ?: return

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("model", valorantMatchModel.toJson())
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(
            "",
            "Scheduling notification ${valorantMatchModel.team1} vs ${valorantMatchModel.team2}"
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the notification here
    }
}