package com.studentsapps.schedule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

internal class Timetable(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    init {
        inflateView()
    }

    private fun inflateView() {
        LayoutInflater.from(context).inflate(R.layout.timetable, this, true)
    }

    private fun drawGridAndHours(drawingContainer: ImageView) {
        //crear el bitmap con el dibujo de las horas y la rejilla
        val bitmap = createBitmapGridAndHours()
        // setear el bitmap de la image
        drawingContainer.setImageBitmap(bitmap)
    }

    private fun createBitmapGridAndHours(): Bitmap {
        val bitmap =
            Bitmap.createBitmap(width, 24 * 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val gridPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val paintHalfHourLine = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val paintText = Paint().apply {
            color = 333333
            textSize = 10f
            textAlign = Paint.Align.CENTER
            //typeface = this@Timetable.typeface
        }
        //val textHeight = paintText.descent() - paintText.ascent()
        //val dayHours = if (is12HoursFormat) hoursIn12HourFormat else hoursIn24HourFormat

        for (i in 2 until 24) {
            val x = (i - 1) * 100 + 100
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), canvas.height.toFloat(), gridPaint)
        }

        for (i in 1 until 7 * 2) {
            val y = (i * 100) / 2
            canvas.drawLine(
                100f,
                y.toFloat(),
                canvas.width.toFloat(),
                y.toFloat(),
                if (i % 2 != 0) paintHalfHourLine else gridPaint
            )
        }

        /*for ((index, hour) in dayHours.withIndex()) {
            val hourTextSplit = hour.split(" ")
            val y = (cellHeight * (index + 1)).toFloat()
            if (is12HoursFormat) {
                hourTextSplit.forEachIndexed { textIndex, text ->
                    canvas.drawText(
                        text,
                        widthOfHoursCell / 2f,
                        y + (textHeight * textIndex),
                        paintText
                    )
                }
            } else {
                canvas.drawText(
                    hour,
                    widthOfHoursCell / 2f,
                    y + (textHeight / 3),
                    paintText
                )
            }
        }*/

        return bitmap
    }
}