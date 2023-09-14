package com.studentsapps.schedule

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoundTextView(context: Context, attrs: AttributeSet) : MaterialTextView(context, attrs) {

    @Inject
    lateinit var canvasRender: RoundTextViewCanvasRender

    private var backgroundColor = 0

    fun setCircleBackgroundColor(@ColorInt color: Int) {
        val canvas = Canvas()
        canvasRender.drawCircleInMiddle(width, height, canvas, backgroundColor)
    }
}