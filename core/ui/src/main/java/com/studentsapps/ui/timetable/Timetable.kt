package com.studentsapps.ui.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textview.MaterialTextView
import com.studentsapps.model.ScheduleView
import com.studentsapps.model.TimetableUserPreferences
import com.studentsapps.model.getCrossSchedules
import com.studentsapps.model.getUniqueSchedules
import com.studentsapps.model.groupByDayOfWeek
import com.studentsapps.ui.R
import com.studentsapps.ui.databinding.TimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class Timetable(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var gridStrokeColor = 0
    private var halfHourGridStrokeColor = 0
    private var hoursTextColor = 0
    private var rootViewWidth = 0
    private lateinit var daysOfWeekFont: Typeface
    private lateinit var daysOfMonthFont: Typeface
    private lateinit var scheduleFont: Typeface
    private lateinit var binding: TimetableBinding
    private var is12HoursFormat = true
    private var showAsGrid = true
    private var showSaturday = true
    private var showSunday = true
    private var isMondayFirstDayOfWeek = true
    private var gridCellWidth = 0
    private val _currentMonth = MutableLiveData(utils.getMonth())
    val currentMonth = _currentMonth as LiveData<String>
    private val gestureDetector = GestureDetectorCompat(context, MyGestureListener())
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var mDownX = 0f
    private val _date = MutableLiveData(utils.getCurrentDate())
    val date = _date as LiveData<LocalDate>
    private var redrawGridsAndHours = false
    private var wasViewDrawn = false
    private var remakeTheView = false
    private var drawView = false
    private var timetableUserPreferences: TimetableUserPreferences? = null
    private var schedulesViewsList = emptyList<ScheduleView>()
    private val adapter = TimetableListAdapter()

    @Inject
    lateinit var utils: TimetableUtils

    @Inject
    lateinit var canvasRender: TimetableCanvasRender

    init {
        configureView(attrs)
        isSaveEnabled = true
    }

    private fun configureView(attrs: AttributeSet) {
        inflateView()
        getAttrs(attrs)
        configureDayViews()
        configureScheduleListAdapter()
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
                    getResourceId(
                        R.styleable.Timetable_days_of_week_font,
                        com.studentsapps.designsystem.R.font.roboto_medium
                    )
                daysOfWeekFont = ResourcesCompat.getFont(context, daysOfWeekFontId)!!

                val daysOfMonthFontId =
                    getResourceId(
                        R.styleable.Timetable_days_of_month_font,
                        com.studentsapps.designsystem.R.font.roboto_regular
                    )
                daysOfMonthFont = ResourcesCompat.getFont(context, daysOfMonthFontId)!!

                val scheduleFontId =
                    getResourceId(
                        R.styleable.Timetable_schedule_font,
                        com.studentsapps.designsystem.R.font.roboto_medium
                    )

                scheduleFont = ResourcesCompat.getFont(context, scheduleFontId)!!

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

    private fun configureScheduleListAdapter() {
        binding.scheduleListContainer.adapter = adapter
    }

    fun isDisplayedAsGrid() = showAsGrid

    fun displaySaturday() = showSaturday

    fun displaySunday() = showSunday

    fun getStartDate(): LocalDate {
        return binding.firstDay.tag as LocalDate
    }

    fun getEndDate(): LocalDate {
        return getDaysOfMonthViews().last().tag as LocalDate
    }

    fun setTimetableUserPreferences(timetableUserPreferences: TimetableUserPreferences) {
        if (timetableUserPreferences != this.timetableUserPreferences) {
            this.timetableUserPreferences = timetableUserPreferences
            drawView = true

            setIs12HoursFormat(timetableUserPreferences.is12HoursFormat)
            setShowSaturday(timetableUserPreferences.showSaturday)
            setShowSunday(timetableUserPreferences.showSunday)
            setIsMondayFirstDayOfWeek(timetableUserPreferences.isMondayFirstDayOfWeek)
            setShowAsGrid(timetableUserPreferences.showAsGrid)

            if (!wasViewDrawn || remakeTheView) {
                configureDayViews()
                updateTextOfDayOfMonthViews()
                updateTextOfDayOfWeekViews()
                restartView()
            } else if (redrawGridsAndHours) {
                restartView()
            }
        }
    }

    private fun setShowAsGrid(value: Boolean) {
        showAsGrid = value
        val hoursCellWidth: Int

        if (showAsGrid) {
            hoursCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
            displayGridView()
            setDaysOfMonthViewsEnabledState(false)
            selectCurrentDay()
        } else {
            hoursCellWidth = 0
            displayListView()
            setDaysOfMonthViewsEnabledState(true)
        }

        (binding.startDayOfWeek.layoutParams as MarginLayoutParams).let { layoutParams ->
            layoutParams.marginStart = hoursCellWidth
            binding.startDayOfWeek.layoutParams = layoutParams
        }
    }

    fun selectCurrentDay() {
        _date.value = utils.getCurrentDate()
        updateTextOfDayOfMonthViews()
        post {
            selectDayOfMonth()
        }
    }

    private fun setDaysOfMonthViewsEnabledState(enabled: Boolean) {
        getDaysOfMonthViews().forEach {
            it.isEnabled = enabled
        }
    }

    private fun restartView() {
        invalidate()
        requestLayout()
    }

    private fun setIs12HoursFormat(value: Boolean) {
        if (is12HoursFormat != value) {
            is12HoursFormat = value
            if (wasViewDrawn) redrawGridsAndHours = true
        }
    }

    private fun setShowSaturday(value: Boolean) {
        if (showSaturday != value) {
            showSaturday = value
            if (wasViewDrawn) remakeTheView = true
        }
    }

    private fun setShowSunday(value: Boolean) {
        if (showSunday != value) {
            showSunday = value
            if (wasViewDrawn) remakeTheView = true
        }
    }

    private fun setIsMondayFirstDayOfWeek(value: Boolean) {
        if (isMondayFirstDayOfWeek != value) {
            isMondayFirstDayOfWeek = value
            if (wasViewDrawn) remakeTheView = true
        }
    }

    private fun configureDayViews() {
        configureDaysOfWeekViews()
        configureDaysOfMonthViews()
    }

    private fun configureDaysOfWeekViews() {
        val daysOfWeekTextSize = getDimensionById(R.dimen.timetable_days_of_week_text_size)
        val daysOfWeekViews = getDaysOfWeekViews()
        hideIneligibleDaysOfWeekViews()
        daysOfWeekViews.forEach { view ->
            view.apply {
                textSize = daysOfWeekTextSize
                typeface = daysOfWeekFont
            }
        }
    }

    private fun updateTextOfDayOfWeekViews() {
        val daysOfWeekOrder = getDaysOfWeekOrder()
        getDaysOfWeekViews().forEachIndexed { index, view ->
            view.apply {
                text = daysOfWeekOrder[index]
            }
        }
    }

    private fun configureDaysOfMonthViews() {
        val daysOfMonthCurrentWeek =
            utils.getDaysOfMonthOfWeek(
                isMondayFirstDayOfWeek,
                showSaturday,
                showSunday,
                date.value!!
            )
        val daysOfMonthTextSize = getDimensionById(R.dimen.timetable_days_of_month_text_size)
        val daysOfMonthViews = getDaysOfMonthViews()
        hideIneligibleDaysOfMonthViews()
        daysOfMonthViews.forEachIndexed { index, view ->
            view.apply {
                val dateView = daysOfMonthCurrentWeek[index]
                tag = dateView
                textSize = daysOfMonthTextSize
                typeface = daysOfMonthFont
                setOnClickListener {
                    _date.value = it.tag as LocalDate
                    selectDayOfMonth()
                }
            }
        }
    }

    private fun selectDayOfMonth() {
        val currentDate = utils.getCurrentDate()
        val monthDayTextColor = getColorById(R.color.timetable_month_day_text_color)
        val currentMonthDayTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val currentMonthDayBackgroundColor =
            getColorById(R.color.timetable_current_month_day_background_color)

        getDaysOfMonthViews().forEach { view ->
            val viewDate = view.tag as LocalDate

            when {
                viewDate == currentDate && viewDate == date.value -> {
                    if (view.measuredWidth != 0 && view.measuredHeight != 0) {
                        val backgroundDrawable = canvasRender.getCurrentMonthDayBackground(
                            view.measuredWidth,
                            view.measuredHeight,
                            currentMonthDayBackgroundColor
                        ).toDrawable(resources)
                        applyViewStyle(view, currentMonthDayTextColor, backgroundDrawable)
                    }
                }

                viewDate == currentDate && viewDate != date.value -> {
                    applyViewStyle(view, currentMonthDayBackgroundColor, null)
                }

                viewDate == date.value && viewDate != currentDate && !showAsGrid -> {
                    if (view.measuredWidth != 0 && view.measuredHeight != 0) {
                        val backgroundDrawable = canvasRender.getCurrentMonthDayBackground(
                            view.measuredWidth,
                            view.measuredHeight,
                            monthDayTextColor
                        ).toDrawable(resources)
                        applyViewStyle(view, currentMonthDayTextColor, backgroundDrawable)
                    }
                }

                else -> {
                    applyViewStyle(view, monthDayTextColor, null)
                }
            }
        }
    }

    private fun applyViewStyle(view: TextView, textColor: Int, backgroundDrawable: Drawable?) {
        view.apply {
            setTextColor(textColor)
            background = backgroundDrawable
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
        if (realRootViewWidth != 0 && realRootViewWidth != rootViewWidth) rootViewWidth =
            realRootViewWidth
        if (drawView) {
            if (redrawGridsAndHours) {
                drawGridsAndHours()
                redrawGridsAndHours = false
            }
            if (!wasViewDrawn || remakeTheView) {
                selectDayOfMonth()
                drawGridsAndHours()
                wasViewDrawn = true
                remakeTheView = false
            }
            drawView = false
        }
    }

    private fun drawGridsAndHours() {
        val hoursCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        calculateGridCellWidth(rootViewWidth, hoursCellWidth)
        val bitmapHoursGrid = createBitmapGridAndHours(rootViewWidth, hoursCellWidth)
        setBitmapToHourDrawingGridContainer(bitmapHoursGrid)
        redrawGridsAndHours = false
    }

    private fun displayListView() {
        binding.apply {
            scheduleContainerAndGrid.visibility = GONE
            scheduleListContainer.visibility = VISIBLE
        }
    }

    private fun displayGridView() {
        binding.apply {
            scheduleListContainer.visibility = GONE
            scheduleContainerAndGrid.visibility = VISIBLE
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

    fun showSchedules(schedules: List<ScheduleView>) {
        if (schedules != schedulesViewsList) {
            schedulesViewsList = schedules

            if (showAsGrid) {
                binding.scheduleContainer.removeAllViews()
                schedulesViewsList.groupByDayOfWeek().forEach { (dayOfWeek, schedulesForOneDayOfWeek) ->

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
            } else {
                adapter.set12HoursFormat(is12HoursFormat)
                adapter.submitList(schedulesViewsList)
            }
        }
    }

    private fun addScheduleToGrid(
        schedule: ScheduleView,
        crossedSchedulesCount: Int = 1,
        index: Int = 0
    ) {
        with(schedule) {
            val scheduleView = createScheduleView(
                id,
                courseName,
                startTime,
                endTime,
                dayOfWeek,
                color,
                crossedSchedulesCount,
                index
            )
            binding.scheduleContainer.addView(scheduleView)
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
        val textSize = getDimensionById(R.dimen.timetable_schedule_text_size)
        val background = ContextCompat.getDrawable(context, R.drawable.background_schedule_view)
        return MaterialTextView(context).apply {
            contentDescription = id.toString()
            text = courseName
            gravity = Gravity.CENTER
            this.layoutParams = layoutParams
            this.background = background
            backgroundTintList = ColorStateList.valueOf(color)
            typeface = scheduleFont
            this.textSize = textSize
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

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x
                false
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = kotlin.math.abs(ev.x - mDownX)
                deltaX > mTouchSlop
            }

            else -> false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            date.value?.let {
                year = it.year
                month = it.monthValue
                day = it.dayOfMonth
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                _date.value = LocalDate.of(state.year, state.month, state.day)
            }

            else -> super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {

        var year = 0
        var month = 0
        var day = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            with(parcel) {
                year = readInt()
                month = readInt()
                day = readInt()
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            with(out) {
                writeInt(year)
                writeInt(month)
                writeInt(day)
            }
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val selectedDate =
                if (showAsGrid) {
                    if (isRightSwipe(e2)) date.value?.minusWeeks(1) else date.value?.plusWeeks(1)
                } else {
                    if (isRightSwipe(e2)) {
                        var dateReturns = date.value?.minusDays(1)
                        if (dateReturns?.dayOfWeek == DayOfWeek.SUNDAY && !showSunday)
                            dateReturns = dateReturns.minusDays(1)
                        if (dateReturns?.dayOfWeek == DayOfWeek.SATURDAY && !showSaturday)
                            dateReturns = dateReturns.minusDays(1)
                        dateReturns
                    } else {
                        var dateReturns = date.value?.plusDays(1)
                        if (dateReturns?.dayOfWeek == DayOfWeek.SATURDAY && !showSaturday)
                            dateReturns = dateReturns.plusDays(1)
                        if (dateReturns?.dayOfWeek == DayOfWeek.SUNDAY && !showSunday)
                            dateReturns = dateReturns.plusDays(1)
                        dateReturns
                    }
                }
            updateTextOfDayOfMonthViews(selectedDate)
            _date.value = selectedDate
            _currentMonth.value = date.value?.let { utils.getMonth(it) }
            selectDayOfMonth()
            return true
        }

        private fun isRightSwipe(upEvent: MotionEvent): Boolean {
            val deltaX = upEvent.x.minus(mDownX)
            return deltaX > 0
        }
    }

    private fun updateTextOfDayOfMonthViews(selectedDate: LocalDate? = null) {
        val daysOfMonthOfWeek =
            utils.getDaysOfMonthOfWeek(
                isMondayFirstDayOfWeek,
                showSaturday,
                showSunday,
                selectedDate ?: date.value!!
            )
        getDaysOfMonthViews().forEachIndexed { index, view ->
            val viewDate = daysOfMonthOfWeek[index]
            view.apply {
                text = viewDate.dayOfMonth.toString()
                tag = viewDate
            }
        }
    }

    companion object {
        private const val COLUMNS_HOURS_NUMBER = 1
        private const val ROWS_NUMBER = 24
        private const val NUM_HORIZONTAL_GRID_LINES = 24
    }
}
