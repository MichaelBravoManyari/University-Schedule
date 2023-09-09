package com.studentsapps.schedule

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.junit.Assert.assertThrows

@Config(sdk = [33])
@RunWith(AndroidJUnit4::class)
class TimetableCanvasRenderTest {

    private val canvasRender = TimetableCanvasRender()

    @Test
    fun createTimetableBitmap_500w900h_returnBitmap() {
        val bitmapHeight = 900
        val bitmapWith = 500
        val bitmapConfig = Bitmap.Config.ARGB_8888
        val realTimetableBitmap = canvasRender.createTimetableBitmap(
            bitmapWith,
            bitmapHeight
        )
        assertThat(realTimetableBitmap.width, `is`(bitmapWith))
        assertThat(realTimetableBitmap.height, `is`(bitmapHeight))
        assertThat(realTimetableBitmap.config, `is`(bitmapConfig))
    }

    @Test
    fun `createTimetableBitmap_0w-100h_returnException`() {
        assertThrows(Exception::class.java) {
            canvasRender.createTimetableBitmap(0, -100)
        }
    }

    @Test
    fun verifyDrawGrid() {
        val canvas = mockk<Canvas>(relaxed = true)
        val gridPaint = Paint()
        val paintHalfHourLine = Paint()
        val verticalLinesCoordinates = floatArrayOf()
        val horizontalHourLinesCoordinates = floatArrayOf()
        val halfHourHorizontalLinesCoordinates = floatArrayOf()
        canvasRender.drawGrid(
            canvas,
            gridPaint,
            paintHalfHourLine,
            verticalLinesCoordinates,
            horizontalHourLinesCoordinates,
            halfHourHorizontalLinesCoordinates
        )
        verify { canvas.drawLines(verticalLinesCoordinates, gridPaint) }
        verify { canvas.drawLines(horizontalHourLinesCoordinates, gridPaint) }
        verify { canvas.drawLines(halfHourHorizontalLinesCoordinates, paintHalfHourLine) }
    }

    @Test
    fun verifyDrawHoursText24HourFormat() {
        val canvas = mockk<Canvas>(relaxed = true)
        val hoursText = listOf("1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00")
        val xAxis = 20f
        val gridCellHeight = 50
        val paint = Paint().apply {
            textSize = 10f
        }
        val hourTextHeight = paint.descent() - paint.ascent()
        canvasRender.drawHoursText24HourFormat(canvas, hoursText, gridCellHeight, paint, xAxis)
        hoursText.forEachIndexed { index, hourText ->
            val yAxis = (gridCellHeight * (index + 1)) + (hourTextHeight / 3)
            verify { canvas.drawText(hourText, xAxis, yAxis, any()) }
        }
    }

    @Test
    fun getPaintForGridLines_black3f_paint() {
        val lineColor = Color.BLACK
        val strokeWidth = 3f
        val realPaint = canvasRender.getPaintForGridLines(lineColor, strokeWidth)
        assertThat(realPaint.color, `is`(lineColor))
        assertThat(realPaint.style, `is`(Paint.Style.STROKE))
        assertThat(realPaint.strokeWidth, `is`(strokeWidth))
    }
}