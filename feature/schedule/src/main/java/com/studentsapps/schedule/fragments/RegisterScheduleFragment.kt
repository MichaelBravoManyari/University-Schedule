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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
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
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

private const val TAG = "RegisterSchedule"

@AndroidEntryPoint
class RegisterScheduleFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentRegisterScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterScheduleViewModel by viewModels()

    val onExistingCoursesCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
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

        setupNavigationObservers()
        configureMenuOptionsInAppBar()
    }

    private fun configureMenuOptionsInAppBar() {
        binding.toolbar.run {
            setupWithNavController(navController)
            inflateMenu(R.menu.schedule_registration_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_add_schedule -> {
                        viewModel.run {
                            if (!uiState.value.existingCourses) setCourseName(binding.editTextCourse.text.toString())
                            setClassroom(binding.editTextClassroom.text.toString())
                            registerSchedule()
                        }
                        true
                    }

                    else -> menuItem.onNavDestinationSelected(findNavController())
                }
            }
        }
    }

    private fun setupNavigationObservers() {
        val navBackStackEntry = navController.getBackStackEntry(R.id.registerScheduleFragment)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                handleSavedState(navBackStackEntry.savedStateHandle)
            }
        }

        navBackStackEntry.lifecycle.addObserver(observer)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun handleSavedState(savedStateHandle: SavedStateHandle) {
        with(savedStateHandle) {
            get<Int>("day")?.let { viewModel.selectDay(DayOfWeek.of(it)) }
            get<Int>("course")?.let { viewModel.selectCourse(it) }
            get<Int>("color")?.let { viewModel.selectColorCourse(it) }
            get<RecurrenceOption>("repetition")?.let { viewModel.setRecurrentOption(it) }

            remove<Int>("day")
            remove<Int>("course")
            remove<Int>("color")
            remove<RecurrenceOption>("repetition")
        }
    }

    fun goToBottomSheetDay() {
        if (viewModel.uiState.value.repetition == RecurrenceOption.EVERY_WEEK) {
            navController.navigate(
                RegisterScheduleFragmentDirections.actionRegisterScheduleFragmentToModalBottomSheetDay()
            )
            return
        }
        showDatePicker()
    }

    private fun showDatePicker() {
        val title = getString(R.string.select_date)
        val instant =
            viewModel.uiState.value.specificDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val constraintsBuilder =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
                .setStart(MaterialDatePicker.todayInUtcMilliseconds()).build()

        val datePicker = buildMaterialDatePicker(title, instant, constraintsBuilder)

        datePicker.addOnPositiveButtonClickListener {
            val instants = Instant.ofEpochMilli(it)
            val localDate = instants.atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1)
            viewModel.setSpecificDate(localDate)
        }

        datePicker.show(childFragmentManager, TAG)
    }

    private fun buildMaterialDatePicker(
        title: String, selectionInstant: Instant, constraintsBuilder: CalendarConstraints
    ): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker().setTitleText(title)
            .setSelection(selectionInstant.toEpochMilli())
            .setCalendarConstraints(constraintsBuilder).build()
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