package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.schedule.databinding.ModalBottomSheetCourseBinding
import com.studentsapps.schedule.viewmodels.BottomSheetCourseUiState
import com.studentsapps.schedule.viewmodels.BottomSheetCourseViewModel
import com.studentsapps.ui.CourseAdapter
import com.studentsapps.ui.dialogs.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModalBottomSheetCourse : BaseBottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: BottomSheetCourseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetCourseBinding.inflate(inflater, container, false)
        navController = findNavController()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            bottomSheetCourse = this@ModalBottomSheetCourse
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val courseAdapter = CourseAdapter {
            navController.previousBackStackEntry?.savedStateHandle?.set("course", it.id)
            dismiss()
        }
        binding.recyclerViewCourses.adapter = courseAdapter
        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is BottomSheetCourseUiState.Success) {
                        courseAdapter.submitList(currentState.courseList)
                    }
                }
            }
        }
    }

    fun goToRegisterCourseFragment() {
        val navOptions = NavOptions.Builder().setEnterAnim(android.R.anim.slide_in_left)
            .setExitAnim(android.R.anim.slide_out_right)
            .setPopEnterAnim(android.R.anim.slide_in_left)
            .setPopExitAnim(android.R.anim.slide_out_right).build()
        val navigatedFromTimeLoggingDestination = true
        val request =
            NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/registerCourseFragment/$navigatedFromTimeLoggingDestination".toUri())
                .build()
        navController.navigate(request = request, navOptions = navOptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}