package com.studentsapps.schedule.timetable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.schedule.R
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
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
        verifySequence {
            canvas.drawLines(verticalLinesCoordinates, gridPaint)
            canvas.drawLines(horizontalHourLinesCoordinates, gridPaint)
            canvas.drawLines(halfHourHorizontalLinesCoordinates, paintHalfHourLine)
        }
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
            verify(exactly = 1) { canvas.drawText(hourText, xAxis, yAxis, any()) }
        }
    }

    @Test
    fun verifyDrawHoursText12HourFormat() {
        val canvas = mockk<Canvas>(relaxed = true)
        val hoursText = listOf("11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm")
        val gridCellHeight = 50
        val xAxis = 20f
        val paint = Paint().apply {
            textSize = 10f
        }
        val hourTextHeight = paint.descent() - paint.ascent()
        canvasRender.drawHoursText12HourFormat(canvas, hoursText, gridCellHeight, paint, xAxis)
        hoursText.forEachIndexed { hourPosition, hourText ->
            val hourTextSplit = hourText.split(" ")
            hourTextSplit.forEachIndexed { textIndex, text ->
                val yAxis = (gridCellHeight * (hourPosition + 1)) + (hourTextHeight * textIndex)
                verify(exactly = 1) { canvas.drawText(text, xAxis, yAxis, paint) }
            }
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

    @Test
    fun getPaintForHoursText() {
        val textColor = Color.BLACK
        val textSize = 10f
        val typeface = ResourcesCompat.getFont(
            ApplicationProvider.getApplicationContext(),
            R.font.roboto_regular
        )!!
        val realPaint = canvasRender.getPaintForHoursText(textColor, textSize, typeface)
        assertThat(realPaint.color, `is`(textColor))
        assertThat(realPaint.textAlign, `is`(Paint.Align.CENTER))
        assertThat(realPaint.textSize, `is`(textSize))
        assertThat(realPaint.typeface, `is`(typeface))
    }
}