package com.studentsapps.database.test.data

import com.studentsapps.database.model.ScheduleDetailsView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

val scheduleDetailsList = mutableListOf(
    createTestScheduleDetailsView(
        id = 1,
        dayOfWeek = DayOfWeek.MONDAY,
        courseName = "Math",
        courseId = 1
    ),
    createTestScheduleDetailsView(
        id = 2,
        dayOfWeek = DayOfWeek.TUESDAY,
        courseName = "Math1",
        courseId = 2
    ),
    createTestScheduleDetailsView(
        id = 3,
        dayOfWeek = DayOfWeek.WEDNESDAY,
        courseName = "Math2",
        courseId = 3
    ),
    createTestScheduleDetailsView(
        id = 4,
        dayOfWeek = DayOfWeek.THURSDAY,
        courseName = "Math3",
        courseId = 4
    ),
    createTestScheduleDetailsView(
        id = 5,
        dayOfWeek = DayOfWeek.FRIDAY,
        courseName = "Math4",
        courseId = 5
    ),
    createTestScheduleDetailsView(
        id = 6,
        dayOfWeek = DayOfWeek.SATURDAY,
        courseName = "Math5",
        courseId = 6
    ),
    createTestScheduleDetailsView(
        id = 7,
        dayOfWeek = DayOfWeek.SUNDAY,
        courseName = "Math6",
        courseId = 7
    ),
    createTestScheduleDetailsView(
        id = 8,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20), courseName = "Math7", courseId = 8
    ),
    createTestScheduleDetailsView(
        id = 9,
        dayOfWeek = DayOfWeek.MONDAY,
        specificDate = LocalDate.of(2023, 11, 20), courseName = "Math8", courseId = 9
    ),
    createTestScheduleDetailsView(
        id = 10,
        dayOfWeek = DayOfWeek.THURSDAY,
        specificDate = LocalDate.of(2023, 11, 23), courseName = "Math9", courseId = 10
    ),
    createTestScheduleDetailsView(
        id = 11,
        dayOfWeek = DayOfWeek.SUNDAY,
        specificDate = LocalDate.of(2023, 11, 19), courseName = "Math10", courseId = 11
    ),
)

private fun createTestScheduleDetailsView(
    id: Int,
    dayOfWeek: DayOfWeek,
    specificDate: LocalDate? = null,
    courseName: String = "Math",
    courseId: Int
) =
    ScheduleDetailsView(
        scheduleId = id,
        startTime = LocalTime.of(14, 5),
        endTime = LocalTime.of(15, 5),
        classPlace = null,
        dayOfWeek = dayOfWeek,
        courseId = courseId,
        specificDate = specificDate,
        courseName = courseName,
        courseColor = 1234,
    )