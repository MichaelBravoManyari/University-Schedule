package com.studentsapps.course.fragments

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
import com.studentsapps.course.databinding.FragmentCourseBinding
import com.studentsapps.course.viewmodels.CourseUiState
import com.studentsapps.course.viewmodels.CourseViewModel
import com.studentsapps.ui.CourseAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: CourseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            courseFragment = this@CourseFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        val courseAdapter = CourseAdapter {
            navController.navigate(
                CourseFragmentDirections.actionCourseFragmentToRegisterCourseFragment(
                    it.id
                )
            )
        }
        binding.recyclerViewCourse.adapter = courseAdapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is CourseUiState.Success) {
                        courseAdapter.submitList(currentState.courseList)
                    }
                }
            }
        }
    }

    fun navigateToCourseRegistrationFragment() {
        navController.navigate(CourseFragmentDirections.actionCourseFragmentToRegisterCourseFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}