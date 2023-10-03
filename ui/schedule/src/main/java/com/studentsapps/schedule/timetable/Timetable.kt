package com.studentsapps.schedule.timetable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.textview.MaterialTextView
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.TimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalTime
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
    private var showAsGrid = true
    private var showSaturday = true
    private var showSunday = true
    private var isMondayFirstDayOfWeek = true
    private var gridCellWidth = 0
    val currentMonth = utils.getMonth()

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

                showSaturday = getBoolean(R.styleable.Timetable_show_saturday, showSaturday)

                showSunday = getBoolean(R.styleable.Timetable_show_sunday, showSunday)
            } finally {
                recycle()
            }
        }
    }

    fun setShowAsGrid(value: Boolean) {
        showAsGrid = value
        if (showAsGrid) displayGridView() else displayListView()
    }

    private fun configureDaysOfWeekViews() {
        val daysOfWeekTextSize = getDimensionById(R.dimen.timetable_days_of_week_text_size)
        val daysOfWeekOrder = getDaysOfWeekOrder()
        val daysOfWeekViews = getDaysOfWeekViews()
        hideIneligibleDaysOfWeekViews()
        configureDaysViews(daysOfWeekViews, daysOfWeekTextSize, daysOfWeekFont, daysOfWeekOrder)
    }

    private fun configureDaysOfMonthViews() {
        val daysOfMonthCurrentWeek =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday)
        val daysOfMonthTextSize = getDimensionById(R.dimen.timetable_days_of_month_text_size)
        val daysOfMonthViews = getDaysOfMonthViews()
        hideIneligibleDaysOfMonthViews()
        configureDaysViews(
            daysOfMonthViews,
            daysOfMonthTextSize,
            daysOfMonthFont,
            daysOfMonthCurrentWeek
        )
    }

    private fun selectCurrentMonthDay() {
        val currentMonthDay = utils.getCurrentMonthDay()
        val currentMonthDayTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val currentMonthDayBackgroundColor =
            getColorById(R.color.timetable_current_month_day_background_color)
        getDaysOfMonthViews().forEach { view ->
            if (view.text == currentMonthDay) {
                view.apply {
                    setTextColor(currentMonthDayTextColor)
                    background = canvasRender.getCurrentMonthDayBackground(
                        view.measuredWidth,
                        view.measuredHeight,
                        currentMonthDayBackgroundColor
                    ).toDrawable(resources)
                }
            }
        }
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
        return utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
            .map { resourceId ->
                getStringById(resourceId)
            }
    }

    private fun getDaysOfWeekViews(): List<TextView> {
        return with(binding) {
            val daysOfWeekViews = mutableListOf(
                startDayOfWeek,
                secondDayOfWeek,
                thirdDayOfWeek,
                fourthDayOfWeek,
                fifthDayOfWeek
            )

            if (showSaturday && showSunday)
                daysOfWeekViews.addAll(listOf(binding.sixthDayOfWeek, binding.seventhDayOfWeek))
            else if (showSaturday || showSunday)
                daysOfWeekViews.add(binding.sixthDayOfWeek)

            daysOfWeekViews
        }
    }

    private fun getDaysOfMonthViews(): List<TextView> {
        return with(binding) {
            val daysOfMonthViews = mutableListOf(
                firstDay,
                secondDay,
                thirdDay,
                fourthDay,
                fifthDay
            )

            if (showSaturday && showSunday)
                daysOfMonthViews.addAll(listOf(binding.sixthDay, binding.seventhDay))
            else if (showSaturday || showSunday)
                daysOfMonthViews.add(binding.sixthDay)

            daysOfMonthViews
        }
    }

    private fun hideIneligibleDaysOfWeekViews() {
        with(binding) {
            if (!showSaturday && !showSunday) {
                sixthDayOfWeek.visibility = GONE
                seventhDayOfWeek.visibility = GONE
            } else if (!showSaturday || !showSunday)
                seventhDayOfWeek.visibility = GONE
        }
    }

    private fun hideIneligibleDaysOfMonthViews() {
        with(binding) {
            if (!showSaturday && !showSunday) {
                sixthDay.visibility = GONE
                seventhDay.visibility = GONE
            } else if (!showSaturday || !showSunday)
                seventhDay.visibility = GONE
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
            selectCurrentMonthDay()
            val bitmapHoursGrid = createBitmapGridAndHours(rootViewWidth, hoursCellWidth)
            setBitmapToHourDrawingGridContainer(bitmapHoursGrid)
            provisionalListView()
            showScheduleInGrid(
                listOf(
                    Schedule(
                        2,
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 0),
                        "sdf",
                        DayOfWeek.TUESDAY,
                        "Test 1",
                        Color.RED
                    ),
                    Schedule(
                        3,
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 0),
                        "sdf",
                        DayOfWeek.TUESDAY,
                        "Test 2",
                        Color.YELLOW
                    ),
                    Schedule(
                        4,
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 0),
                        "sdf",
                        DayOfWeek.SATURDAY,
                        "Test 3",
                        Color.YELLOW
                    ),
                    Schedule(
                        5,
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 0),
                        "sdf",
                        DayOfWeek.SUNDAY,
                        "Test 4",
                        Color.RED
                    ),
                    Schedule(
                        6,
                        LocalTime.of(12, 30),
                        LocalTime.of(15, 0),
                        "sdf",
                        DayOfWeek.MONDAY,
                        "Test 5",
                        Color.BLUE
                    )
                )
            )
        }
    }

    private fun displayListView() {
        binding.apply {
            hourDrawingContainerAndGrid.visibility = GONE
            scheduleListContainer.visibility = VISIBLE
        }
    }

    private fun displayGridView() {
        binding.apply {
            scheduleListContainer.visibility = GONE
            hourDrawingContainerAndGrid.visibility = VISIBLE
        }
    }

    private fun provisionalListView() {
        val adapter = TimetableListAdapter()
        binding.scheduleListContainer.adapter = adapter
        val listString = mutableListOf("df", "hello")
        adapter.submitList(listString)
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
        val numHorizontalGridLines = utils.getNumHorizontalGridLines(showSaturday, showSunday)
        val paintGrid = canvasRender.getPaintForGridLines(gridStrokeColor, lineWidth)
        val paintHalfHourLine =
            canvasRender.getPaintForGridLines(halfHourGridStrokeColor, lineWidth)
        val verticalLinesCoordinates = utils.getVerticalLinesCoordinates(
            numHorizontalGridLines,
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
        val columnsNumber = utils.getColumnsNumber(showSaturday, showSunday)
        gridCellWidth = utils.calculateGridCellWidth(
            realRootViewWidth,
            hoursCellWidth,
            columnsNumber,
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

    fun showScheduleInGrid(schedules: List<Schedule>) {
        schedules.groupByDayOfWeek().forEach { (dayOfWeek, schedulesForOneDayOfWeek) ->

            if ((dayOfWeek != DayOfWeek.SUNDAY || showSunday) && (dayOfWeek != DayOfWeek.SATURDAY || showSaturday)) {
                val scheduleCrossing = schedulesForOneDayOfWeek.getCrossSchedules()
                val uniqueSchedules = schedulesForOneDayOfWeek.getUniqueSchedules()

                scheduleCrossing.forEach { crossSchedules ->
                    crossSchedules.forEachIndexed { index, schedule ->
                        val crossedSchedulesCount = crossSchedules.size
                        addScheduleToGrid(schedule, crossedSchedulesCount, index)
                    }
                }

                uniqueSchedules.forEach { uniqueSchedule ->
                    addScheduleToGrid(uniqueSchedule)
                }
            }
        }
    }

    private fun addScheduleToGrid(
        schedule: Schedule,
        crossedSchedulesCount: Int = 1,
        index: Int = 0
    ) {
        with(schedule) {
            val scheduleView = createScheduleView(
                id,
                courseName,
                startTime,
                endTime,
                day,
                color,
                crossedSchedulesCount,
                index
            )
            binding.scheduleContainerAndGrid.addView(scheduleView)
        }
    }

    private fun createScheduleView(
        id: Int,
        courseName: String,
        startTime: LocalTime,
        endTime: LocalTime,
        day: DayOfWeek,
        color: Int,
        crossedSchedulesCount: Int = 1,
        crossScheduleIndex: Int = 0
    ): MaterialTextView {
        val layoutParams = getScheduleViewLayoutParams(
            startTime,
            endTime,
            day,
            crossedSchedulesCount,
            crossScheduleIndex
        )
        val background = ContextCompat.getDrawable(context, R.drawable.background_schedule_view)
        return MaterialTextView(context).apply {
            contentDescription = id.toString()
            text = courseName
            gravity = Gravity.CENTER
            this.layoutParams = layoutParams
            this.background = background
            backgroundTintList = ColorStateList.valueOf(color)
            setTextColor(getTextColorBasedOnCourseColor(color))
        }
    }

    private fun getScheduleViewLayoutParams(
        startTime: LocalTime,
        endTime: LocalTime,
        day: DayOfWeek,
        crossedSchedulesCount: Int = 1,
        crossScheduleIndex: Int = 0
    ): RelativeLayout.LayoutParams {
        val scheduleEndMargin = getDimensionPixelSizeById(R.dimen.timetable_schedule_end_margin)
        val cellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hoursCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val scheduleBottomMargin =
            getDimensionPixelSizeById(R.dimen.timetable_schedule_bottom_margin)

        val width = when (crossedSchedulesCount) {
            1 -> utils.calculateSingleScheduleViewWidth(gridCellWidth, scheduleEndMargin)
            else -> utils.calculateCrossScheduleViewWidth(
                gridCellWidth,
                crossedSchedulesCount,
                scheduleEndMargin
            )
        }

        val height =
            utils.calculateScheduleViewHeight(startTime, endTime, cellHeight, scheduleBottomMargin)
        val topMargin = utils.calculateTopMarginScheduleView(startTime, cellHeight)
        val startMargin = when (crossedSchedulesCount) {
            1 -> utils.calculateStartMarginSingleScheduleView(
                hoursCellWidth,
                gridCellWidth,
                day,
                isMondayFirstDayOfWeek,
                showSaturday,
                showSunday
            )

            else -> utils.calculateStartMarginCrossScheduleView(
                hoursCellWidth,
                gridCellWidth,
                day,
                isMondayFirstDayOfWeek,
                showSaturday,
                showSunday,
                crossedSchedulesCount,
                crossScheduleIndex
            )
        }

        val layoutParams = RelativeLayout.LayoutParams(width, height).apply {
            addRule(RelativeLayout.ALIGN_PARENT_START)
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            setMargins(startMargin, topMargin, 0, 0)
        }

        return layoutParams
    }

    private fun getTextColorBasedOnCourseColor(@ColorInt courseColor: Int): Int {
        return if (ColorUtils.calculateLuminance(courseColor) < 0.5)
            getColorById(R.color.timetable_schedule_view_light_text_color)
        else
            getColorById(R.color.timetable_schedule_view_dark_text_color)
    }

    companion object {
        private const val COLUMNS_HOURS_NUMBER = 1
        private const val ROWS_NUMBER = 24
        private const val NUM_HORIZONTAL_GRID_LINES = 24
    }
}

data class Schedule(
    val id: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classPlace: String,
    val day: DayOfWeek,
    val courseName: String,
    val color: Int
)

fun List<Schedule>.groupByDayOfWeek(): Map<DayOfWeek, List<Schedule>> {
    return groupBy { it.day }
}

fun List<Schedule>.getCrossSchedules(): List<List<Schedule>> {
    val list1 = mutableListOf<MutableList<Schedule>>()
    for (i in 0 until size - 1) {
        val schedule = this[i]
        val list2 = mutableListOf<Schedule>()
        if (!list1.scheduleContains(schedule))
            list2.add(schedule)

        for (j in i + 1 until size) {
            val scheduleToCompare = this[j]
            if (schedule.isCrossingSchedules(scheduleToCompare)) {
                if (!list1.scheduleContains(schedule))
                    list2.add(scheduleToCompare)
                else
                    list1.addScheduleToList(schedule, scheduleToCompare)
            }
        }

        if (list2.isNotEmpty())
            list1.add(list2)
    }

    for (list in list1) {
        list.sortBy {
            it.startTime
        }
    }

    return list1
}

fun List<Schedule>.getUniqueSchedules(): List<Schedule> {
    return if (count() == 1)
        this
    else {
        val uniqueSchedules = mutableListOf<Schedule>()
        for (i in 0 until size - 1) {
            val schedule = this[i]
            var isUnique = true
            for (j in i + 1 until size) {
                val scheduleToCompare = this[j]
                if (schedule.isCrossingSchedules(scheduleToCompare)) {
                    isUnique = false
                    break
                }
            }
            if (isUnique) uniqueSchedules.add(schedule)
        }
        uniqueSchedules
    }
}

fun List<List<Schedule>>.scheduleContains(schedule: Schedule): Boolean {
    for (list in this) {
        if (schedule in list)
            return true
    }
    return false
}

fun List<MutableList<Schedule>>.addScheduleToList(
    scheduleToCompare: Schedule,
    scheduleToAdd: Schedule
) {
    for (list in this) {
        if (scheduleToCompare in list) {
            list.add(scheduleToAdd)
            break
        }
    }
}

fun Schedule.isCrossingSchedules(scheduleToCompare: Schedule): Boolean {
    val startHourSchedule = this.startTime
    val endHourSchedule = this.endTime
    val startHourScheduleCompare = scheduleToCompare.startTime
    val endHourScheduleCompare = scheduleToCompare.endTime
    return startHourSchedule < endHourScheduleCompare && endHourSchedule > startHourScheduleCompare
}
