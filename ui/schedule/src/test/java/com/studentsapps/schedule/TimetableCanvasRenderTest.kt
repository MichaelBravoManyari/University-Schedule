package com.studentsapps.schedule

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.junit.Assert.assertThrows

@Config(sdk = [33])
@RunWith(AndroidJUnit4::class)
class TimetableCanvasRenderTest {

    private val timetableCanvasRender = TimetableCanvasRender()

    @Test
    fun createTimetableBitmap_500w900h_returnBitmap() {
        val expectedTimetableBitmapHeight = 900
        val expectedTimetableBitmapWith = 500
        val expectedTimetableBitmapConfig = Bitmap.Config.ARGB_8888
        val realTimetableBitmap = timetableCanvasRender.createTimetableBitmap(500, 900)
        assertThat(realTimetableBitmap.width, `is`(expectedTimetableBitmapWith))
        assertThat(realTimetableBitmap.height, `is`(expectedTimetableBitmapHeight))
        assertThat(realTimetableBitmap.config, `is`(expectedTimetableBitmapConfig))
    }

    @Test
    fun `createTimetableBitmap_0w-100h_returnBitmap`() {
        assertThrows(Exception::class.java) {
            timetableCanvasRender.createTimetableBitmap(0, -100)
        }
    }
}