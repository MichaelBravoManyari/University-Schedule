package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.studentsapps.model.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["course_id"]
        )
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
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
    val courseId: Int
)

fun ScheduleEntity.asExternalMode() = Schedule(
    id = id,
    startTime = startTime,
    endTime = endTime,
    classPlace = classPlace,
    dayOfWeek = dayOfWeek,
    courseId = courseId
)