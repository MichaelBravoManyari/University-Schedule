package com.studentsapps.schedule.adapters

import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@BindingAdapter("dayOfWeek", "specificDate")
fun bindSetDay(view: MaterialButton, dayOfWeek: DayOfWeek, specificDate: LocalDate?) {
    view.text = specificDate?.toString()
        ?: dayOfWeek.getDisplayName(
            TextStyle.FULL, Locale.getDefault()
        )?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
}