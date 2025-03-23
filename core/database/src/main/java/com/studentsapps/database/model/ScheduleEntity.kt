package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.studentsapps.model.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
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
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "course_id")
    val courseId: String
)

fun ScheduleEntity.asExternalModel() = Schedule(
    id = id,
    startTime = startTime,
    endTime = endTime,
    classPlace = classPlace,
    dayOfWeek = dayOfWeek,
    specificDate = specificDate,
    courseId = courseId
)