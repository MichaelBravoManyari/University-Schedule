package com.studentsapps.schedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.FragmentRegisterScheduleBinding
import com.studentsapps.schedule.viewmodels.RecurrenceOption
import com.studentsapps.schedule.viewmodels.RegisterScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

private const val TAG = "RegisterSchedule"

@AndroidEntryPoint
class RegisterScheduleFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentRegisterScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterScheduleViewModel by viewModels()

    val existingCoursesIsChecked = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        viewModel.existingCourseChecked(isChecked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterScheduleBinding.inflate(inflater, container, false)
        navController = findNavController()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@RegisterScheduleFragment.viewModel
            registerScheduleFragment = this@RegisterScheduleFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.noSelectCourse) binding.textInputLayoutCourse.endIconDrawable =
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.ic_error, context?.theme
                        )

                    uiState.userMessage?.let { id ->
                        val message = resources.getString(id)
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                        viewModel.userMessageShown()
                    }

                    if (uiState.isScheduleRecorded) {
                        navController.navigate(
                            RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToScheduleFragment()
                        )
                    }
                }
            }
        }

        val navBackStackEntry = navController.getBackStackEntry(R.id.registerScheduleFragment)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                with(navBackStackEntry.savedStateHandle) {
                    get<Int>("day")?.let {
                        viewModel.selectDay(
                            DayOfWeek.of(it)
                        )
                    }
                    get<Int>("course")?.let {
                        viewModel.selectCourse(it)
                    }
                    get<Int>("color")?.let {
                        viewModel.selectColorCourse(it)
                    }
                    get<RecurrenceOption>("repetition")?.let {
                        viewModel.setRecurrentOption(it)
                    }
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    fun goToBottomSheetDay() {
        navController.navigate(
            RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToModalBottomSheetDay()
        )
    }

    fun goToBottomSheetCourse() {
        navController.navigate(
            RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToModalBottomSheetCourse()
        )
    }

    fun goToBottomSheetColor(colorCourse: Int) {
        navController.navigate(
            RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToModalBottomSheetColor(
                colorCourse
            )
        )
    }

    fun goToBottomSheetRepetition() {
        navController.navigate(
            RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToModalBottomSheetRepetition()
        )
    }

    fun showTimePicker(isStartTime: Boolean, time: LocalTime) {
        val title = resources.getString(R.string.select_hour)
        getTimePicker(time, title) { selectedTime ->
            if (isStartTime) viewModel.selectStartHour(selectedTime)
            else viewModel.selectEndHour(selectedTime)
        }.show(childFragmentManager, TAG)
    }

    private fun getTimePicker(
        time: LocalTime, title: String, positiveButtonListener: (selectedTime: LocalTime) -> Unit
    ): MaterialTimePicker {
        val picker =
            MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(time.hour)
                .setMinute(time.minute).setTitleText(title).build()

        picker.addOnPositiveButtonClickListener {
            val selectedTime = LocalTime.of(picker.hour, picker.minute)
            positiveButtonListener(selectedTime)
        }

        return picker
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}