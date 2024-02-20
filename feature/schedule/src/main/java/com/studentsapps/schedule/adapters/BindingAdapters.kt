package com.studentsapps.schedule.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@BindingAdapter("dayOfWeek", "specificDate")
fun bindSetDay(view: TextView, dayOfWeek: DayOfWeek?, specificDate: LocalDate?) {
    view.text = specificDate?.toString()
        ?: dayOfWeek?.getDisplayName(TextStyle.FULL, Locale.getDefault())
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

@BindingAdapter("hour")
fun bindSetHour(view: TextView, time: LocalTime?) {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    view.text = time?.format(formatter) ?: ""
}

@BindingAdapter("startTime", "endTime")
fun bindSetHour(view: TextView, startTime: LocalTime?, endTime: LocalTime?) {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    val text = buildString {
        append(startTime?.format(formatter))
        append(" - ")
        append(endTime?.format(formatter))
    }
    view.text = text
}
