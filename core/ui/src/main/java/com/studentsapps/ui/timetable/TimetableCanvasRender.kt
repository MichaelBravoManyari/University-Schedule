package com.studentsapps.ui.timetable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import javax.inject.Inject

class TimetableCanvasRender @Inject constructor() {

    fun createTimetableBitmap(bitmapWith: Int, bitmapHeight: Int): Bitmap {
        return Bitmap.createBitmap(bitmapWith, bitmapHeight, Bitmap.Config.ARGB_8888)
    }

    fun drawGrid(
        canvas: Canvas,
        paintGrid: Paint,
        paintHalfHourLine: Paint,
        verticalLinesCoordinates: FloatArray,
        horizontalHourLinesCoordinates: FloatArray,
        halfHourHorizontalLinesCoordinates: FloatArray
    ) {
        canvas.apply {
            drawLines(verticalLinesCoordinates, paintGrid)
            drawLines(horizontalHourLinesCoordinates, paintGrid)
            drawLines(halfHourHorizontalLinesCoordinates, paintHalfHourLine)
        }
    }

    fun drawHoursText24HourFormat(
        canvas: Canvas,
        hoursText: List<String>,
        gridCellHeight: Int,
        hourTextPaint: Paint,
        xAxis: Float
    ) {
        val hourTextHeight = with(hourTextPaint) { descent() - ascent() }
        hoursText.forEachIndexed { index, hourText ->
            val yAxis = (gridCellHeight * (index + 1)) + (hourTextHeight / 3)
            canvas.drawText(hourText, xAxis, yAxis, hourTextPaint)
        }
    }

    fun drawHoursText12HourFormat(
        canvas: Canvas,
        hoursText: List<String>,
        gridCellHeight: Int,
        hourTextPaint: Paint,
        xAxis: Float
    ) {
        val hourTextHeight = with(hourTextPaint) { descent() - ascent() }
        hoursText.forEachIndexed { hourPosition, hourText ->
            val hourTextSplit = hourText.split(" ")
            hourTextSplit.forEachIndexed { textIndex, text ->
                val yAxis = (gridCellHeight * (hourPosition + 1)) + (hourTextHeight * textIndex)
                canvas.drawText(text, xAxis, yAxis, hourTextPaint)
            }
        }
    }

    fun getCurrentMonthDayBackground(viewWidth: Int, viewHeight: Int, @ColorInt circleColor: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val xAxis = viewWidth / 2f
        val yAxis = viewHeight / 2f
        val paint = Paint().apply {
            color = circleColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(xAxis, yAxis, yAxis, paint)
        return bitmap
    }

    fun getPaintForGridLines(@ColorInt lineColor: Int, strokeWidth: Float): Paint {
        return Paint().apply {
            color = lineColor
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
    }

    fun getPaintForHoursText(
        @ColorInt textColor: Int,
        @Dimension textSize: Float,
        typeface: Typeface
    ): Paint {
        return Paint().apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            this.textSize = textSize
            this.typeface = typeface
        }
    }
}