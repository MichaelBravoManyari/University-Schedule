package com.studentsapps.schedule

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.studentsapps.schedule.databinding.TimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class Timetable(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var gridStrokeColor = 0
    private var halfHourGridStrokeColor = 0
    private var hoursTextColor = 0
    private lateinit var daysOfWeekFont: Typeface
    private lateinit var daysOfMonthFont: Typeface
    private lateinit var binding: TimetableBinding
    private var is12HoursFormat = true
    private var gridCellWidth = 0
    private var isMondayFirstDayOfWeek = true

    @Inject
    lateinit var dateUtils: TimetableDateUtils

    init {
        configureView(attrs)
        configureDaysOfWeekViews()
        configureDaysOfMonthViews()
        showDaysOfMonthCurrentWeek()
    }

    private fun configureView(attrs: AttributeSet) {
        inflateView()
        getAttrs(attrs)
    }

    private fun inflateView() {
        binding = TimetableBinding.inflate(LayoutInflater.from(context), this)
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

                val daysOfWeekFontId =
                    getResourceId(R.styleable.Timetable_days_of_week_font, R.font.roboto_medium)
                daysOfWeekFont = ResourcesCompat.getFont(context, daysOfWeekFontId)!!

                val daysOfMonthFontId =
                    getResourceId(R.styleable.Timetable_days_of_month_font, R.font.roboto_regular)
                daysOfMonthFont = ResourcesCompat.getFont(context, daysOfMonthFontId)!!

                is12HoursFormat =
                    getBoolean(R.styleable.Timetable_is_12_hours_format, is12HoursFormat)

                isMondayFirstDayOfWeek = getBoolean(
                    R.styleable.Timetable_is_monday_first_day_of_week, isMondayFirstDayOfWeek
                )

            } finally {
                recycle()
            }
        }
    }

    private fun configureDaysOfWeekViews() {
        val daysOfWeekTextSize = getDimensionById(R.dimen.timetable_days_of_week_text_size)
        val daysOfWeekOrder = dateUtils.getDaysOfWeekOrder(isMondayFirstDayOfWeek)
        getDayOfWeekViews().forEachIndexed { index, view ->
            view.apply {
                typeface = daysOfWeekFont
                textSize = daysOfWeekTextSize
                text = getStringById(daysOfWeekOrder[index])
            }
        }
    }

    private fun configureDaysOfMonthViews() {
        val daysOfMonthTextSize = getDimensionById(R.dimen.timetable_days_of_month_text_size)
        getDaysOfMonthViews().forEach {view ->
            view.apply {
                textSize = daysOfMonthTextSize
                typeface = daysOfMonthFont
            }
        }
    }

    private fun showDaysOfMonthCurrentWeek() {
        val daysOfMonthCurrentWeek = dateUtils.getDaysOfMonthCurrentWeek(isMondayFirstDayOfWeek)
        val daysOfMonthViews = getDaysOfMonthViews()
        setDaysOfMonthText(daysOfMonthCurrentWeek, daysOfMonthViews)
    }

    private fun setDaysOfMonthText(
        daysOfMonthCurrentWeek: List<String>,
        daysOfMonthViews: List<TextView>
    ) {
        daysOfMonthViews.forEachIndexed { index, view ->
            view.text = daysOfMonthCurrentWeek[index]
        }
    }

    private fun getDayOfWeekViews(): List<TextView> {
        return with(binding) {
            listOf(
                startDayOfWeek,
                secondDayOfWeek,
                thirdDayOfWeek,
                fourthDayOfWeek,
                fifthDayOfWeek,
                sixthDayOfWeek,
                seventhDayOfWeek
            )
        }
    }

    private fun getDaysOfMonthViews(): List<TextView> {
        return with(binding) {
            listOf(
                firstDay,
                secondDay,
                thirdDay,
                fourthDay,
                fifthDay,
                sixthDay,
                seventhDay
            )
        }
    }

    private fun getStringById(@StringRes stringId: Int): String {
        return resources.getString(stringId)
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
        binding.hourDrawingContainerAndGrid.apply {
            setImageBitmap(bitmapHoursGrid)
        }
    }

    private fun createBitmapGridAndHours(width: Int, hoursCellWidth: Int): Bitmap {
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val bitmap =
            Bitmap.createBitmap(width, ROWS_NUMBER * gridCellHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawGrid(canvas, hoursCellWidth, gridCellHeight)
        drawHoursText(canvas, gridCellHeight, hoursCellWidth)
        return bitmap
    }

    private fun drawGrid(canvas: Canvas, hoursCellWidth: Int, gridCellHeight: Int) {
        val gridPaint = createPaintForGridLines(gridStrokeColor)
        val paintHalfHourLine = createPaintForGridLines(halfHourGridStrokeColor)
        drawVerticalGridLines(canvas, hoursCellWidth, gridPaint)
        drawHorizontalGridLines(
            canvas, hoursCellWidth, gridCellHeight, gridPaint, paintHalfHourLine
        )
    }

    private fun drawHoursText(canvas: Canvas, gridCellHeight: Int, hoursCellWidth: Int) {
        val hoursTextSize = getDimensionById(R.dimen.timetable_hours_text_size)
        val hourTextPaint = createPaintForHoursText(hoursTextColor, hoursTextSize, daysOfWeekFont)
        val hourTextHeight = hourTextPaint.descent() - hourTextPaint.ascent()
        val xAxis = hoursCellWidth / 2f
        if (is12HoursFormat) drawHoursText12HourFormat(
            canvas,
            xAxis,
            gridCellHeight,
            hourTextHeight,
            hourTextPaint
        )
        else drawHoursText24HourFormat(canvas, xAxis, gridCellHeight, hourTextHeight, hourTextPaint)
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
        @ColorInt textColor: Int, @Dimension textSize: Float, typeface: Typeface
    ): Paint {
        return Paint().apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            this.textSize = textSize
            this.typeface = typeface
        }
    }

    private fun drawHorizontalGridLines(
        canvas: Canvas,
        hoursCellWidth: Int,
        gridCellHeight: Int,
        gridPaint: Paint,
        paintHalfHourLine: Paint
    ) {
        for (lineNumber in 1..NUM_HORIZONTAL_GRID_LINES) {
            val xAxisStart = hoursCellWidth.toFloat()
            val yAxis = calculateHorizontalGridLineYAxis(lineNumber, gridCellHeight)
            val xAxisStop = canvas.width.toFloat()
            val paint = if (isHorizontalLineHourMarker(lineNumber)) gridPaint else paintHalfHourLine
            drawLine(canvas, xAxisStart, yAxis, xAxisStop, yAxis, paint)
        }
    }

    // The horizontal lines of the grid can mark the hours and half hours.
    private fun isHorizontalLineHourMarker(lineNumber: Int): Boolean {
        return lineNumber % 2 == 0
    }

    private fun calculateHorizontalGridLineYAxis(lineNumber: Int, gridCellHeight: Int): Float {
        return (lineNumber * gridCellHeight) / 2f
    }

    private fun drawVerticalGridLines(canvas: Canvas, hoursCellWidth: Int, gridPaint: Paint) {
        for (lineNumber in 1..NUM_VERTICAL_GRID_LINES) {
            val xAxis = calculateVerticalGridLineXAxis(hoursCellWidth, lineNumber)
            val yAxisStart = 0f
            val yAxisStop = canvas.height.toFloat()
            drawLine(canvas, xAxis, yAxisStart, xAxis, yAxisStop, gridPaint)
        }
    }

    private fun calculateVerticalGridLineXAxis(hoursCellWidth: Int, lineNumber: Int): Float {
        return hoursCellWidth + (lineNumber * gridCellWidth).toFloat()
    }

    private fun drawLine(
        canvas: Canvas,
        xAxisStart: Float,
        yAxisStart: Float,
        xAxisStop: Float,
        yAxisStop: Float,
        paint: Paint
    ) {
        canvas.drawLine(xAxisStart, yAxisStart, xAxisStop, yAxisStop, paint)
    }

    private fun drawText(canvas: Canvas, text: String, xAxis: Float, yAxis: Float, paint: Paint) {
        canvas.drawText(text, xAxis, yAxis, paint)
    }

    private fun calculateYAxisOfHoursText24HourFormat(
        gridCellHeight: Int, hourPosition: Int, hourTextHeight: Float
    ): Float {
        return (gridCellHeight * (hourPosition + 1)) + (hourTextHeight / 3)
    }

    private fun calculateYAxisOfHoursText12HourFormat(
        gridCellHeight: Int, hourPosition: Int, hourTextHeight: Float, textIndex: Int
    ): Float {
        return (gridCellHeight * (hourPosition + 1)) + (hourTextHeight * textIndex)
    }

    private fun drawHoursText24HourFormat(
        canvas: Canvas,
        textXAxisPosition: Float,
        gridCellHeight: Int,
        hourTextHeight: Float,
        paint: Paint
    ) {
        for ((hourPosition, hour) in hoursIn24HourFormat.withIndex()) {
            val yAxis =
                calculateYAxisOfHoursText24HourFormat(gridCellHeight, hourPosition, hourTextHeight)
            drawText(canvas, hour, textXAxisPosition, yAxis, paint)
        }
    }

    private fun drawHoursText12HourFormat(
        canvas: Canvas,
        textXAxisPosition: Float,
        gridCellHeight: Int,
        hourTextHeight: Float,
        paint: Paint
    ) {
        for ((hourPosition, hour) in hoursIn12HourFormat.withIndex()) {
            val hourTextSplit = hour.split(" ")
            hourTextSplit.forEachIndexed { textIndex, text ->
                val yAxis = calculateYAxisOfHoursText12HourFormat(
                    gridCellHeight, hourPosition, hourTextHeight, textIndex
                )
                drawText(canvas, text, textXAxisPosition, yAxis, paint)
            }
        }
    }

    companion object {
        private const val COLUMNS_NUMBER = 8
        private const val COLUMNS_HOURS_NUMBER = 1
        private const val ROWS_NUMBER = 24
        private const val GRID_LINES_STROKE_WIDTH = 3F
        private const val NUM_VERTICAL_GRID_LINES = 6
        private const val NUM_HORIZONTAL_GRID_LINES = 48

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