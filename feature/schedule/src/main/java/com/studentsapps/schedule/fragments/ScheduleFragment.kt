package com.studentsapps.schedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.studentsapps.model.asScheduleView
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.FragmentScheduleBinding
import com.studentsapps.schedule.viewmodels.ScheduleUiState
import com.studentsapps.schedule.viewmodels.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        navController = view.findNavController()

        observeCurrentMonth()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launchScheduleDetailsUpdates()
                launchScheduleListUpdates()
                observeScheduleUiState()
            }
        }

        binding.toolbar.setupWithNavController(navController)
        configureMenuOptionsInAppBar()
    }

    private fun observeCurrentMonth() {
        val observer = Observer<String> { currentMonth ->
            navController.currentDestination?.label = currentMonth
        }
        binding.timetable.currentMonth.observe(viewLifecycleOwner, observer)
    }

    private fun CoroutineScope.observeScheduleUiState() {
        launch {
            viewModel.uiState.collect { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    binding.timetable.apply {
                        date.collect { selectedDate ->
                            if (isDisplayedAsGrid()) {
                                viewModel.updateScheduleDetailsListInGridMode(
                                    displaySaturday(),
                                    displaySunday(),
                                    getStartDate(),
                                    getEndDate()
                                )
                            } else {
                                viewModel.updateScheduleDetailsListInListMode(selectedDate)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.launchScheduleDetailsUpdates() {
        launch {
            viewModel.uiState.collect { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    handleScheduleDetailsUpdate(currentState)
                    updateToolbarIcon()
                }
            }
        }
    }

    private fun updateToolbarIcon() {
        binding.toolbar.menu.getItem(0).icon = if (!binding.timetable.isDisplayedAsGrid()) {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_view)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_list)
        }
    }

    private fun CoroutineScope.launchScheduleListUpdates() {
        launch {
            viewModel.uiState.collect { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    handleScheduleListUpdate(currentState)
                }
            }
        }
    }

    private fun handleScheduleDetailsUpdate(currentState: ScheduleUiState.Success) {
        with(binding.timetable) {
            setTimetableUserPreferences(currentState.timetableUserPreferences)
        }
    }

    private fun handleScheduleListUpdate(currentState: ScheduleUiState.Success) {
        with(binding.timetable) {
            if (currentState.scheduleDetailsList != null) {
                showSchedules(
                    currentState.scheduleDetailsList.map { it.asScheduleView() }
                ) { scheduleId ->
                    navController.navigate(
                        ScheduleFragmentDirections.actionScheduleFragmentToModalBottomSheetSchedule(
                            scheduleId
                        )
                    )
                }
            }
        }
    }

    private fun configureMenuOptionsInAppBar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.schedule_appbar_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.change_timetable_view -> {
                        toggleTimetableView()
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

    private fun toggleTimetableView() {
        viewModel.setShowAsGrid()
        binding.toolbar.menu.findItem(R.id.change_timetable_view)?.icon =
            if (binding.timetable.isDisplayedAsGrid()) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_view)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_list)
            }
    }

    fun goToRegisterSchedule() {
        navController.navigate(
            ScheduleFragmentDirections.actionScheduleFragmentToRegisterScheduleFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}