package com.studentsapps.course.adapters

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studentsapps.course.databinding.CourseListItemBinding
import com.studentsapps.model.Course
import com.studentsapps.ui.R
import com.studentsapps.ui.timetable.animateOpacity

class CourseListAdapter(private val onItemClicked: (Course) -> Unit) :
    ListAdapter<Course, CourseListAdapter.CourseListViewHolder>(
        DiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListViewHolder {
        val viewHolder = CourseListViewHolder(
            CourseListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        viewHolder.itemView.setOnClickListener {
            animateOpacity(it, 0.5f) {
                animateOpacity(it, 1.0f) {
                    val position = viewHolder.adapterPosition
                    onItemClicked(getItem(position))
                }
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CourseListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseListViewHolder(
        private val binding: CourseListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            with(binding) {
                courseListItemName.text = course.name
                courseListItemName.setTextColor(getTextColorBasedOnCourseColor(course.color))
                courseListItemTeacherName.visibility = View.GONE
                course.nameProfessor?.let { classPlace ->
                    if (classPlace.replace(" ", "").isNotEmpty()) {
                        courseListItemTeacherName.apply {
                            visibility = View.VISIBLE
                            text = course.nameProfessor
                            setTextColor(
                                getTextColorBasedOnCourseColor(
                                    course.color
                                )
                            )
                            val drawableStart = compoundDrawablesRelative[0]
                            if (drawableStart != null) {
                                val color = getTextColorBasedOnCourseColor(course.color)
                                drawableStart.colorFilter =
                                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                                setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    drawableStart, null, null, null
                                )
                            }
                        }

                    }
                }
                courseListItemContainer.backgroundTintList =
                    ColorStateList.valueOf(course.color)
                root.tag = course.id
            }
        }

        private fun getTextColorBasedOnCourseColor(@ColorInt courseColor: Int): Int {
            return if (ColorUtils.calculateLuminance(courseColor) < 0.5) ContextCompat.getColor(
                binding.root.context, R.color.timetable_schedule_view_light_text_color
            )
            else ContextCompat.getColor(
                binding.root.context, R.color.timetable_schedule_view_dark_text_color
            )
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Course>() {
            override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
                return oldItem == newItem
            }
        }
    }
}