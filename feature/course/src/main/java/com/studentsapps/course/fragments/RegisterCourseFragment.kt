package com.studentsapps.course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.studentsapps.course.R
import com.studentsapps.course.databinding.FragmentRegisterCourseBinding
import com.studentsapps.course.viewmodels.RegisterCourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterCourseFragment : Fragment() {

    private var _binding: FragmentRegisterCourseBinding? = null
    private val binding get() = _binding!!
    private val args: RegisterCourseFragmentArgs by navArgs()
    private lateinit var navController: NavController
    private val viewModel: RegisterCourseViewModel by viewModels()
    private var courseId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCourseBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@RegisterCourseFragment.viewModel
            registerCourseFragment = this@RegisterCourseFragment
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
        if (courseId != 0) viewModel.displayCourseData(courseId)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isCourseRecorded) navController.navigate(
                        RegisterCourseFragmentDirections.actionRegisterCourseFragmentToCourseFragment()
                    )
                }
            }
        }

        configureMenuOptionsInAppBar()
        setupNavigationObservers()
    }

    fun goToBottomSheetColor(colorCourse: Int) {
        val request =
            NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/modalBottomSheetColor/$colorCourse".toUri())
                .build()
        navController.navigate(request)
    }

    private fun setupNavigationObservers() {
        val navBackStackEntry = navController.getBackStackEntry(R.id.registerCourseFragment)
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
            get<Int>("color")?.let { viewModel.selectColorCourse(it) }
            remove<Int>("color")
        }
    }

    private fun configureMenuOptionsInAppBar() {
        binding.toolbar.run {
            //inflateMenu(com.studentsapps.ui.R.menu.registration_menu)
            setupWithNavController(navController)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    com.studentsapps.ui.R.id.menu_add -> {
                        viewModel.run {
                            setCourseName(binding.editTextCourseName.text.toString())
                            setNameProfessor(binding.editTextTeacherCourse.text.toString())
                            registerCourse()
                        }
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