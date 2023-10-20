package com.studentsapps.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.studentsapps.schedule.databinding.FragmentScheduleBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    val showAsGrid = MutableStateFlow(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scheduleFragment = this@ScheduleFragment
            timetableView = timetable
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //showCurrentMonthInAppBar()
        configureMenuOptionsInAppBar()
    }

    /*private fun showCurrentMonthInAppBar() {
        binding.apply {
            toolbar.title = timetable._currentMonth
        }
    }*/

    private fun configureMenuOptionsInAppBar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.schedule_appbar_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.change_timetable_view -> {
                        showAsGrid.update {
                            !it
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}