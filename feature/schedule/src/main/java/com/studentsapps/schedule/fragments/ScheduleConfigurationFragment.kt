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
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.FragmentScheduleConfigurationBinding
import com.studentsapps.schedule.viewmodels.ScheduleConfigurationUiState.Success
import com.studentsapps.schedule.viewmodels.ScheduleConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleConfigurationFragment : Fragment() {

    private var _binding: FragmentScheduleConfigurationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleConfigurationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleConfigurationBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ScheduleConfigurationFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is Success) {
                        binding.firstDayOfWeekValue.text =
                            if (currentState.isMondayFirstDayOfWeek)
                                getText(R.string.monday)
                            else
                                getText(R.string.sunday)

                        binding.hourFormatValue.text =
                            if (currentState.is12HoursFormat)
                                getText(R.string.twelve_hours)
                            else
                                getText(R.string.twenty_four_hours)

                        binding.saturdayDisplaySwitch.isChecked = currentState.showSaturday

                        binding.sundayDisplaySwitch.isChecked = currentState.showSunday
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}