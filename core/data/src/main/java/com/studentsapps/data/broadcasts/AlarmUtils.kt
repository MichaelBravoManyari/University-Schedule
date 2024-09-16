package com.studentsapps.data.broadcasts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun scheduleAlarm(context: Context, scheduleDetails: ScheduleDetails) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
        putExtra("scheduleId", scheduleDetails.scheduleId)
        putExtra("courseName", scheduleDetails.courseName)
        putExtra("courseColor", scheduleDetails.courseColor)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        scheduleDetails.scheduleId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTimeMillis = calculateTriggerTimeMillis(scheduleDetails)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        return
    }

    if (scheduleDetails.specificDate != null) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
    } else {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }
}

private fun calculateTriggerTimeMillis(scheduleDetails: ScheduleDetails): Long {
    val specificDate = scheduleDetails.specificDate
    val dateTime = if (specificDate != null) {
        specificDate.atTime(scheduleDetails.startTime)
    } else {
        val today = LocalDate.now()
        val daysUntilNextOccurrence =
            (scheduleDetails.dayOfWeek.value - today.dayOfWeek.value + 7) % 7
        val nextOccurrence = if (daysUntilNextOccurrence == 0 && scheduleDetails.startTime.isBefore(
                LocalTime.now()
            )
        ) {
            today.plusDays(7)
        } else {
            today.plusDays(daysUntilNextOccurrence.toLong())
        }
        nextOccurrence.atTime(scheduleDetails.startTime)
    }

    val triggerTime = dateTime.minusMinutes(10)
    return triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun cancelAlarm(context: Context, scheduleDetails: ScheduleDetails) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ScheduleAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        scheduleDetails.scheduleId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

fun Schedule.asScheduleDetails(
    scheduleId: Int,
    specificDate: LocalDate?,
    courseName: String,
    courseColor: Int
) = ScheduleDetails(
    scheduleId = scheduleId,
    startTime = startTime,
    endTime = endTime,
    classPlace = classPlace,
    dayOfWeek = dayOfWeek,
    specificDate = specificDate,
    courseId = courseId,
    courseName = courseName,
    courseColor = courseColor
)