package com.studentsapps.course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.studentsapps.course.databinding.FragmentRegisterCourseBinding
import com.studentsapps.course.viewmodels.RegisterCourseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterCourseFragment : Fragment() {

    private var _binding: FragmentRegisterCourseBinding? = null
    private val binding get() = _binding!!
    private val args: RegisterCourseFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private val viewModel: RegisterCourseViewModel by viewModels()
    private var courseId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCourseBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@RegisterCourseFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        /*If the course ID passed to this fragment is equal to 0, it indicates that you want to
        add a new course. If it is greater than or equal to 1, it signifies that you want to
        update an existing course.*/

        courseId = args.courseId
        if (courseId != 0)
            viewModel.displayCourseData(courseId)
    }
}