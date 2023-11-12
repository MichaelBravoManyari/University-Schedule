package com.studentsapps.ui.timetable

import com.studentsapps.ui.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class TimetableUtils @Inject constructor() {

    fun getDaysOfMonthOfWeek(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean,
        date: LocalDate = LocalDate.now()
    ): List<LocalDate> {
        val startOfWeek =
            if (isMondayFirstDayOfWeek) date.with(DayOfWeek.MONDAY) else date.with(DayOfWeek.MONDAY)
                .minusDays(1)
        val daysOfMonthOfWeek =
            (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
                .toMutableList()

        if (!showSaturday) removeSaturdayFromDaysOfWeek(isMondayFirstDayOfWeek, daysOfMonthOfWeek)

        if (!showSunday) {
            if (isMondayFirstDayOfWeek) daysOfMonthOfWeek.removeLast() else daysOfMonthOfWeek.removeFirst()
        }

        return daysOfMonthOfWeek
    }

    private fun removeSaturdayFromDaysOfWeek(
        isMondayFirstDayOfWeek: Boolean,
        daysOfMonthOfWeek: MutableList<LocalDate>
    ) {
        val positionSaturday = if (isMondayFirstDayOfWeek) 5 else 6
        daysOfMonthOfWeek.removeAt(positionSaturday)
    }

    fun getCurrentMonthDay(): String {
        return LocalDate.now().dayOfMonth.toString()
    }

    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    fun getMonth(date: LocalDate = LocalDate.now()): String {
        return date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
    }

    fun getDaysOfWeekOrder(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean
    ): List<Int> {
        var orderList = mutableListOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr
        )

        if (showSaturday) orderList.add(R.string.saturday_abbr)

        if (showSunday) {
            if (isMondayFirstDayOfWeek)
                orderList.add(R.string.sunday_abbr)
            else {
                val baseOrderList = orderList
                orderList = mutableListOf(R.string.sunday_abbr)
                orderList.addAll(baseOrderList)
            }
        }

        return orderList
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

    fun getColumnsNumber(showSaturday: Boolean, showSunday: Boolean): Int {
        var columnsNumber = 8
        if (!showSaturday && !showSunday)
            columnsNumber -= 2
        else if (!showSaturday || !showSunday)
            columnsNumber -= 1
        return columnsNumber
    }

    fun getNumHorizontalGridLines(showSaturday: Boolean, showSunday: Boolean): Int {
        var numHorizontalGridLines = 6
        if (!showSaturday && !showSunday)
            numHorizontalGridLines -= 2
        else if (!showSaturday || !showSunday)
            numHorizontalGridLines -= 1
        return numHorizontalGridLines
    }

    fun calculateCrossScheduleViewWidth(
        gridCellWidth: Int,
        crossedSchedulesCount: Int,
        scheduleEndMargin: Int
    ): Int {
        return (gridCellWidth / crossedSchedulesCount) - scheduleEndMargin
    }

    fun calculateSingleScheduleViewWidth(gridCellWidth: Int, scheduleEndMargin: Int): Int {
        return gridCellWidth - scheduleEndMargin
    }

    fun calculateScheduleViewHeight(
        startTime: LocalTime,
        endTime: LocalTime,
        cellHeight: Int,
        scheduleBottomMargin: Int
    ): Int {
        val minutesDifference = ChronoUnit.MINUTES.between(startTime, endTime)
        val minutesInDecimals = convertMinutesToDecimals(minutesDifference)
        return (minutesInDecimals * cellHeight).toInt() - scheduleBottomMargin
    }

    fun calculateTopMarginScheduleView(startTime: LocalTime, cellHeight: Int): Int {
        val localTimeInDecimals = convertLocalTimeToDecimals(startTime)
        return (localTimeInDecimals * cellHeight).toInt()
    }

    fun calculateStartMarginSingleScheduleView(
        hoursCellWidth: Int,
        gridCellWidth: Int,
        day: DayOfWeek,
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean
    ): Int {
        return if (isMondayFirstDayOfWeek || !showSunday) {
            val offset = if (!showSaturday && day == DayOfWeek.SUNDAY) 1 else 0
            hoursCellWidth + (gridCellWidth * (day.value - 1 - offset))
        } else {
            if (day == DayOfWeek.SUNDAY) {
                hoursCellWidth
            } else
                hoursCellWidth + (gridCellWidth * day.value)
        }
    }

    fun calculateStartMarginCrossScheduleView(
        hoursCellWidth: Int,
        gridCellWidth: Int,
        day: DayOfWeek,
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean,
        crossedSchedulesCount: Int,
        crossScheduleIndex: Int
    ): Int {
        return calculateStartMarginSingleScheduleView(
            hoursCellWidth,
            gridCellWidth,
            day,
            isMondayFirstDayOfWeek,
            showSaturday,
            showSunday
        ) + ((gridCellWidth / crossedSchedulesCount) * crossScheduleIndex)
    }

    private fun convertMinutesToDecimals(minutes: Long): Double {
        return (minutes / 60) + ((minutes % 60) / 60.0)
    }

    private fun convertLocalTimeToDecimals(localTime: LocalTime): Double {
        return localTime.hour + (localTime.minute / 60.0)
    }
}