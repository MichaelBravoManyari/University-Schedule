package com.studentsapps.schedule.adapters

import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@BindingAdapter("dayOfWeek")
fun bindSetDayOfWeek(view: MaterialButton, dayOfWeek: DayOfWeek) {
    view.text = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}