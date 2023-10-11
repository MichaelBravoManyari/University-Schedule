package com.studentsapps.schedule.timetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studentsapps.schedule.databinding.TimetableListItemBinding

internal class TimetableListAdapter : ListAdapter<String, TimetableListAdapter.TimetableListViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableListViewHolder {
        return TimetableListViewHolder(
            TimetableListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimetableListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TimetableListViewHolder(private var binding: TimetableListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(string: String) {
                binding.timetableListItemText.text = string
            }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem.contentEquals(newItem)
            }

        }
    }
}