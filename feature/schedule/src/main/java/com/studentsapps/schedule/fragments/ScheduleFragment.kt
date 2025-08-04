package com.studentsapps.schedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.FragmentScheduleBinding
import com.studentsapps.schedule.viewmodels.ScheduleUiState
import com.studentsapps.schedule.viewmodels.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.studentsapps.common.UserManager
import com.studentsapps.ui.theme.UniversityScheduleTheme
import com.studentsapps.schedule.TimetableCompose

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            scheduleFragment = this@ScheduleFragment
            scheduleViewModel = viewModel
        }

        binding.composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                UniversityScheduleTheme {
                    TimetableCompose(viewModel) { scheduleId ->
                        navController.navigate(
                            ScheduleFragmentDirections.actionScheduleFragmentToModalBottomSheetSchedule(
                                scheduleId
                            )
                        )
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            })

        navController = view.findNavController()

        observeCurrentMonth()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { currentState ->
                    if (currentState is ScheduleUiState.Success) {
                        binding.toolbar.menu.findItem(R.id.change_timetable_view)?.icon =
                            if (currentState.timetableUserPreferences.showAsGrid) {
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_list)
                            } else {
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_view)
                            }
                    }
                }
            }
        }

        binding.toolbar.setupWithNavController(navController)
        configureMenuOptionsInAppBar()
    }

    private fun observeCurrentMonth() {
        val observer = Observer<String> { currentMonth ->
            navController.currentDestination?.label = currentMonth
        }
        viewModel.currentMonth.observe(viewLifecycleOwner, observer)
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
                        viewModel.selectNowDay(true)
                        true
                    }

                    R.id.scheduleConfigurationFragment -> {
                        navController.navigate(R.id.action_scheduleFragment_to_scheduleConfigurationFragment)
                        true
                    }

                    R.id.sign_out -> {
                        viewModel.cancelUserAlarms()
                        auth.signOut()
                        userManager.updateUserId()
                        navController.popBackStack(navController.graph.startDestinationId, true)
                        val request =
                            NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/authFragment".toUri())
                                .build()
                        navController.navigate(request)
                        true
                    }

                    else -> menuItem.onNavDestinationSelected(findNavController())
                }
            }
        }
    }

    private fun toggleTimetableView() {
        viewModel.setShowAsGrid()
    }

    fun goToRegisterSchedule() {
        navController.navigate(
            ScheduleFragmentDirections.actionScheduleFragmentToRegisterScheduleFragment(
                scheduleId = "",
                title = R.string.new_schedule
            )
        )
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            val request =
                NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/authFragment".toUri())
                    .build()
            navController.navigate(request)
        } else {
            val firestore = FirebaseFirestore.getInstance()
            val userId = auth.currentUser!!.uid
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Registrar al usuario si no existe
                    userDocRef.set(mapOf("userId" to userId)).addOnSuccessListener {
                        Log.d("AuthFragment", "Usuario registrado correctamente en Firestore.")
                    }.addOnFailureListener { e ->
                        Log.e("AuthFragment", "Error al registrar el usuario en Firestore.", e)
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