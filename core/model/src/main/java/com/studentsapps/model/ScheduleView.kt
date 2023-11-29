package com.studentsapps.model

import java.time.DayOfWeek
import java.time.LocalTime

data class ScheduleView(
    val id: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classPlace: String?,
    val dayOfWeek: DayOfWeek,
    val courseName: String,
    val color: Int
)

fun ScheduleDetails.asScheduleView() =
    ScheduleView(
        id = scheduleId,
        startTime = startTime,
        endTime = endTime,
        classPlace = classPlace,
        dayOfWeek = dayOfWeek,
        courseName = courseName,
        color = courseColor,
    )

fun List<ScheduleView>.groupByDayOfWeek(): Map<DayOfWeek, List<ScheduleView>> {
    return groupBy { it.dayOfWeek }
}


fun List<ScheduleView>.getUniqueSchedules(): List<ScheduleView> {
    if (size == 1) {
        return this
    }

    val uniqueSchedules = mutableListOf<ScheduleView>()
    val crossingSet = mutableSetOf<ScheduleView>()

    forEachIndexed { i, schedule ->
        var isUnique = false

        for (j in i + 1 until size) {
            val scheduleToCompare = this[j]

            if (schedule.isCrossingSchedules(scheduleToCompare)) {
                crossingSet.add(schedule)
                crossingSet.add(scheduleToCompare)
                isUnique = false
                break
            } else if (crossingSet.contains(schedule)) {
                isUnique = false
                break
            } else
                isUnique = true
        }

        if (isUnique) {
            uniqueSchedules.add(schedule)
        }
    }

    return uniqueSchedules
}

fun ScheduleView.isCrossingSchedules(scheduleToCompare: ScheduleView): Boolean =
    startTime < scheduleToCompare.endTime && endTime > scheduleToCompare.startTime

fun List<ScheduleView>.getCrossSchedules(): List<List<ScheduleView>> {
    if (size == 1)
        return emptyList()

    val result = mutableListOf<MutableList<ScheduleView>>()

    for (i in indices) {
        val schedule = this[i]
        if (result.none { it.contains(schedule) }) {
            val list = mutableListOf(schedule)
            for (j in i + 1 until size) {
                val scheduleToCompare = this[j]
                if (schedule.isCrossingSchedules(scheduleToCompare)) {
                    list.add(scheduleToCompare)
                }
            }
            result.add(list)
        }
    }

    result.forEach { list ->
        list.sortBy { it.startTime }
    }

    return result
}
