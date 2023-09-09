package com.studentsapps.schedule

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
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

    fun drawHoursText24HourFormat(canvas: Canvas, hoursText: List<String>, gridCellHeight: Int, hourTextPaint: Paint, xAxis: Float) {
        val hourTextHeight = with(hourTextPaint) { descent() - ascent() }
        hoursText.forEachIndexed { index, hourText ->
            val yAxis = (gridCellHeight * (index + 1)) + (hourTextHeight / 3)
            canvas.drawText(hourText, xAxis, yAxis, hourTextPaint)
        }
    }

    fun getPaintForGridLines(@ColorInt lineColor: Int, strokeWidth: Float): Paint {
        return Paint().apply {
            color = lineColor
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
    }
}