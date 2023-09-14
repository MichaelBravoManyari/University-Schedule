package com.studentsapps.schedule.timetable

import com.studentsapps.schedule.R
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
        lineHeight: Float
    ): FloatArray {
        val coordinates = mutableListOf<Float>()
        for (lineNumber in 1..numLines) {
            val xAxis = hoursCellWidth + (lineNumber * gridCellWidth).toFloat()
            val yAxisStart = 0f
            coordinates.addAll(arrayOf(xAxis, yAxisStart, xAxis, lineHeight))
        }
        return coordinates.toFloatArray()
    }

    fun getHorizontalHourLinesCoordinates(
        numLines: Int,
        hoursCellWidth: Int,
        gridCellHeight: Int,
        lineLength: Float
    ): FloatArray {
        val coordinates = mutableListOf<Float>()
        for (lineNumber in 1..numLines) {
            val yAxis = lineNumber * gridCellHeight
            coordinates.addAll(
                arrayOf(
                    hoursCellWidth.toFloat(),
                    yAxis.toFloat(),
                    lineLength,
                    yAxis.toFloat()
                )
            )
        }
        return coordinates.toFloatArray()
    }

    fun getHalfHourHorizontalLinesCoordinates(
        numLines: Int,
        hoursCellWidth: Int,
        gridCellHeight: Int,
        lineLength: Float
    ): FloatArray {
        val coordinator = mutableListOf<Float>()
        for (lineNumber in 1..numLines) {
            val yAxis = ((lineNumber - 1) * gridCellHeight) + (gridCellHeight / 2f)
            coordinator.addAll(arrayOf(hoursCellWidth.toFloat(), yAxis, lineLength, yAxis))
        }
        return coordinator.toFloatArray()
    }
}