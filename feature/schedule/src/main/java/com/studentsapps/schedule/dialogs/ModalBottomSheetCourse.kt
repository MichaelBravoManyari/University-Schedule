package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.schedule.adapters.CourseAdapter
import com.studentsapps.schedule.databinding.ModalBottomSheetCourseBinding
import com.studentsapps.schedule.viewmodels.BottomSheetCourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModalBottomSheetCourse : BottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: BottomSheetCourseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                viewModel.uiState.collect {
                    courseAdapter.submitList(it.coursesItems)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}