package com.zen.vlrnotifications.helper

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import com.zen.vlrnotifications.models.ValorantMatchModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

object NotificationScheduler {

    fun setReminder(context: Context, matchModel: ValorantMatchModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "0", "VLR-Match-Channel", NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                matchModel.hashCode(),
                Intent(),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val notifyWhen = dateTimeToMillis(matchModel.getISTTime())

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notifyWhen,
                    pendingIntent
                )
                Timber.d("Reminder set for ${notifyWhen}")
            } catch (e: Exception) {
            }
        }

    }

    private fun dateTimeToMillis(dateTime: String): Long {
        val formatter = DateTimeFormatter.ofPattern("EEE, MMMM d, yyyy h:mm a")
        val localDateTime = LocalDateTime.parse(dateTime, formatter)
        val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }
}