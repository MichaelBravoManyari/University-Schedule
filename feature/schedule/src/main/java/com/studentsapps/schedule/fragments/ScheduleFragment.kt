package com.studentsapps.schedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.studentsapps.model.asScheduleView
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.FragmentScheduleBinding
import com.studentsapps.schedule.viewmodels.ScheduleUiState
import com.studentsapps.schedule.viewmodels.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scheduleFragment = this@ScheduleFragment
            timetableView = timetable
            scheduleViewModel = viewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is ScheduleUiState.Success) {
                        with(binding.timetable) {
                            setTimetableUserPreferences(currentState.timetableUserPreferences)
                            if (currentState.scheduleDetailsList != null)
                                showScheduleInGrid(currentState.scheduleDetailsList.map { it.asScheduleView() })
                        }
                    }
                }
            }
        }

        with(binding.timetable) {
            date.observe(viewLifecycleOwner) {
                if (isDisplayedAsGrid()) {
                    viewModel.updateScheduleDetailsListInGridMode(
                        displaySaturday(),
                        displaySunday(),
                        getStartDate(),
                        getEndDate()
                    )
                }
            }
        }

        binding.toolbar.setupWithNavController(view.findNavController())
        configureMenuOptionsInAppBar()
    }

    private fun configureMenuOptionsInAppBar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.schedule_appbar_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.change_timetable_view -> {
                        viewModel.setShowAsGrid()
                        true
                    }

                    R.id.timetable_today -> {
                        binding.timetable.selectCurrentDay()
                        true
                    }

                    else -> menuItem.onNavDestinationSelected(findNavController())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}