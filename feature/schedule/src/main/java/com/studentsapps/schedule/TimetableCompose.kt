package com.studentsapps.schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

@Composable
fun DiaSemana(dia: String, numero: Int, ancho: Dp, select: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ancho)
            .padding(horizontal = 5.dp)
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

        Text(
            text = dia,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 15.sp,
            fontFamily = customFont
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .background(
                    color = if (select) Color.Blue else Color.White, shape = CircleShape
                )
                .size(25.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero.toString(),
                color = if (select) Color.White else MaterialTheme.colorScheme.onPrimary,
                fontSize = 13.sp,
                fontFamily = customFont1
            )
        }
    }
}

@Composable
fun HorarioListItem(
    nombreCurso: String,
    horaInicioFin: String,
    aula: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(15.dp)
        ) {
            Text(
                text = nombreCurso,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = horaInicioFin,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (!aula.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = aula,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
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
    isTimetableModeGrid: Boolean
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val hoursCellWidth = dimensionResource(R.dimen.timetable_hours_cell_width)

        val anchoDiaSemana = if (isTimetableModeGrid) {
            val anchoDisponibleDp = maxWidth - hoursCellWidth
            anchoDisponibleDp / dias.size
        } else {
            maxWidth / dias.size
        }

        Row(modifier = Modifier.padding(start = if (isTimetableModeGrid) hoursCellWidth else 0.dp)) {
            for (dia in dias) {
                DiaSemana(
                    dia.key,
                    dia.value.dayOfMonth,
                    anchoDiaSemana,
                    if (isTimetableModeGrid)
                        dia.value == LocalDate.now()
                    else
                        dia.value == currentDate
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
    anchura: Dp
) {
    Column(
        modifier = modifier
            .size(anchura, altura)
            .background(Color.LightGray)
    ) {
        Text(text = nombreCurso)
        lugar?.let { Text(it) }
    }
}

@Composable
fun TimetableCompose(viewModel: ScheduleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is ScheduleUiState.Success) {
        val successState = uiState as ScheduleUiState.Success
        val prefs = successState.timetableUserPreferences

        val totalPages = Int.MAX_VALUE
        val initialPage = totalPages / 2
        val pagerState = rememberPagerState(
            initialPage = initialPage, pageCount = { totalPages })

        HorizontalPager(
            state = pagerState, key = { it }, modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val weekOffset = pageIndex - initialPage
            val currentDate =
                if (prefs.showAsGrid)
                    LocalDate.now().plusWeeks(weekOffset.toLong())
                else
                    LocalDate.now().plusDays(weekOffset.toLong())

            LaunchedEffect(pagerState.currentPage) {
                if (prefs.showAsGrid) {
                    viewModel.updateScheduleDetailsListInGridMode(currentDate)
                } else {
                    viewModel.updateScheduleDetailsListInListMode(currentDate)
                }
            }

            val daysOfWeekOfMonth = viewModel.getDaysOfMonthOfWeek(
                isMondayFirstDayOfWeek = prefs.isMondayFirstDayOfWeek,
                showSaturday = prefs.showSaturday,
                showSunday = prefs.showSunday,
                date = currentDate
            )

            val daysOfWeek = viewModel.getDaysOfWeekOrder(
                prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday
            ).map { stringResource(it) }

            val diasMap = daysOfWeek.zip(daysOfWeekOfMonth).toMap()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CabezeraHorario(diasMap, currentDate, prefs.showAsGrid)
                if (prefs.showAsGrid) {
                    TimetableGrid(
                        showSaturday = prefs.showSaturday,
                        showSunday = prefs.showSunday,
                        is12HoursFormat = prefs.is12HoursFormat,
                        isMondayFirstDayOfWeek = prefs.isMondayFirstDayOfWeek,
                        schedules = successState.scheduleDetailsList.map { it.asScheduleView() }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(successState.scheduleDetailsList.map { it.asScheduleView() }) { schedule ->
                            HorarioListItem(
                                nombreCurso = schedule.courseName,
                                horaInicioFin = schedule.startTime.toString() + "-" + schedule.endTime.toString(),
                                aula = schedule.classPlace
                            )
                        }
                    }

                }

            }
        }
    } else {
        Text("Cargando preferencias...")
    }
}

@Composable
fun TimetableGrid(
    modifier: Modifier = Modifier,
    showSaturday: Boolean,
    showSunday: Boolean,
    is12HoursFormat: Boolean,
    isMondayFirstDayOfWeek: Boolean,
    schedules: List<ScheduleView>
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
    val scheduleEndPaddingPx = with(density) { scheduleEndPadding.toPx() }
    val scheduleBottomPadding = dimensionResource(R.dimen.timetable_schedule_bottom_margin)
    val scheduleBottomPaddingPx = with(density) { scheduleBottomPadding.toPx() }

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
                            text = AnnotatedString(part),
                            style = TextStyle(
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
                        text = AnnotatedString(hour),
                        style = TextStyle(
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
                            modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule)
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
                        modifier = Modifier.absoluteOffset(x = xSchedule, y = ySchedule)
                    )
                }
            }
        }

        /*val xCurso = with(density) { (hoursCellWidthPx + 0 * gridCellWidthPx).toDp() }
        val yCurso = with(density) { (10 * gridCellHeight.toPx()).toDp() }
        val anchoCurso = with(density) { gridCellWidthPx.toDp() }
        val altoCurso = gridCellHeight * 2 // 2 horas

        HorarioTimetableGrid(
            nombreCurso = "Matemáticas",
            lugar = "Aula 1",
            altura = altoCurso,
            anchura = anchoCurso,
            modifier = Modifier.absoluteOffset(x = xCurso, y = yCurso)
        )*/
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
        hoursCellWidth,
        gridCellWidth,
        day,
        isMondayFirstDayOfWeek,
        showSaturday,
        showSunday
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
        } else
            hoursCellWidth + (gridCellWidth * day.value)
    }
}

fun calculateScheduleViewHeight(
    startTime: LocalTime,
    endTime: LocalTime,
    cellHeight: Dp,
    scheduleBottomMargin: Dp
): Dp {
    val minutesDifference = ChronoUnit.MINUTES.between(startTime, endTime)
    val minutesInDecimals = convertMinutesToDecimals(minutesDifference)
    return (minutesInDecimals * cellHeight.value).dp - scheduleBottomMargin
}

private fun convertMinutesToDecimals(minutes: Long): Double {
    return (minutes / 60) + ((minutes % 60) / 60.0)
}

fun calculateCrossScheduleViewWidth(
    gridCellWidth: Dp,
    crossedSchedulesCount: Int,
    scheduleEndMargin: Dp
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
        HorarioListItem("Curso prueba", "10:00 a.m. - 11:00 a.m.", "Edificio 1")
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
        CabezeraHorario(diasMap, LocalDate.now(), true)
    }
}

@Preview
@Composable
fun DiaSemanaSelectPreview() {
    UniversityScheduleTheme {
        DiaSemana("Lun", 7, 40.dp, true)
    }
}