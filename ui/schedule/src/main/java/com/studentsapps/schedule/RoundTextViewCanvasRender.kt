package com.studentsapps.schedule

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import javax.inject.Inject

class RoundTextViewCanvasRender @Inject constructor() {

    fun drawCircleInMiddle(textViewWidth: Int, textViewHeight: Int, canvas: Canvas, @ColorInt circleColor: Int) {
        val paint = Paint().apply {
            color = circleColor
            style = Paint.Style.FILL
        }
        val xAxis = textViewWidth / 2f
        val yAxis = textViewHeight / 2f
        canvas.drawCircle(xAxis, yAxis, yAxis, paint)
    }
}