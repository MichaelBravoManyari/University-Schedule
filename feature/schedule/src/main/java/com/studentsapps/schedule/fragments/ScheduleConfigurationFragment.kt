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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
    private lateinit var navController: NavController

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
        navController = view.findNavController()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is Success) {
                        binding.btnDayOfTheWeek.text =
                            if (currentState.isMondayFirstDayOfWeek)
                                getText(R.string.monday)
                            else
                                getText(R.string.sunday)

                        binding.btnTimeFormat.text =
                            if (currentState.is12HoursFormat)
                                getText(R.string.twelve_hours)
                            else
                                getText(R.string.twenty_four_hours)

                        binding.saturdayDisplaySwitch.apply {
                            if (isChecked != currentState.showSaturday)
                                isChecked = currentState.showSaturday
                            setOnCheckedChangeListener { _, _ ->
                                viewModel.setShowSaturday()
                            }
                        }

                        binding.sundayDisplaySwitch.apply {
                            if (isChecked != currentState.showSunday)
                                isChecked = currentState.showSunday

                            setOnCheckedChangeListener { _, _ ->
                                viewModel.setShowSunday()
                            }
                        }
                    }
                }
            }
        }

        configureAppBar()
    }

    private fun configureAppBar() {
        binding.toolbar.run {
            setupWithNavController(navController)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}