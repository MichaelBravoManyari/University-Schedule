package com.studentsapps.ui.timetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studentsapps.model.ScheduleView
import com.studentsapps.ui.R
import com.studentsapps.ui.databinding.TimetableListItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimetableListAdapter :
    ListAdapter<ScheduleView, TimetableListAdapter.TimetableListViewHolder>(
        DiffCallback
    ) {

    private var is12HoursFormat = true

    var onItemClicked: ((Int) -> Unit)? = null

    fun set12HoursFormat(value: Boolean) {
        is12HoursFormat = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableListViewHolder {
        val viewHolder = TimetableListViewHolder(
            TimetableListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked?.let { it1 -> it1(getItem(position).id) }
        }
        return  viewHolder
    }

    override fun onBindViewHolder(holder: TimetableListViewHolder, position: Int) {
        holder.bind(getItem(position), is12HoursFormat)
    }

    class TimetableListViewHolder(
        private val binding: TimetableListItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scheduleView: ScheduleView, is12HoursFormat: Boolean) {
            with(binding) {
                timetableListItemCourseName.text = scheduleView.courseName
                timetableListItemCourseHour.text = itemView.context.getString(
                    R.string.course_time,
                    if (is12HoursFormat) formatLocalTime(scheduleView.startTime) else scheduleView.startTime,
                    if (is12HoursFormat) formatLocalTime(scheduleView.endTime) else scheduleView.endTime,
                )
                timetableListItemClassroom.text = scheduleView.classPlace
                root.tag = scheduleView.id
            }
        }

        private fun formatLocalTime(localTime: LocalTime): String {
            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            return localTime.format(formatter)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ScheduleView>() {
            override fun areItemsTheSame(oldItem: ScheduleView, newItem: ScheduleView): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScheduleView, newItem: ScheduleView): Boolean {
                return oldItem == newItem
            }

        }
    }
}