package com.studentsapps.schedule

import android.graphics.Canvas
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class RoundTextViewCanvasRenderTest {

    private val canvasRender = RoundTextViewCanvasRender()

    @Test
    fun drawCircleInMiddle_200w400h() {
        val canvas = mockk<Canvas>(relaxed = true)
        val circleColor = Color.BLACK
        canvasRender.drawCircleInMiddle(200, 400, canvas, circleColor)
        verify { canvas.drawCircle(100f, 200f, 200f, any()) }
    }
}