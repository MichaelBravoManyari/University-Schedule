package com.studentsapps.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.studentsapps.model.ScheduleView
import com.studentsapps.model.asScheduleView
import com.studentsapps.model.getCrossSchedules
import com.studentsapps.model.getUniqueSchedules
import com.studentsapps.model.groupByDayOfWeek
import com.studentsapps.schedule.viewmodels.ScheduleUiState
import com.studentsapps.schedule.viewmodels.ScheduleViewModel
import com.studentsapps.ui.R
import com.studentsapps.ui.theme.UniversityScheduleTheme
import com.studentsapps.ui.timetable.TimetableUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.core.graphics.ColorUtils
import com.studentsapps.model.ScheduleDetails
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun DiaSemana(
    dia: String,
    numero: Int,
    ancho: Dp,
    select: Boolean,
    isNow: Boolean,
    onClick: () -> Unit
) {
    val customFont = FontFamily(
        Font(
            com.studentsapps.designsystem.R.font.roboto_regular, FontWeight.Normal
        )
    )

    val customFont1 = FontFamily(
        Font(
            com.studentsapps.designsystem.R.font.roboto_medium, FontWeight.Normal
        )
    )

    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            select && isNow -> MaterialTheme.colorScheme.background
            select -> MaterialTheme.colorScheme.onPrimary
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 250)
    )

    val textColor by animateColorAsState(
        targetValue = when {
            select -> MaterialTheme.colorScheme.secondary
            isNow -> MaterialTheme.colorScheme.background
            else -> MaterialTheme.colorScheme.onPrimary
        },
        animationSpec = tween(durationMillis = 250)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ancho)
            .padding(horizontal = 5.dp)
    ) {
        Text(
            text = dia,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 13.sp,
            fontFamily = customFont
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .size(30.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero.toString(),
                color = textColor,
                fontSize = 15.sp,
                fontFamily = customFont1
            )
        }
    }
}

@Composable
fun HorarioListItem(
    modifier: Modifier = Modifier,
    nombreCurso: String,
    horaInicioFin: String,
    aula: String? = null,
    backgroundColor: Int,
    onClickSchedule: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(backgroundColor)
        ),
        onClick = onClickSchedule
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            val textColor = if (ColorUtils.calculateLuminance(backgroundColor) < 0.5)
                colorResource(R.color.timetable_schedule_view_light_text_color)
            else
                colorResource(R.color.timetable_schedule_view_dark_text_color)
            Text(
                text = nombreCurso,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = textColor,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = horaInicioFin,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            if (!aula.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = aula,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun CabezeraHorario(
    dias: Map<String, LocalDate>,
    currentDate: LocalDate,
    isTimetableModeGrid: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
            .padding(bottom = 10.dp)
    ) {
        val hoursCellWidth = dimensionResource(R.dimen.timetable_hours_cell_width)

        val anchoDiaSemana = if (isTimetableModeGrid) {
            val anchoDisponibleDp = maxWidth - hoursCellWidth
            anchoDisponibleDp / dias.size
        } else {
            maxWidth / dias.size
        }

        Row(
            modifier = Modifier
                .padding(start = if (isTimetableModeGrid) hoursCellWidth else 0.dp)
        ) {
            for (dia in dias) {
                DiaSemana(
                    dia.key,
                    dia.value.dayOfMonth,
                    anchoDiaSemana,
                    if (isTimetableModeGrid)
                        dia.value == LocalDate.now()
                    else
                        dia.value == currentDate,
                    if (isTimetableModeGrid && dia.value == LocalDate.now())
                        true
                    else
                        if (!isTimetableModeGrid)
                            dia.value == LocalDate.now()
                        else
                            false,
                    onClick = { onDayClick(dia.value) }
                )
            }
        }
    }
}

@Composable
fun HorarioTimetableGrid(
    modifier: Modifier = Modifier,
    nombreCurso: String,
    lugar: String?,
    altura: Dp,
    anchura: Dp,
    backgroundColor: Int,
    onClick: () -> Unit
) {
    val textColor = if (ColorUtils.calculateLuminance(backgroundColor) < 0.5)
        colorResource(R.color.timetable_schedule_view_light_text_color)
    else
        colorResource(R.color.timetable_schedule_view_dark_text_color)

    val customFont1 = FontFamily(
        Font(
            com.studentsapps.designsystem.R.font.roboto_medium, FontWeight.Normal
        )
    )

    Column(
        modifier = modifier
            .size(anchura, altura)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(backgroundColor))
            .padding(5.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = nombreCurso,
            fontFamily = customFont1,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        lugar?.let {
            Text(
                text = it,
                fontFamily = customFont1,
                color = textColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun TimetableCompose(viewModel: ScheduleViewModel, onClickSchedule: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is ScheduleUiState.Success) {
        val successState = uiState as ScheduleUiState.Success
        val prefs = successState.timetableUserPreferences

        if (prefs.showAsGrid) {
            TimetableGrid(
                prefs,
                { date ->
                    viewModel.loadScheduleForDate(date)
                },
                successState.scheduleByDate,
                { isMondayFirstDayOfWeek, showSaturday, showSunday, date ->
                    viewModel.getDaysOfMonthOfWeek(
                        isMondayFirstDayOfWeek,
                        showSaturday,
                        showSunday,
                        date
                    )
                },
                { isMondayFirstDayOfWeek, showSaturday, showSunday ->
                    viewModel.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
                },
                onClickSchedule = onClickSchedule
            )
        } else {
            TimetableList(
                prefs,
                { date ->
                    viewModel.loadScheduleForDate(date)
                },
                successState.scheduleByDate,
                { isMondayFirstDayOfWeek, showSaturday, showSunday, date ->
                    viewModel.getDaysOfMonthOfWeek(
                        isMondayFirstDayOfWeek,
                        showSaturday,
                        showSunday,
                        date
                    )
                },
                { isMondayFirstDayOfWeek, showSaturday, showSunday ->
                    viewModel.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
                },
                onClickSchedule = onClickSchedule
            )
        }
    } else {
        Text("Cargando preferencias...")
    }
}

@Composable
fun TimetableGrid(
    prefs: TimetableUserPreferences,
    changePag: (LocalDate) -> Unit,
    scheduleByDate: Map<LocalDate, List<ScheduleDetails>>,
    getDaysOfMonthOfWeek: (isMondayFirstDayOfWeek: Boolean, showSaturday: Boolean, showSunday: Boolean, date: LocalDate) -> List<LocalDate>,
    getDaysOfWeekOrder: (isMondayFirstDayOfWeek: Boolean, showSaturday: Boolean, showSunday: Boolean) -> List<Int>,
    onClickSchedule: (String) -> Unit,
) {
    val totalPages = Int.MAX_VALUE
    val initialPage = totalPages / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage, pageCount = { totalPages })

    HorizontalPager(
        state = pagerState,
        key = { it },
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1
    ) { pageIndex ->
        val date = remember(pageIndex) {
            LocalDate.now().plusWeeks((pageIndex - initialPage).toLong())
        }

        LaunchedEffect(date) {
            changePag(date)
        }

        val scheduleList = scheduleByDate[date].orEmpty()

        val daysOfWeekOfMonth = getDaysOfMonthOfWeek(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday, date
        )

        val daysOfWeek = getDaysOfWeekOrder(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday
        ).map { stringResource(it) }

        val diasMap = daysOfWeek.zip(daysOfWeekOfMonth).toMap()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CabezeraHorario(diasMap, date, prefs.showAsGrid) {}
            SchedulesGrid(
                showSaturday = prefs.showSaturday,
                showSunday = prefs.showSunday,
                is12HoursFormat = prefs.is12HoursFormat,
                isMondayFirstDayOfWeek = prefs.isMondayFirstDayOfWeek,
                schedules = scheduleList.map { it.asScheduleView() },
                onClickSchedule = onClickSchedule
            )
        }
    }
}

@Composable
fun TimetableList(
    prefs: TimetableUserPreferences,
    changePag: (LocalDate) -> Unit,
    scheduleByDate: Map<LocalDate, List<ScheduleDetails>>,
    getDaysOfMonthOfWeek: (isMondayFirstDayOfWeek: Boolean, showSaturday: Boolean, showSunday: Boolean, date: LocalDate) -> List<LocalDate>,
    getDaysOfWeekOrder: (isMondayFirstDayOfWeek: Boolean, showSaturday: Boolean, showSunday: Boolean) -> List<Int>,
    onClickSchedule: (String) -> Unit,
) {
    val visibleDates = remember(prefs) {
        val daysBefore = 365
        val daysAfter = 365
        val today = LocalDate.now()
        val range = (0 - daysBefore)..daysAfter

        range.map { today.plusDays(it.toLong()) }
            .filter { date ->
                when (date.dayOfWeek) {
                    DayOfWeek.SATURDAY -> prefs.showSaturday
                    DayOfWeek.SUNDAY -> prefs.showSunday
                    else -> true
                }
            }
    }

    val initialPage = remember(visibleDates) {
        val today = LocalDate.now()

        val indexToday = visibleDates.indexOf(today)
        if (indexToday != -1) return@remember indexToday

        val afterToday = visibleDates.indexOfFirst { it.isAfter(today) }
        if (afterToday != -1) return@remember afterToday

        visibleDates.indexOfLast { it.isBefore(today) }.coerceAtLeast(0)
    }

    val coroutineScope = rememberCoroutineScope()

    key(prefs) {
        val pagerState =
            rememberPagerState(initialPage = initialPage, pageCount = { visibleDates.size })

        val pagerCabezeraState = rememberPagerState(
            initialPage = initialPage, pageCount = { visibleDates.size }
        )

        val date1 = remember(pagerState.currentPage) {
            visibleDates[pagerState.currentPage]
        }

        LaunchedEffect(date1) {
            changePag(date1)
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }
                .collect { page ->
                    val dateContent = visibleDates[page]
                    val curentDateCabezera =
                        visibleDates[initialPage].plusWeeks((pagerCabezeraState.currentPage - initialPage).toLong())
                    val rangeDate = getDaysOfMonthOfWeek(
                        prefs.isMondayFirstDayOfWeek,
                        prefs.showSaturday,
                        prefs.showSunday,
                        curentDateCabezera
                    )
                    if (!rangeDate.contains(dateContent)) {
                        val navigatePageCabezeraPager =
                            if (dateContent < rangeDate.first()) pagerCabezeraState.currentPage - 1 else pagerCabezeraState.currentPage + 1
                        pagerCabezeraState.animateScrollToPage(navigatePageCabezeraPager)
                    }
                }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            WrapContentHorizontalPager(
                state = pagerCabezeraState,
                key = { visibleDates[it] },
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val currentDate =
                    visibleDates[initialPage].plusWeeks((pageIndex - initialPage).toLong())

                val daysOfWeekOfMonth = getDaysOfMonthOfWeek(
                    prefs.isMondayFirstDayOfWeek,
                    prefs.showSaturday,
                    prefs.showSunday,
                    currentDate
                )

                val daysOfWeek = getDaysOfWeekOrder(
                    prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday
                ).map { stringResource(it) }

                val diasMap = daysOfWeek.zip(daysOfWeekOfMonth).toMap()

                CabezeraHorario(diasMap, date1, prefs.showAsGrid) { clickedDate ->
                    val index = visibleDates.indexOf(clickedDate)
                    if (index != -1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                key = { visibleDates[it] },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val currentDate = visibleDates[pageIndex]

                val scheduleList = scheduleByDate[currentDate].orEmpty()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(scheduleList.map { it.asScheduleView() }) { schedule ->
                            val horaInicio =
                                if (prefs.is12HoursFormat) formatLocalTime(schedule.startTime) else schedule.startTime.toString()
                            val horaFin =
                                if (prefs.is12HoursFormat) formatLocalTime(schedule.endTime) else schedule.endTime.toString()
                            HorarioListItem(
                                nombreCurso = schedule.courseName,
                                horaInicioFin = "$horaInicio-$horaFin",
                                aula = schedule.classPlace,
                                backgroundColor = schedule.color,
                                onClickSchedule = { onClickSchedule(schedule.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatLocalTime(localTime: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return localTime.format(formatter)
}

@Composable
fun WrapContentHorizontalPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    key: ((index: Int) -> Any)? = null,
    userScrollEnabled: Boolean = true,
    beyondViewportPageCount: Int = 0,
    content: @Composable (page: Int) -> Unit
) {
    var pageHeight by remember { mutableIntStateOf(0) }

    SubcomposeLayout(modifier = modifier) { constraints ->
        val placeables = subcompose("measure") {
            Box(Modifier.fillMaxWidth()) {
                content(state.currentPage)
            }
        }.map { it.measure(constraints) }

        val measuredHeight = placeables.maxOfOrNull { it.height } ?: constraints.minHeight
        pageHeight = measuredHeight

        layout(0, 0) {}
    }

    HorizontalPager(
        state = state,
        key = key,
        userScrollEnabled = userScrollEnabled,
        beyondViewportPageCount = beyondViewportPageCount,
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { pageHeight.toDp() })
    ) { pageIndex ->
        content(pageIndex)
    }
}

@Composable
fun SchedulesGrid(
    modifier: Modifier = Modifier,
    showSaturday: Boolean,
    showSunday: Boolean,
    is12HoursFormat: Boolean,
    isMondayFirstDayOfWeek: Boolean,
    schedules: List<ScheduleView>,
    onClickSchedule: (String) -> Unit
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val hoursCellWidth = dimensionResource(R.dimen.timetable_hours_cell_width)
    val hoursCellWidthPx = with(density) { hoursCellWidth.toPx() }
    val columnsNumber = getColumnsNumber(showSaturday, showSunday)
    val gridCellHeight = dimensionResource(R.dimen.timetable_grid_cell_height)
    val gridCellHeightPx = with(density) { gridCellHeight.toPx() }
    val canvasHeight = gridCellHeight * 24
    val lineWidth = dimensionResource(R.dimen.timetable_grid_lines_stroke_width)
    val lineWidthPx = with(density) { lineWidth.toPx() }
    val numVerticalGridLines = getNumVerticalGridLines(showSaturday, showSunday)
    val numHorizontalGridLines = 24
    val gridStrokeColor = colorResource(R.color.timetable_default_grid_stroke_color)
    val halfHourGridStrokeColor =
        colorResource(R.color.timetable_default_half_hour_grid_stroke_color)
    val hoursTextSize = dimensionResource(R.dimen.timetable_hours_text_size).value.sp
    val hoursTextColor = colorResource(R.color.timetable_default_hours_text_color)
    val hoursText =
        stringArrayResource(if (is12HoursFormat) R.array.hours_in_12_hour_format else R.array.hours_in_24_hour_format).toList()
    val xDrawText = hoursCellWidthPx / 2
    val scheduleEndPadding = dimensionResource(R.dimen.timetable_schedule_end_margin)
    //val scheduleEndPaddingPx = with(density) { scheduleEndPadding.toPx() }
    val scheduleBottomPadding = dimensionResource(R.dimen.timetable_schedule_bottom_margin)
    //val scheduleBottomPaddingPx = with(density) { scheduleBottomPadding.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .heightIn(min = canvasHeight)
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val gridCellWidthPx = calculateGridCellWidth(
            rootViewWidthPx = totalWidthPx,
            hoursCellWidthPx = hoursCellWidthPx,
            columnsNumber = columnsNumber
        )

        val gridCellWidth = with(density) { gridCellWidthPx.toDp() }

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(canvasHeight)
        ) {
            for (i in 1..numVerticalGridLines) {
                val x = hoursCellWidthPx + i * gridCellWidthPx
                drawLine(
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    color = gridStrokeColor,
                    strokeWidth = lineWidthPx
                )
            }

            for (i in 1..numHorizontalGridLines) {
                val y = i * gridCellHeightPx
                drawLine(
                    start = Offset(hoursCellWidthPx, y),
                    end = Offset(size.width, y),
                    color = gridStrokeColor,
                    strokeWidth = lineWidthPx
                )
            }

            for (i in 1..numHorizontalGridLines) {
                val y = ((i - 1) * gridCellHeightPx) + (gridCellHeightPx / 2)
                drawLine(
                    start = Offset(hoursCellWidthPx, y),
                    end = Offset(size.width, y),
                    color = halfHourGridStrokeColor,
                    strokeWidth = lineWidthPx
                )
            }

            if (is12HoursFormat) {
                hoursText.forEachIndexed { index, hour ->
                    val parts = hour.split(" ")
                    parts.forEachIndexed { partIndex, part ->
                        val layoutResult = textMeasurer.measure(
                            text = AnnotatedString(part), style = TextStyle(
                                color = hoursTextColor,
                                fontSize = hoursTextSize,
                                textAlign = TextAlign.Center
                            )
                        )
                        val baseY = gridCellHeightPx * (index + 1)
                        val y =
                            baseY + (partIndex * layoutResult.size.height) - layoutResult.size.height
                        drawText(
                            textLayoutResult = layoutResult,
                            topLeft = Offset(xDrawText - layoutResult.size.width / 2f, y)
                        )
                    }
                }
            } else {
                hoursText.forEachIndexed { index, hour ->
                    val layoutResult = textMeasurer.measure(
                        text = AnnotatedString(hour), style = TextStyle(
                            color = hoursTextColor,
                            fontSize = hoursTextSize,
                            textAlign = TextAlign.Center
                        )
                    )
                    val y = (gridCellHeightPx * (index + 1)) - (layoutResult.size.height / 2)
                    drawText(
                        textLayoutResult = layoutResult,
                        topLeft = Offset(xDrawText - layoutResult.size.width / 2f, y)
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
                        val width = when (crossedSchedulesCount) {
                            1 -> calculateSingleScheduleViewWidth(gridCellWidth, scheduleEndPadding)
                            else -> calculateCrossScheduleViewWidth(
                                gridCellWidth, crossedSchedulesCount, scheduleEndPadding
                            )
                        }
                        val height = calculateScheduleViewHeight(
                            schedule.startTime,
                            schedule.endTime,
                            gridCellHeight,
                            scheduleBottomPadding
                        )

                        val ySchedule =
                            calculateTopMarginScheduleView(schedule.startTime, gridCellHeight)

                        val xSchedule = when (crossedSchedulesCount) {
                            1 -> calculateStartMarginSingleScheduleView(
                                hoursCellWidth,
                                gridCellWidth,
                                schedule.dayOfWeek,
                                isMondayFirstDayOfWeek,
                                showSaturday,
                                showSunday
                            )

                            else -> calculateStartMarginCrossScheduleView(
                                hoursCellWidth,
                                gridCellWidth,
                                schedule.dayOfWeek,
                                isMondayFirstDayOfWeek,
                                showSaturday,
                                showSunday,
                                crossedSchedulesCount,
                                index
                            )
                        }
                        HorarioTimetableGrid(
                            nombreCurso = schedule.courseName,
                            lugar = schedule.classPlace,
                            altura = height,
                            anchura = width,
                            modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule),
                            backgroundColor = schedule.color,
                            onClick = { onClickSchedule(schedule.id) }
                        )
                    }
                }

                uniqueSchedules.forEach { uniqueSchedule ->
                    val width = calculateSingleScheduleViewWidth(gridCellWidth, scheduleEndPadding)

                    val height = calculateScheduleViewHeight(
                        uniqueSchedule.startTime,
                        uniqueSchedule.endTime,
                        gridCellHeight,
                        scheduleBottomPadding
                    )

                    val ySchedule =
                        calculateTopMarginScheduleView(uniqueSchedule.startTime, gridCellHeight)

                    val xSchedule = calculateStartMarginSingleScheduleView(
                        hoursCellWidth,
                        gridCellWidth,
                        uniqueSchedule.dayOfWeek,
                        isMondayFirstDayOfWeek,
                        showSaturday,
                        showSunday
                    )

                    HorarioTimetableGrid(
                        nombreCurso = uniqueSchedule.courseName,
                        lugar = uniqueSchedule.classPlace,
                        altura = height,
                        anchura = width,
                        modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule),
                        backgroundColor = uniqueSchedule.color,
                        onClick = { onClickSchedule(uniqueSchedule.id) }
                    )
                }
            }
        }
    }
}

fun calculateTopMarginScheduleView(startTime: LocalTime, cellHeight: Dp): Dp {
    val localTimeInDecimals = convertLocalTimeToDecimals(startTime)
    return (localTimeInDecimals * cellHeight.value).dp
}

private fun convertLocalTimeToDecimals(localTime: LocalTime): Double {
    return localTime.hour + (localTime.minute / 60.0)
}

fun calculateStartMarginCrossScheduleView(
    hoursCellWidth: Dp,
    gridCellWidth: Dp,
    day: DayOfWeek,
    isMondayFirstDayOfWeek: Boolean,
    showSaturday: Boolean,
    showSunday: Boolean,
    crossedSchedulesCount: Int,
    crossScheduleIndex: Int
): Dp {
    return calculateStartMarginSingleScheduleView(
        hoursCellWidth, gridCellWidth, day, isMondayFirstDayOfWeek, showSaturday, showSunday
    ) + ((gridCellWidth / crossedSchedulesCount) * crossScheduleIndex)
}

fun calculateStartMarginSingleScheduleView(
    hoursCellWidth: Dp,
    gridCellWidth: Dp,
    day: DayOfWeek,
    isMondayFirstDayOfWeek: Boolean,
    showSaturday: Boolean,
    showSunday: Boolean
): Dp {
    return if (isMondayFirstDayOfWeek || !showSunday) {
        val offset = if (!showSaturday && day == DayOfWeek.SUNDAY) 1 else 0
        hoursCellWidth + (gridCellWidth * (day.value - 1 - offset))
    } else {
        if (day == DayOfWeek.SUNDAY) {
            hoursCellWidth
        } else hoursCellWidth + (gridCellWidth * day.value)
    }
}

fun calculateScheduleViewHeight(
    startTime: LocalTime, endTime: LocalTime, cellHeight: Dp, scheduleBottomMargin: Dp
): Dp {
    val minutesDifference = ChronoUnit.MINUTES.between(startTime, endTime)
    val minutesInDecimals = convertMinutesToDecimals(minutesDifference)
    return (minutesInDecimals * cellHeight.value).dp - scheduleBottomMargin
}

private fun convertMinutesToDecimals(minutes: Long): Double {
    return (minutes / 60) + ((minutes % 60) / 60.0)
}

fun calculateCrossScheduleViewWidth(
    gridCellWidth: Dp, crossedSchedulesCount: Int, scheduleEndMargin: Dp
): Dp {
    return (gridCellWidth / crossedSchedulesCount) - scheduleEndMargin
}

fun calculateSingleScheduleViewWidth(gridCellWidth: Dp, scheduleEndMargin: Dp): Dp {
    return gridCellWidth - scheduleEndMargin
}

fun calculateGridCellWidth(
    rootViewWidthPx: Float, hoursCellWidthPx: Float, columnsNumber: Int
): Float {
    return (rootViewWidthPx - hoursCellWidthPx) / (columnsNumber - 1)
}

fun getColumnsNumber(showSaturday: Boolean, showSunday: Boolean): Int {
    var columnsNumber = 8
    if (!showSaturday && !showSunday) columnsNumber -= 2
    else if (!showSaturday || !showSunday) columnsNumber -= 1
    return columnsNumber
}

fun getNumVerticalGridLines(showSaturday: Boolean, showSunday: Boolean): Int {
    var numHorizontalGridLines = 6
    if (!showSaturday && !showSunday) numHorizontalGridLines -= 2
    else if (!showSaturday || !showSunday) numHorizontalGridLines -= 1
    return numHorizontalGridLines
}

@Preview
@Composable
fun HorarioListItemPreview() {
    UniversityScheduleTheme {
        HorarioListItem(Modifier, "Curso prueba", "10:00 a.m. - 11:00 a.m.", "Edificio 1", 1234, {})
    }
}

@Preview
@Composable
fun CabezeraHorarioPreview() {
    val timetableUtils = TimetableUtils()
    val daysOfMonth = timetableUtils.getDaysOfMonthOfWeek(true, true, true)
    val daysOfWeek = timetableUtils.getDaysOfWeekOrder(true, true, true).map { stringResource(it) }
    val diasMap = daysOfWeek.zip(daysOfMonth).toMap()
    UniversityScheduleTheme {
        CabezeraHorario(diasMap, LocalDate.now(), true) { }
    }
}

@Preview
@Composable
fun DiaSemanaSelectPreview() {
    UniversityScheduleTheme {
        DiaSemana("Lun", 7, 40.dp, true, false) { }
    }
}