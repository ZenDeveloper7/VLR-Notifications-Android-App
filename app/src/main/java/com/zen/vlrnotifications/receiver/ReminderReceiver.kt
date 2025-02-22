package com.zen.vlrnotifications.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.zen.vlrnotifications.MainActivity
import com.zen.vlrnotifications.R
import com.zen.vlrnotifications.models.ValorantMatchModel

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var matchModel: ValorantMatchModel?
        intent.getStringExtra("match")?.let {
            matchModel = Gson().fromJson(it, ValorantMatchModel::class.java)
            matchModel?.let { model ->
                //On tap of notification, I can do the following things
                // 1. Open the app
                // 2. Redirect to match details in chrome
                // 3. Open official stream
                // 4. Open watch parties

                val pendingIntent = Intent(context, MainActivity::class.java)
                val notificationBuilder = NotificationCompat.Builder(context, "0")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("${model.team1} vs ${model.team2}")
                    .setContentText("${model.event} | ${model.series} is going to start soon")
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            System.currentTimeMillis().toInt(),
                            pendingIntent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    )

                val appLogo = BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher
                )
                notificationBuilder.setLargeIcon(
                    appLogo
                ).setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(appLogo)
                )
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
            }
        }
    }
}