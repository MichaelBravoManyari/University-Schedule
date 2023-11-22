package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.studentsapps.model.ScheduleDetails
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@DatabaseView(
    value = """
        SELECT s.id as schedule_id, s.start_time, s.end_time, s.class_place, s.day_of_week, 
        s.course_id, c.name as course_name, s.specific_date, c.color as course_color
        FROM schedules s INNER JOIN courses c 
        ON s.course_id = course_id
    """,
    viewName = "schedule_details"
)
data class ScheduleDetailsView(
    @ColumnInfo(name = "schedule_id")
    val scheduleId: Int,
    @ColumnInfo(name = "start_time")
    val startTime: LocalTime,
    @ColumnInfo(name = "end_time")
    val endTime: LocalTime,
    @ColumnInfo(name = "class_place")
    val classPlace: String?,
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: DayOfWeek,
    @ColumnInfo(name = "specific_date")
    val specificDate: LocalDate?,
    @ColumnInfo(name = "course_id")
    val courseId: Int,
    @ColumnInfo(name = "course_name")
    val courseName: String,
    @ColumnInfo(name = "course_color")
    val courseColor: Int,
)

fun ScheduleDetailsView.asExternalModel() = ScheduleDetails(
    scheduleId = scheduleId,
    startTime = startTime,
    endTime = endTime,
    classPlace = classPlace,
    dayOfWeek = dayOfWeek,
    specificDate = specificDate,
    courseId = courseId,
    courseName = courseName,
    courseColor = courseColor,
)
