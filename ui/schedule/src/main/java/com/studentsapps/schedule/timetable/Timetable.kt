package com.studentsapps.schedule.timetable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.TimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class Timetable(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var gridStrokeColor = 0
    private var halfHourGridStrokeColor = 0
    private var hoursTextColor = 0
    private var rootViewWidth = 0
    private lateinit var daysOfWeekFont: Typeface
    private lateinit var daysOfMonthFont: Typeface
    private lateinit var binding: TimetableBinding
    private var is12HoursFormat = true
    private var gridCellWidth = 0
    private var isMondayFirstDayOfWeek = true

    @Inject
    lateinit var utils: TimetableUtils

    @Inject
    lateinit var canvasRender: TimetableCanvasRender

    init {
        configureView(attrs)
        configureDaysOfWeekViews()
        configureDaysOfMonthViews()
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
        val daysOfWeekOrder = getDaysOfWeekOrder()
        val daysOfWeekViews = getDaysOfWeekViews()
        configureDaysViews(daysOfWeekViews, daysOfWeekTextSize, daysOfWeekFont, daysOfWeekOrder)
    }

    private fun configureDaysOfMonthViews() {
        val daysOfMonthCurrentWeek = utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek)
        val daysOfMonthTextSize = getDimensionById(R.dimen.timetable_days_of_month_text_size)
        val daysOfMonthViews = getDaysOfMonthViews()
        configureDaysViews(
            daysOfMonthViews,
            daysOfMonthTextSize,
            daysOfMonthFont,
            daysOfMonthCurrentWeek
        )
    }

    private fun configureDaysViews(
        daysViews: List<TextView>,
        @Dimension dayTextSize: Float,
        dayTypeface: Typeface,
        daysTexts: List<String>
    ) {
        daysViews.forEachIndexed { index, view ->
            view.apply {
                textSize = dayTextSize
                typeface = dayTypeface
                text = daysTexts[index]
            }
        }
    }

    private fun getDaysOfWeekOrder(): List<String> {
        return utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek).map { resourceId ->
            getStringById(resourceId)
        }
    }

    private fun getDaysOfWeekViews(): List<TextView> {
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
        val realRootViewWidth = getRootViewWidth(widthMeasureSpec)
        if (rootViewWidth != realRootViewWidth && realRootViewWidth != 0) {
            rootViewWidth = realRootViewWidth
            val hoursCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
            calculateGridCellWidth(realRootViewWidth, hoursCellWidth)
            val bitmapHoursGrid = createBitmapGridAndHours(realRootViewWidth, hoursCellWidth)
            setBitmapToHourDrawingGridContainer(bitmapHoursGrid)
        }
    }

    private fun createBitmapGridAndHours(bitmapWidth: Int, hoursCellWidth: Int): Bitmap {
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val bitmapHeight =
            utils.calculateTimetableBitmapHeight(ROWS_NUMBER, gridCellHeight)
        val timetableBitmap =
            canvasRender.createTimetableBitmap(bitmapWidth, bitmapHeight)
        val canvas = Canvas(timetableBitmap)
        drawGrid(
            canvas,
            hoursCellWidth,
            gridCellHeight,
            bitmapHeight.toFloat(),
            bitmapWidth.toFloat()
        )

        drawHoursText(canvas, gridCellHeight, hoursCellWidth)
        return timetableBitmap
    }

    private fun drawGrid(
        canvas: Canvas,
        hoursCellWidth: Int,
        gridCellHeight: Int,
        lineHeight: Float,
        lineLength: Float
    ) {
        val lineWidth = getDimensionById(R.dimen.timetable_grid_lines_stroke_width)
        val paintGrid = canvasRender.getPaintForGridLines(gridStrokeColor, lineWidth)
        val paintHalfHourLine =
            canvasRender.getPaintForGridLines(halfHourGridStrokeColor, lineWidth)
        val verticalLinesCoordinates = utils.getVerticalLinesCoordinates(
            NUM_VERTICAL_GRID_LINES,
            hoursCellWidth,
            gridCellWidth,
            lineHeight
        )
        val horizontalHourLinesCoordinates = utils.getHorizontalHourLinesCoordinates(
            NUM_HORIZONTAL_GRID_LINES,
            hoursCellWidth,
            gridCellHeight,
            lineLength
        )
        val halfHourHorizontalLinesCoordinates = utils.getHalfHourHorizontalLinesCoordinates(
            NUM_HORIZONTAL_GRID_LINES,
            hoursCellWidth,
            gridCellHeight,
            lineLength
        )
        canvasRender.drawGrid(
            canvas,
            paintGrid,
            paintHalfHourLine,
            verticalLinesCoordinates,
            horizontalHourLinesCoordinates,
            halfHourHorizontalLinesCoordinates
        )
    }

    private fun calculateGridCellWidth(realRootViewWidth: Int, hoursCellWidth: Int) {
        gridCellWidth = utils.calculateGridCellWidth(
            realRootViewWidth,
            hoursCellWidth,
            COLUMNS_NUMBER,
            COLUMNS_HOURS_NUMBER
        )
    }

    private fun getRootViewWidth(widthMeasureSpec: Int): Int {
        val rootViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        return utils.calculateRealRootViewWidth(rootViewWidth, paddingLeft, paddingRight)
    }

    private fun setBitmapToHourDrawingGridContainer(bitmapHoursGrid: Bitmap) {
        binding.hourDrawingContainerAndGrid.apply {
            setImageBitmap(bitmapHoursGrid)
        }
    }

    private fun drawHoursText(canvas: Canvas, gridCellHeight: Int, hoursCellWidth: Int) {
        val hoursTextSize = getDimensionById(R.dimen.timetable_hours_text_size)
        val hourTextPaint =
            canvasRender.getPaintForHoursText(hoursTextColor, hoursTextSize, daysOfWeekFont)
        val xAxis = hoursCellWidth / 2f
        val hoursText =
            if (is12HoursFormat) getStringArrayById(R.array.hours_in_12_hour_format).toList() else getStringArrayById(
                R.array.hours_in_24_hour_format
            ).toList()
        if (is12HoursFormat)
            canvasRender.drawHoursText12HourFormat(
                canvas,
                hoursText,
                gridCellHeight,
                hourTextPaint,
                xAxis
            )
        else
            canvasRender.drawHoursText24HourFormat(
                canvas,
                hoursText,
                gridCellHeight,
                hourTextPaint,
                xAxis
            )
    }

    private fun getColorById(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(context, colorId)
    }

    private fun getStringArrayById(@ArrayRes arrayId: Int): Array<String> {
        return resources.getStringArray(arrayId)
    }

    private fun getDimensionPixelSizeById(@DimenRes dimenId: Int): Int {
        return resources.getDimensionPixelSize(dimenId)
    }

    private fun getDimensionById(@DimenRes dimenId: Int): Float {
        return resources.getDimension(dimenId)
    }

    companion object {
        private const val COLUMNS_NUMBER = 8
        private const val COLUMNS_HOURS_NUMBER = 1
        private const val ROWS_NUMBER = 24
        private const val NUM_VERTICAL_GRID_LINES = 6
        private const val NUM_HORIZONTAL_GRID_LINES = 24
    }
}