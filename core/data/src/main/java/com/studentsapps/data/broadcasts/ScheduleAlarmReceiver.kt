package com.studentsapps.data.broadcasts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.studentsapps.data.R

class ScheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("scheduleId", -1)
        val courseName = intent.getStringExtra("courseName") ?: "Class"
        val courseColor = intent.getIntExtra("courseColor", Color.BLUE)

        showNotification(context, scheduleId, courseName, courseColor)
    }

    private fun showNotification(context: Context, scheduleId: Int, courseName: String, courseColor: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "schedule_channel"
        val channelName = "Schedule Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Notifications for schedule"
            channel.enableLights(true)
            channel.lightColor = courseColor
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.class_reminder))
            .setContentText(context.getString(R.string.class_is_about_begin, courseName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(courseColor)
            .setAutoCancel(true)

        notificationManager.notify(scheduleId, notificationBuilder.build())
    }
}
