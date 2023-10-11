package com.studentsapps.schedule.timetable

import android.util.AttributeSet
import androidx.annotation.FontRes
import com.studentsapps.schedule.R
import org.robolectric.Robolectric

class TimetableAttributeSet {

    private val attrs = Robolectric.buildAttributeSet()

    fun addIsMondayFirstOfWeek(isMondayFirstOfWeek: Boolean): TimetableAttributeSet {
        attrs.addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
        return this
    }

    fun addShowSaturday(showSaturday: Boolean): TimetableAttributeSet {
        attrs.addAttribute(R.attr.show_saturday, showSaturday.toString())
        return this
    }

    fun addShowSunday(showSunday: Boolean): TimetableAttributeSet {
        attrs.addAttribute(R.attr.show_sunday, showSunday.toString())
        return this
    }

    fun addDaysFont(@FontRes fontId: Int, isDayOfWeek: Boolean): TimetableAttributeSet {
        val font =
            if (fontId == R.font.roboto_regular) "@font/roboto_regular" else "@font/roboto_medium"
        val attr = if (isDayOfWeek) R.attr.days_of_week_font else R.attr.days_of_month_font
        attrs.addAttribute(attr, font)
        return this
    }

    fun addIs12HoursFormat(is12HoursFormat: Boolean): TimetableAttributeSet {
        attrs.addAttribute(R.attr.is_12_hours_format, is12HoursFormat.toString())
        return this
    }

    fun build(): AttributeSet {
        return attrs.build()
    }
}