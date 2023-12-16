package com.studentsapps.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ColorPicker(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint? = null
    private var luar: Shader? = null
    private val color = floatArrayOf(1f, 1f, 1f)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (paint == null) {
            paint = Paint()
            luar = LinearGradient(0f, 0f, 0f, measuredHeight.toFloat(), 0xffffffff.toInt(), 0xff000000.toInt(), Shader.TileMode.CLAMP)
        }
        val rgb = Color.HSVToColor(color)
        val dalam = LinearGradient(0f, 0f, measuredWidth.toFloat(), 0f, 0xffffffff.toInt(), rgb, Shader.TileMode.CLAMP)
        val shader =  ComposeShader(luar!!, dalam, PorterDuff.Mode.MULTIPLY)
        paint!!.shader = shader
        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint!!)
    }

    fun setHue(hue: Float) {
        color[0] = hue
        invalidate()
    }
}