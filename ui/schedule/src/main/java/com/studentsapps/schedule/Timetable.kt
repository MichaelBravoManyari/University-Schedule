package com.studentsapps.schedule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

internal class Timetable(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var gridStrokeColor = 0
    private var halfHourGridStrokeColor = 0
    private var hoursTextColor = 0
    private lateinit var typeface: Typeface
    private var is12HoursFormat = true
    private var gridCellWidth = 0

    init {
        inflateView()
        getAttrs(attrs)
    }

    private fun inflateView() {
        LayoutInflater.from(context).inflate(R.layout.timetable, this, true)
    }

    private fun getAttrs(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.Timetable).apply {
            try {
                gridStrokeColor = getColor(
                    R.styleable.Timetable_grid_stroke_color,
                    getColorById(R.color.timetable_default_grid_stroke_color)
                )

                halfHourGridStrokeColor = getColor(
                    R.styleable.Timetable_half_hour_grid_stroke_color,
                    getColorById(R.color.timetable_default_half_hour_grid_stroke_color)
                )

                hoursTextColor = getColor(
                    R.styleable.Timetable_hours_text_color,
                    getColorById(R.color.timetable_default_hours_text_color)
                )

                val fontId = getResourceId(R.styleable.Timetable_typeface, R.font.roboto_medium)
                typeface = ResourcesCompat.getFont(context, fontId)!!

                is12HoursFormat =
                    getBoolean(R.styleable.Timetable_is_12_hours_format, is12HoursFormat)


            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val rootViewWidth = getRootViewWidth(widthMeasureSpec)
        val hoursCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        gridCellWidth = calculateGridCellWidth(rootViewWidth, hoursCellWidth)
        val bitmapHoursGrid = createBitmapGridAndHours(rootViewWidth, hoursCellWidth)
        setBitmapToHourDrawingGridContainer(bitmapHoursGrid)
    }

    private fun setBitmapToHourDrawingGridContainer(bitmapHoursGrid: Bitmap) {
        findViewById<ImageView>(R.id.hour_drawing_container_and_grid).apply {
            setImageBitmap(bitmapHoursGrid)
        }
    }

    private fun getColorById(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(context, colorId)
    }

    private fun getDimensionPixelSizeById(@DimenRes dimenId: Int): Int {
        return resources.getDimensionPixelSize(dimenId)
    }

    private fun getDimensionById(@DimenRes dimenId: Int): Float {
        return resources.getDimension(dimenId)
    }

    private fun getRootViewWidth(widthMeasureSpec: Int): Int {
        return MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
    }

    private fun calculateGridCellWidth(rootViewWidth: Int, hoursCellWidth: Int): Int {
        return (rootViewWidth - hoursCellWidth) / (COLUMNS_NUMBER - COLUMNS_HOURS_NUMBER)
    }

    private fun createPaintForGridLines(@ColorInt lineColor: Int): Paint {
        return Paint().apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeWidth = GRID_LINES_STROKE_WIDTH
        }
    }

    private fun createPaintForHoursText(
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

    private fun createBitmapGridAndHours(width: Int, hoursCellWidth: Int): Bitmap {
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hoursTextSize = getDimensionById(R.dimen.timetable_hours_text_size)
        val bitmap =
            Bitmap.createBitmap(width, ROWS_NUMBER * gridCellHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val gridPaint = createPaintForGridLines(gridStrokeColor)
        val paintHalfHourLine = createPaintForGridLines(halfHourGridStrokeColor)
        val hourTextPaint = createPaintForHoursText(hoursTextColor, hoursTextSize, typeface)
        val hourTextHeight = hourTextPaint.descent() - hourTextPaint.ascent()
        val dayHours = if (is12HoursFormat) hoursIn12HourFormat else hoursIn24HourFormat

        // Dibujar lineas verticales
        for (i in 2 until COLUMNS_NUMBER) {
            val x = (i - 1) * gridCellWidth + hoursCellWidth
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), canvas.height.toFloat(), gridPaint)
        }

        // Dibujar lineas horizontales
        for (i in 1 until ROWS_NUMBER * 2) {
            val y = (i * gridCellHeight) / 2
            canvas.drawLine(
                hoursCellWidth.toFloat(),
                y.toFloat(),
                canvas.width.toFloat(),
                y.toFloat(),
                if (i % 2 != 0) paintHalfHourLine else gridPaint
            )
        }

        // Dibujar texto de horas
        for ((index, hour) in dayHours.withIndex()) {
            val hourTextSplit = hour.split(" ")
            val y = (gridCellHeight * (index + 1)).toFloat()
            if (is12HoursFormat) {
                hourTextSplit.forEachIndexed { textIndex, text ->
                    canvas.drawText(
                        text,
                        hoursCellWidth / 2f,
                        y + (hourTextHeight * textIndex),
                        hourTextPaint
                    )
                }
            } else {
                canvas.drawText(
                    hour,
                    hoursCellWidth / 2f,
                    y + (hourTextHeight / 3),
                    hourTextPaint
                )
            }
        }

        return bitmap
    }

    companion object {
        private const val COLUMNS_NUMBER = 8
        private const val COLUMNS_HOURS_NUMBER = 1
        private const val ROWS_NUMBER = 24
        private const val GRID_LINES_STROKE_WIDTH = 3F

        private val hoursIn12HourFormat = listOf(
            "1 am",
            "2 am",
            "3 am",
            "4 am",
            "5 am",
            "6 am",
            "7 am",
            "8 am",
            "9 am",
            "10 am",
            "11 am",
            "12 pm",
            "1 pm",
            "2 pm",
            "3 pm",
            "4 pm",
            "5 pm",
            "6 pm",
            "7 pm",
            "8 pm",
            "9 pm",
            "10 pm",
            "11 pm"
        )

        private val hoursIn24HourFormat = listOf(
            "1:00",
            "2:00",
            "3:00",
            "4:00",
            "5:00",
            "6:00",
            "7:00",
            "8:00",
            "9:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
            "20:00",
            "21:00",
            "22:00",
            "23:00"
        )
    }
}