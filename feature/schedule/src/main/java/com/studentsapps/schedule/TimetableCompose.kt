package com.studentsapps.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.studentsapps.model.ScheduleDetails
import com.studentsapps.model.ScheduleView
import com.studentsapps.model.TimetableUserPreferences
import com.studentsapps.model.asScheduleView
import com.studentsapps.model.getCrossSchedules
import com.studentsapps.model.getUniqueSchedules
import com.studentsapps.model.groupByDayOfWeek
import com.studentsapps.schedule.viewmodels.ScheduleUiState
import com.studentsapps.schedule.viewmodels.ScheduleViewModel
import com.studentsapps.ui.R
import com.studentsapps.ui.theme.UniversityScheduleTheme
import com.studentsapps.ui.timetable.TimetableUtils
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class TimetableProps(
    val prefs: TimetableUserPreferences,
    val changePage: (LocalDate) -> Unit,
    val scheduleByDate: Map<LocalDate, List<ScheduleDetails>>,
    val getDaysOfMonthOfWeek: (Boolean, Boolean, Boolean, LocalDate) -> List<LocalDate>,
    val getDaysOfWeekOrder: (Boolean, Boolean, Boolean) -> List<Int>,
    val onClickSchedule: (String) -> Unit,
    val updateCurrentMonth: (LocalDate) -> Unit,
    val selectNowDay: Boolean,
    val updateSelectNowDay: () -> Unit,
)

@Suppress("ktlint:standard:function-naming")
@Composable
fun TimetableCompose(
    viewModel: ScheduleViewModel,
    onClickSchedule: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ScheduleUiState.Success -> {
            val success = uiState as ScheduleUiState.Success
            val prefs = success.timetableUserPreferences

            val commonProps =
                TimetableProps(
                    prefs = prefs,
                    scheduleByDate = success.scheduleByDate,
                    changePage = viewModel::loadScheduleForDate,
                    getDaysOfMonthOfWeek = viewModel::getDaysOfMonthOfWeek,
                    getDaysOfWeekOrder = viewModel::getDaysOfWeekOrder,
                    onClickSchedule = onClickSchedule,
                    updateCurrentMonth = viewModel::setCurrentMonth,
                    selectNowDay = success.selectNowDay,
                    updateSelectNowDay = { viewModel.selectNowDay(false) },
                )

            AnimatedContent(
                targetState = prefs.showAsGrid,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TimetableViewTransition",
            ) { showAsGrid ->
                if (showAsGrid) {
                    TimetableGrid(commonProps)
                } else {
                    TimetableList(commonProps)
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val regularFont =
                    FontFamily(
                        Font(
                            com.studentsapps.designsystem.R.font.roboto_regular,
                            FontWeight.Normal,
                        ),
                    )

                Text(
                    text = stringResource(com.studentsapps.schedule.R.string.loading_preferences),
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontFamily = regularFont,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TimetableGrid(props: TimetableProps) {
    val prefs = props.prefs

    val totalPages = Int.MAX_VALUE
    val initialPage = totalPages / 2
    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { totalPages },
        )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val currentDate = LocalDate.now().plusWeeks((page - initialPage).toLong())
                props.updateCurrentMonth(currentDate)
            }
    }

    LaunchedEffect(props.selectNowDay) {
        if (props.selectNowDay) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(initialPage)
                props.updateSelectNowDay()
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        key = { it },
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
    ) { pageIndex ->
        val currentDate =
            remember(pageIndex) {
                LocalDate.now().plusWeeks((pageIndex - initialPage).toLong())
            }

        LaunchedEffect(currentDate) {
            props.changePage(currentDate)
        }

        val scheduleList = props.scheduleByDate[currentDate].orEmpty()

        val daysOfMonth =
            props.getDaysOfMonthOfWeek(
                prefs.isMondayFirstDayOfWeek,
                prefs.showSaturday,
                prefs.showSunday,
                currentDate,
            )

        val daysOfWeek =
            props
                .getDaysOfWeekOrder(
                    prefs.isMondayFirstDayOfWeek,
                    prefs.showSaturday,
                    prefs.showSunday,
                ).map { stringResource(it) }

        val dayMap = daysOfWeek.zip(daysOfMonth).toMap()

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ScheduleHeader(
                daysMap = dayMap,
                currentDate = currentDate,
                isGridMode = prefs.showAsGrid,
                onDayClick = {},
            )
            SchedulesGrid(
                showSaturday = prefs.showSaturday,
                showSunday = prefs.showSunday,
                is12HoursFormat = prefs.is12HoursFormat,
                isMondayFirstDayOfWeek = prefs.isMondayFirstDayOfWeek,
                schedules = scheduleList.map { it.asScheduleView() },
                onScheduleClick = props.onClickSchedule,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TimetableList(props: TimetableProps) {
    val prefs = props.prefs

    val visibleDates =
        remember(prefs) {
            val daysBefore = 365
            val daysAfter = 365
            val today = LocalDate.now()
            val range = (0 - daysBefore)..daysAfter

            range.map { today.plusDays(it.toLong()) }.filter { date ->
                when (date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> prefs.showSaturday
                    DayOfWeek.SUNDAY -> prefs.showSunday
                    else -> true
                }
            }
        }

    val initialPage =
        remember(visibleDates) {
            val today = LocalDate.now()

            val todayIndex = visibleDates.indexOf(today)
            if (todayIndex != -1) return@remember todayIndex

            val afterToday = visibleDates.indexOfFirst { it.isAfter(today) }
            if (afterToday != -1) return@remember afterToday

            visibleDates.indexOfLast { it.isBefore(today) }.coerceAtLeast(0)
        }

    val coroutineScope = rememberCoroutineScope()

    key(prefs) {
        val pagerState =
            rememberPagerState(initialPage = initialPage, pageCount = { visibleDates.size })

        val headerPagerState =
            rememberPagerState(
                initialPage = initialPage,
                pageCount = { visibleDates.size },
            )

        val currentDate =
            remember(pagerState.currentPage) {
                visibleDates[pagerState.currentPage]
            }

        LaunchedEffect(currentDate) {
            props.changePage(currentDate)
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }.collect { page ->
                val selectedDate = visibleDates[page]
                val currentHeaderDate =
                    visibleDates[initialPage].plusWeeks((headerPagerState.currentPage - initialPage).toLong())
                val currentWeekDates =
                    props.getDaysOfMonthOfWeek(
                        prefs.isMondayFirstDayOfWeek,
                        prefs.showSaturday,
                        prefs.showSunday,
                        currentHeaderDate,
                    )
                props.updateCurrentMonth(selectedDate)
                if (!currentWeekDates.contains(selectedDate)) {
                    val nextHeaderPage =
                        if (selectedDate < currentWeekDates.first()) headerPagerState.currentPage - 1 else headerPagerState.currentPage + 1
                    headerPagerState.animateScrollToPage(nextHeaderPage)
                }
            }
        }

        LaunchedEffect(props.selectNowDay) {
            if (props.selectNowDay) {
                coroutineScope.launch {
                    headerPagerState.animateScrollToPage(initialPage)
                    pagerState.animateScrollToPage(initialPage)
                    props.updateSelectNowDay()
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = headerPagerState,
                key = { visibleDates[it] },
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                beyondViewportPageCount = 1,
            ) { pageIndex ->
                val headerDate =
                    visibleDates[initialPage].plusWeeks((pageIndex - initialPage).toLong())

                val daysOfWeekOfMonth =
                    props.getDaysOfMonthOfWeek(
                        prefs.isMondayFirstDayOfWeek,
                        prefs.showSaturday,
                        prefs.showSunday,
                        headerDate,
                    )

                val daysOfWeek =
                    props
                        .getDaysOfWeekOrder(
                            prefs.isMondayFirstDayOfWeek,
                            prefs.showSaturday,
                            prefs.showSunday,
                        ).map { stringResource(it) }

                val diasMap = daysOfWeek.zip(daysOfWeekOfMonth).toMap()

                ScheduleHeader(diasMap, currentDate, prefs.showAsGrid) { clickedDate ->
                    val targetIndex = visibleDates.indexOf(clickedDate)
                    if (targetIndex != -1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetIndex)
                        }
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                key = { visibleDates[it] },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                beyondViewportPageCount = 1,
            ) { pageIndex ->
                val date = visibleDates[pageIndex]
                val scheduleList =
                    if (prefs.showAsGrid) emptyList() else props.scheduleByDate[date].orEmpty()

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(scheduleList.map { it.asScheduleView() }) { schedule ->
                            val startTime =
                                if (prefs.is12HoursFormat) formatLocalTime(schedule.startTime) else schedule.startTime.toString()
                            val endTime =
                                if (prefs.is12HoursFormat) formatLocalTime(schedule.endTime) else schedule.endTime.toString()
                            ScheduleListItem(
                                courseName = schedule.courseName,
                                timeRange = "$startTime-$endTime",
                                classroom = schedule.classPlace,
                                backgroundColor = schedule.color,
                                onClick = { props.onClickSchedule(schedule.id) },
                            )
                        }
                    }

                    if (!prefs.showAsGrid && scheduleList.isEmpty()) {
                        val regularFont =
                            FontFamily(
                                Font(
                                    com.studentsapps.designsystem.R.font.roboto_regular,
                                    FontWeight.Normal,
                                ),
                            )

                        Text(
                            text = stringResource(com.studentsapps.schedule.R.string.no_schedules_this_day),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontFamily = regularFont,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScheduleHeader(
    daysMap: Map<String, LocalDate>,
    currentDate: LocalDate,
    isGridMode: Boolean,
    onDayClick: (LocalDate) -> Unit,
) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                ).padding(bottom = 10.dp),
    ) {
        val hoursCellWidth = dimensionResource(R.dimen.timetable_hours_cell_width)

        val dayCellWidth =
            if (isGridMode) {
                val availableWidth = maxWidth - hoursCellWidth
                availableWidth / daysMap.size
            } else {
                maxWidth / daysMap.size
            }

        Row(
            modifier = Modifier.padding(start = if (isGridMode) hoursCellWidth else 0.dp),
        ) {
            for ((dayName, date) in daysMap) {
                val isSelected =
                    if (isGridMode) {
                        date == LocalDate.now()
                    } else {
                        date == currentDate
                    }

                val isToday =
                    if (isGridMode && date == LocalDate.now()) {
                        true
                    } else if (!isGridMode) {
                        date == LocalDate.now()
                    } else {
                        false
                    }

                WeekDayCell(
                    dayLabel = dayName,
                    dayNumber = date.dayOfMonth,
                    cellWidth = dayCellWidth,
                    isSelected = isSelected,
                    isToday = isToday,
                    onClick = { onDayClick(date) },
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun WeekDayCell(
    dayLabel: String,
    dayNumber: Int,
    cellWidth: Dp,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val regularFont =
        FontFamily(
            Font(
                com.studentsapps.designsystem.R.font.roboto_regular,
                FontWeight.Normal,
            ),
        )

    val mediumFont =
        FontFamily(
            Font(
                com.studentsapps.designsystem.R.font.roboto_medium,
                FontWeight.Normal,
            ),
        )

    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        targetValue =
            when {
                isSelected && isToday -> MaterialTheme.colorScheme.background
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> Color.Transparent
            },
        animationSpec = tween(durationMillis = 250),
    )

    val textColor by animateColorAsState(
        targetValue =
            when {
                isSelected -> MaterialTheme.colorScheme.secondary
                isToday -> MaterialTheme.colorScheme.background
                else -> MaterialTheme.colorScheme.onPrimary
            },
        animationSpec = tween(durationMillis = 250),
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .width(cellWidth)
                .padding(horizontal = 5.dp),
    ) {
        Text(
            text = dayLabel,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 13.sp,
            fontFamily = regularFont,
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier =
                Modifier
                    .background(
                        color = backgroundColor,
                        shape = CircleShape,
                    ).size(30.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dayNumber.toString(),
                color = textColor,
                fontSize = 15.sp,
                fontFamily = mediumFont,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun SchedulesGrid(
    modifier: Modifier = Modifier,
    showSaturday: Boolean,
    showSunday: Boolean,
    is12HoursFormat: Boolean,
    isMondayFirstDayOfWeek: Boolean,
    schedules: List<ScheduleView>,
    onScheduleClick: (String) -> Unit,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val hoursCellWidth = dimensionResource(R.dimen.timetable_hours_cell_width)
    val hoursCellWidthPx = with(density) { hoursCellWidth.toPx() }
    val columnCount = getColumnsNumber(showSaturday, showSunday)
    val gridCellHeight = dimensionResource(R.dimen.timetable_grid_cell_height)
    val gridCellHeightPx = with(density) { gridCellHeight.toPx() }
    val canvasHeight = gridCellHeight * 24
    val lineStrokeWidth = dimensionResource(R.dimen.timetable_grid_lines_stroke_width)
    val lineStrokeWidthPx = with(density) { lineStrokeWidth.toPx() }
    val verticalLineCount = getNumVerticalGridLines(showSaturday, showSunday)
    val horizontalLineCount = 24
    val gridLineColor = colorResource(R.color.timetable_default_grid_stroke_color)
    val halfHourLineColor =
        colorResource(R.color.timetable_default_half_hour_grid_stroke_color)
    val hoursTextSize = dimensionResource(R.dimen.timetable_hours_text_size).value.sp
    val hoursTextColor = colorResource(R.color.timetable_default_hours_text_color)
    val hourLabels =
        stringArrayResource(if (is12HoursFormat) R.array.hours_in_12_hour_format else R.array.hours_in_24_hour_format).toList()
    val hourTextXPosition = hoursCellWidthPx / 2
    val scheduleEndPadding = dimensionResource(R.dimen.timetable_schedule_end_margin)
    val scheduleBottomPadding = dimensionResource(R.dimen.timetable_schedule_bottom_margin)
    val scrollState = rememberScrollState()
    var targetOffsetY by remember { mutableStateOf(0) }

    LaunchedEffect(targetOffsetY) {
        scrollState.scrollTo(targetOffsetY)
    }

    BoxWithConstraints(
        modifier =
            modifier
                .verticalScroll(scrollState)
                .heightIn(min = canvasHeight),
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val cellWidthPx =
            calculateGridCellWidth(
                rootViewWidthPx = totalWidthPx,
                hoursCellWidthPx = hoursCellWidthPx,
                columnsNumber = columnCount,
            )

        val cellWidth = with(density) { cellWidthPx.toDp() }

        Canvas(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(canvasHeight),
        ) {
            for (i in 1..verticalLineCount) {
                val x = hoursCellWidthPx + i * cellWidthPx
                drawLine(
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    color = gridLineColor,
                    strokeWidth = lineStrokeWidthPx,
                )
            }

            for (i in 1..horizontalLineCount) {
                val y = i * gridCellHeightPx
                drawLine(
                    start = Offset(hoursCellWidthPx, y),
                    end = Offset(size.width, y),
                    color = gridLineColor,
                    strokeWidth = lineStrokeWidthPx,
                )
            }

            for (i in 1..horizontalLineCount) {
                val y = ((i - 1) * gridCellHeightPx) + (gridCellHeightPx / 2)
                drawLine(
                    start = Offset(hoursCellWidthPx, y),
                    end = Offset(size.width, y),
                    color = halfHourLineColor,
                    strokeWidth = lineStrokeWidthPx,
                )
            }

            if (is12HoursFormat) {
                hourLabels.forEachIndexed { index, hour ->
                    val parts = hour.split(" ")
                    parts.forEachIndexed { partIndex, part ->
                        val layoutResult =
                            textMeasurer.measure(
                                text = AnnotatedString(part),
                                style =
                                    TextStyle(
                                        color = hoursTextColor,
                                        fontSize = hoursTextSize,
                                        textAlign = TextAlign.Center,
                                    ),
                            )
                        val baseY = gridCellHeightPx * (index + 1)
                        val y =
                            baseY + (partIndex * layoutResult.size.height) - layoutResult.size.height
                        drawText(
                            textLayoutResult = layoutResult,
                            topLeft = Offset(hourTextXPosition - layoutResult.size.width / 2f, y),
                        )
                    }
                }
            } else {
                hourLabels.forEachIndexed { index, hour ->
                    val layoutResult =
                        textMeasurer.measure(
                            text = AnnotatedString(hour),
                            style =
                                TextStyle(
                                    color = hoursTextColor,
                                    fontSize = hoursTextSize,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                    val y = (gridCellHeightPx * (index + 1)) - (layoutResult.size.height / 2)
                    drawText(
                        textLayoutResult = layoutResult,
                        topLeft = Offset(hourTextXPosition - layoutResult.size.width / 2f, y),
                    )
                }
            }
        }

        schedules.groupByDayOfWeek().forEach { (dayOfWeek, schedulesForOneDayOfWeek) ->
            if ((dayOfWeek != DayOfWeek.SUNDAY || showSunday) && (dayOfWeek != DayOfWeek.SATURDAY || showSaturday)) {
                val scheduleCrossing = schedulesForOneDayOfWeek.getCrossSchedules()
                val uniqueSchedules = schedulesForOneDayOfWeek.getUniqueSchedules()

                scheduleCrossing.forEach { crossSchedules ->
                    crossSchedules.forEachIndexed { index, schedule ->
                        val crossedSchedulesCount = crossSchedules.size
                        val width =
                            when (crossedSchedulesCount) {
                                1 -> calculateSingleScheduleViewWidth(cellWidth, scheduleEndPadding)
                                else ->
                                    calculateCrossScheduleViewWidth(
                                        cellWidth,
                                        crossedSchedulesCount,
                                        scheduleEndPadding,
                                    )
                            }
                        val height =
                            calculateScheduleViewHeight(
                                schedule.startTime,
                                schedule.endTime,
                                gridCellHeight,
                                scheduleBottomPadding,
                            )

                        val ySchedule =
                            calculateTopMarginScheduleView(schedule.startTime, gridCellHeight)

                        val xSchedule =
                            when (crossedSchedulesCount) {
                                1 ->
                                    calculateStartMarginSingleScheduleView(
                                        hoursCellWidth,
                                        cellWidth,
                                        schedule.dayOfWeek,
                                        isMondayFirstDayOfWeek,
                                        showSaturday,
                                        showSunday,
                                    )

                                else ->
                                    calculateStartMarginCrossScheduleView(
                                        hoursCellWidth,
                                        cellWidth,
                                        schedule.dayOfWeek,
                                        isMondayFirstDayOfWeek,
                                        showSaturday,
                                        showSunday,
                                        crossedSchedulesCount,
                                        index,
                                    )
                            }
                        TimetableScheduleItem(
                            courseName = schedule.courseName,
                            location = schedule.classPlace,
                            height = height,
                            width = width,
                            modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule),
                            backgroundColor = schedule.color,
                            onClick = { onScheduleClick(schedule.id) },
                        )
                    }
                }

                uniqueSchedules.forEach { uniqueSchedule ->
                    val width = calculateSingleScheduleViewWidth(cellWidth, scheduleEndPadding)

                    val height =
                        calculateScheduleViewHeight(
                            uniqueSchedule.startTime,
                            uniqueSchedule.endTime,
                            gridCellHeight,
                            scheduleBottomPadding,
                        )

                    val ySchedule =
                        calculateTopMarginScheduleView(uniqueSchedule.startTime, gridCellHeight)

                    val xSchedule =
                        calculateStartMarginSingleScheduleView(
                            hoursCellWidth,
                            cellWidth,
                            uniqueSchedule.dayOfWeek,
                            isMondayFirstDayOfWeek,
                            showSaturday,
                            showSunday,
                        )

                    TimetableScheduleItem(
                        courseName = uniqueSchedule.courseName,
                        location = uniqueSchedule.classPlace,
                        height = height,
                        width = width,
                        modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule),
                        backgroundColor = uniqueSchedule.color,
                        onClick = { onScheduleClick(uniqueSchedule.id) },
                    )
                }
            }
        }

        val topCurrentHour = calculateSelectorTopMargin(gridCellHeightPx)

        Box(
            modifier =
                Modifier
                    .absoluteOffset(
                        x = hoursCellWidth,
                        y = with(density) { topCurrentHour.toDp() },
                    ).size(width = maxWidth - hoursCellWidth, 1.dp)
                    .background(MaterialTheme.colorScheme.background),
        )

        Box(
            modifier =
                Modifier
                    .absoluteOffset(
                        x = hoursCellWidth,
                        y = with(density) { topCurrentHour.toDp() - 3.dp },
                    ).size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
        )

        targetOffsetY = topCurrentHour.toInt() - (gridCellHeightPx * 4).toInt()
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TimetableScheduleItem(
    modifier: Modifier = Modifier,
    courseName: String,
    location: String?,
    height: Dp,
    width: Dp,
    backgroundColor: Int,
    onClick: () -> Unit,
) {
    val textColor =
        if (ColorUtils.calculateLuminance(backgroundColor) < 0.5) {
            colorResource(R.color.timetable_schedule_view_light_text_color)
        } else {
            colorResource(R.color.timetable_schedule_view_dark_text_color)
        }

    val fontFamilyMedium =
        FontFamily(
            Font(
                com.studentsapps.designsystem.R.font.roboto_medium,
                FontWeight.Normal,
            ),
        )

    Column(
        modifier =
            modifier
                .size(width, height)
                .clickable { onClick() }
                .clip(RoundedCornerShape(5.dp))
                .background(Color(backgroundColor))
                .padding(5.dp),
    ) {
        Text(
            text = courseName,
            fontFamily = fontFamilyMedium,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
        location?.let {
            Text(
                text = it,
                fontFamily = fontFamilyMedium,
                color = textColor,
                fontSize = 12.sp,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScheduleListItem(
    modifier: Modifier = Modifier,
    courseName: String,
    timeRange: String,
    classroom: String? = null,
    backgroundColor: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(backgroundColor),
            ),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            val textColor =
                if (ColorUtils.calculateLuminance(backgroundColor) < 0.5) {
                    colorResource(R.color.timetable_schedule_view_light_text_color)
                } else {
                    colorResource(R.color.timetable_schedule_view_dark_text_color)
                }

            Text(
                text = courseName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = textColor,
                modifier = Modifier.padding(bottom = 5.dp),
            )

            Text(
                text = timeRange,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor,
            )

            if (!classroom.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = textColor,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = classroom,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor,
                    )
                }
            }
        }
    }
}

private fun formatLocalTime(localTime: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return localTime.format(formatter)
}

private fun calculateSelectorTopMargin(gridCellHeight: Float): Float {
    val currentHour = LocalTime.now().hour
    val currentMinute = LocalTime.now().minute
    return (gridCellHeight * currentHour) + (gridCellHeight * (currentMinute / 60f)).toInt()
}

fun calculateTopMarginScheduleView(
    startTime: LocalTime,
    cellHeight: Dp,
): Dp {
    val localTimeInDecimals = convertLocalTimeToDecimals(startTime)
    return (localTimeInDecimals * cellHeight.value).dp
}

private fun convertLocalTimeToDecimals(localTime: LocalTime): Double = localTime.hour + (localTime.minute / 60.0)

fun calculateStartMarginCrossScheduleView(
    hoursCellWidth: Dp,
    gridCellWidth: Dp,
    day: DayOfWeek,
    isMondayFirstDayOfWeek: Boolean,
    showSaturday: Boolean,
    showSunday: Boolean,
    crossedSchedulesCount: Int,
    crossScheduleIndex: Int,
): Dp =
    calculateStartMarginSingleScheduleView(
        hoursCellWidth,
        gridCellWidth,
        day,
        isMondayFirstDayOfWeek,
        showSaturday,
        showSunday,
    ) + ((gridCellWidth / crossedSchedulesCount) * crossScheduleIndex)

fun calculateStartMarginSingleScheduleView(
    hoursCellWidth: Dp,
    gridCellWidth: Dp,
    day: DayOfWeek,
    isMondayFirstDayOfWeek: Boolean,
    showSaturday: Boolean,
    showSunday: Boolean,
): Dp =
    if (isMondayFirstDayOfWeek || !showSunday) {
        val offset = if (!showSaturday && day == DayOfWeek.SUNDAY) 1 else 0
        hoursCellWidth + (gridCellWidth * (day.value - 1 - offset))
    } else {
        if (day == DayOfWeek.SUNDAY) {
            hoursCellWidth
        } else {
            hoursCellWidth + (gridCellWidth * day.value)
        }
    }

fun calculateScheduleViewHeight(
    startTime: LocalTime,
    endTime: LocalTime,
    cellHeight: Dp,
    scheduleBottomMargin: Dp,
): Dp {
    val minutesDifference = ChronoUnit.MINUTES.between(startTime, endTime)
    val minutesInDecimals = convertMinutesToDecimals(minutesDifference)
    return (minutesInDecimals * cellHeight.value).dp - scheduleBottomMargin
}

private fun convertMinutesToDecimals(minutes: Long): Double = (minutes / 60) + ((minutes % 60) / 60.0)

fun calculateCrossScheduleViewWidth(
    gridCellWidth: Dp,
    crossedSchedulesCount: Int,
    scheduleEndMargin: Dp,
): Dp = (gridCellWidth / crossedSchedulesCount) - scheduleEndMargin

fun calculateSingleScheduleViewWidth(
    gridCellWidth: Dp,
    scheduleEndMargin: Dp,
): Dp = gridCellWidth - scheduleEndMargin

fun calculateGridCellWidth(
    rootViewWidthPx: Float,
    hoursCellWidthPx: Float,
    columnsNumber: Int,
): Float = (rootViewWidthPx - hoursCellWidthPx) / (columnsNumber - 1)

fun getColumnsNumber(
    showSaturday: Boolean,
    showSunday: Boolean,
): Int {
    var columnsNumber = 8
    if (!showSaturday && !showSunday) {
        columnsNumber -= 2
    } else if (!showSaturday || !showSunday) {
        columnsNumber -= 1
    }
    return columnsNumber
}

fun getNumVerticalGridLines(
    showSaturday: Boolean,
    showSunday: Boolean,
): Int {
    var numHorizontalGridLines = 6
    if (!showSaturday && !showSunday) {
        numHorizontalGridLines -= 2
    } else if (!showSaturday || !showSunday) {
        numHorizontalGridLines -= 1
    }
    return numHorizontalGridLines
}

@Suppress("ktlint:standard:function-naming")
@Preview
@Composable
fun ScheduleListItemPreview() {
    UniversityScheduleTheme {
        ScheduleListItem(Modifier, "Test Course", "10:00 a.m. - 11:00 a.m.", "Classroom 1", 1234) {}
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview
@Composable
fun ScheduleHeaderPreview() {
    val timetableUtils = TimetableUtils()
    val daysOfMonth =
        timetableUtils.getDaysOfMonthOfWeek(
            isMondayFirstDayOfWeek = true,
            showSaturday = true,
            showSunday = true,
        )
    val daysOfWeek =
        timetableUtils
            .getDaysOfWeekOrder(
                isMondayFirstDayOfWeek = true,
                showSaturday = true,
                showSunday = true,
            ).map { stringResource(it) }
    val diasMap = daysOfWeek.zip(daysOfMonth).toMap()
    UniversityScheduleTheme {
        ScheduleHeader(diasMap, LocalDate.now(), true) { }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview
@Composable
fun WeekDayCellPreview() {
    UniversityScheduleTheme {
        WeekDayCell(
            dayLabel = "Lun",
            dayNumber = 7,
            cellWidth = 40.dp,
            isSelected = true,
            isToday = false,
        ) {}
    }
}
