package com.studentsapps.database.test.data

import com.studentsapps.database.model.ScheduleDetailsView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

val scheduleDetailsList = mutableListOf(
    createTestScheduleDetails(id = 1, dayOfWeek = DayOfWeek.MONDAY),
    createTestScheduleDetails(id = 2, dayOfWeek = DayOfWeek.TUESDAY),
    createTestScheduleDetails(id = 3, dayOfWeek = DayOfWeek.WEDNESDAY),
    createTestScheduleDetails(id = 4, dayOfWeek = DayOfWeek.THURSDAY),
    createTestScheduleDetails(id = 5, dayOfWeek = DayOfWeek.FRIDAY),
    createTestScheduleDetails(id = 6, dayOfWeek = DayOfWeek.SATURDAY),
    createTestScheduleDetails(id = 7, dayOfWeek = DayOfWeek.SUNDAY),
    createTestScheduleDetails(
        id = 8,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20)
    ),
    createTestScheduleDetails(
        id = 9,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20)
    ),
    createTestScheduleDetails(
        id = 10,
        dayOfWeek = DayOfWeek.THURSDAY,
        specificDate = LocalDate.of(2023, 11, 23)
    ),
    createTestScheduleDetails(
        id = 11,
        dayOfWeek = DayOfWeek.SUNDAY,
        specificDate = LocalDate.of(2023, 11, 19)
    ),
)

private fun createTestScheduleDetails(
    id: Int,
    dayOfWeek: DayOfWeek,
    specificDate: LocalDate? = null
) =
    ScheduleDetailsView(
        scheduleId = id,
        startTime = LocalTime.of(14, 5),
        endTime = LocalTime.of(15, 5),
        classPlace = null,
        dayOfWeek = dayOfWeek,
        courseId = 1,
        specificDate = specificDate,
        courseName = "Math",
        courseColor = 1234,
    )