package com.studentsapps.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.studentsapps.data.broadcasts.ScheduleAlarmReceiver
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    @ApplicationContext private val context: Context
) : ScheduleRepository {
    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean, showSunday: Boolean, startDate: LocalDate, endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
            showSaturday, showSunday, startDate, endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun getSchedulesForTimetableInListMode(date: LocalDate): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInListMode(date.dayOfWeek, date)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun registerSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int
    ) {
        scheduleLocalDataSource.insert(with(schedule) {
            ScheduleEntity(
                id, startTime, endTime, classPlace, dayOfWeek, specificDate, courseId
            )
        })
        scheduleAlarm(context, schedule.asScheduleDetails(specificDate, courseName, courseColor))
    }

    override suspend fun getScheduleDetailsById(scheduleId: Int): ScheduleDetails =
        scheduleLocalDataSource.getScheduleDetailsView(scheduleId).asExternalModel()

    override suspend fun updateSchedule(schedule: Schedule, specificDate: LocalDate?) {
        scheduleLocalDataSource.updateSchedule(with(schedule) {
            ScheduleEntity(
                id = id,
                startTime = startTime,
                endTime = endTime,
                classPlace = classPlace,
                dayOfWeek = dayOfWeek,
                specificDate = specificDate,
                courseId = courseId
            )
        })
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
        scheduleLocalDataSource.deleteSchedule(with(
            scheduleLocalDataSource.getScheduleDetailsView(
                scheduleId
            )
        ) {
            ScheduleEntity(
                id = scheduleId,
                startTime = startTime,
                endTime = endTime,
                classPlace = classPlace,
                dayOfWeek = dayOfWeek,
                specificDate = specificDate,
                courseId = courseId
            )
        })
    }

    private fun scheduleAlarm(context: Context, scheduleDetails: ScheduleDetails) {
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
            val nextOccurrence = today.plusDays(daysUntilNextOccurrence.toLong())
            nextOccurrence.atTime(scheduleDetails.startTime)
        }

        val triggerTime = dateTime.minusMinutes(10)
        return triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun Schedule.asScheduleDetails(
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int
    ) = ScheduleDetails(
        scheduleId = id,
        startTime = startTime,
        endTime = endTime,
        classPlace = classPlace,
        dayOfWeek = dayOfWeek,
        specificDate = specificDate,
        courseId = courseId,
        courseName = courseName,
        courseColor = courseColor
    )

}