package com.studentsapps.schedule

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TimetableUtils @Inject constructor() {

    fun getDaysOfMonthOfWeek(
        isMondayFirstDayOfWeek: Boolean,
        date: LocalDate = LocalDate.now()
    ): List<String> {
        val formatter = DateTimeFormatter.ofPattern("d")
        val startOfWeek =
            if (isMondayFirstDayOfWeek) date.with(DayOfWeek.MONDAY) else date.with(DayOfWeek.MONDAY)
                .minusDays(1)
        return (0 until 7).map { startOfWeek.plusDays(it.toLong()).format(formatter).toString() }
    }

    fun getDaysOfWeekOrder(isMondayFirstDayOfWeek: Boolean): List<Int> {
        return if (isMondayFirstDayOfWeek) {
            listOf(
                R.string.monday_abbr,
                R.string.tuesday_abbr,
                R.string.wednesday_abbr,
                R.string.thursday_abbr,
                R.string.friday_abbr,
                R.string.saturday_abbr,
                R.string.sunday_abbr
            )
        } else {
            listOf(
                R.string.sunday_abbr,
                R.string.monday_abbr,
                R.string.tuesday_abbr,
                R.string.wednesday_abbr,
                R.string.thursday_abbr,
                R.string.friday_abbr,
                R.string.saturday_abbr
            )
        }
    }

    fun calculateRealRootViewWidth(width: Int, paddingLeft: Int, paddingRight: Int): Int {
        if (width <= 0) throw IllegalArgumentException("The root view cannot be 0 width.")
        return width - paddingLeft - paddingRight
    }

    fun calculateGridCellWidth(
        rootViewWidth: Int,
        hoursCellWidth: Int,
        columnsNumber: Int,
        columnsHourNumber: Int
    ): Int {
        return (rootViewWidth - hoursCellWidth) / (columnsNumber - columnsHourNumber)
    }

    fun calculateTimetableBitmapHeight(rowsNumber: Int, gridCellHeight: Int): Int {
        return rowsNumber * gridCellHeight
    }

    fun getVerticalLinesCoordinates(
        numLines: Int,
        hoursCellWidth: Int,
        gridCellWidth: Int,
        heightLine: Float
    ): FloatArray {
        val coordinates = mutableListOf<Float>()
        for (lineNumber in 1..numLines) {
            val xAxis = hoursCellWidth + (lineNumber * gridCellWidth).toFloat()
            val yAxisStart = 0f
            coordinates.addAll(arrayOf(xAxis, yAxisStart, xAxis, heightLine))
        }
        return coordinates.toFloatArray()
    }
}