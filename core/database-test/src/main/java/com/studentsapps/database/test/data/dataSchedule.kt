package com.studentsapps.database.test.data

import com.studentsapps.database.model.ScheduleDetailsView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

val scheduleDetailsList = mutableListOf(
    createTestScheduleDetailsView(id = 1, dayOfWeek = DayOfWeek.MONDAY),
    createTestScheduleDetailsView(id = 2, dayOfWeek = DayOfWeek.TUESDAY),
    createTestScheduleDetailsView(id = 3, dayOfWeek = DayOfWeek.WEDNESDAY),
    createTestScheduleDetailsView(id = 4, dayOfWeek = DayOfWeek.THURSDAY),
    createTestScheduleDetailsView(id = 5, dayOfWeek = DayOfWeek.FRIDAY),
    createTestScheduleDetailsView(id = 6, dayOfWeek = DayOfWeek.SATURDAY),
    createTestScheduleDetailsView(id = 7, dayOfWeek = DayOfWeek.SUNDAY),
    createTestScheduleDetailsView(
        id = 8,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20)
    ),
    createTestScheduleDetailsView(
        id = 9,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20)
    ),
    createTestScheduleDetailsView(
        id = 10,
        dayOfWeek = DayOfWeek.THURSDAY,
        specificDate = LocalDate.of(2023, 11, 23)
    ),
    createTestScheduleDetailsView(
        id = 11,
        dayOfWeek = DayOfWeek.SUNDAY,
        specificDate = LocalDate.of(2023, 11, 19)
    ),
)

private fun createTestScheduleDetailsView(
    id: Int,
    dayOfWeek: DayOfWeek,
    specificDate: LocalDate? = null,
    courseName: String = "Math"
) =
    ScheduleDetailsView(
        scheduleId = id,
        startTime = LocalTime.of(14, 5),
        endTime = LocalTime.of(15, 5),
        classPlace = null,
        dayOfWeek = dayOfWeek,
        courseId = 1,
        specificDate = specificDate,
        courseName = courseName,
        courseColor = 1234,
    )