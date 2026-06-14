package com.mbm.login.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.studentsapps.login.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import theme.UniversityScheduleTheme

/**
 * Fragment for authentication screen.
 *
 * This fragment now acts as a thin coordinator layer that:
 * - Manages fragment lifecycle
 * - Handles navigation (since the app still uses fragment-based navigation)
 * - Hosts the Compose AuthScreen
 */
@AndroidEntryPoint
class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressHandler()
        navController = view.findNavController()
        setupComposeView()
    }

    /**
     * Configures the back press handler to finish the app instead of navigating back.
     */
    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            },
        )
    }

    /**
     * Sets up the Compose view and passes all callbacks.
     */
    private fun setupComposeView() {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                UniversityScheduleTheme {
                    AuthScreen(
                        viewModel = viewModel,
                        onEmailSignIn = ::navigateToEmailLogin,
                        onRegisterClick = ::navigateToEmailSignUp,
                        onNavigateToSchedule = ::navigateToScheduleFragment
                    )
                }
            }
        }
    }

    /**
     * Navigate to the email login screen.
     * TODO: This will be migrated to Compose Navigation when the app migrates.
     */
    private fun navigateToEmailLogin() {
        navController.navigate(
            AuthFragmentDirections.actionAuthFragmentToEmailLoginFragment()
        )
    }

    /**
     * Navigate to the email sign-up screen.
     * TODO: This will be migrated to Compose Navigation when the app migrates.
     */
    private fun navigateToEmailSignUp() {
        navController.navigate(
            AuthFragmentDirections.actionAuthFragmentToEmailSignUpFragment()
        )
    }

    /**
     * Navigate to the schedule screen.
     * TODO: This will be migrated to Compose Navigation when the app migrates.
     */
    private fun navigateToScheduleFragment() {
        val request =
            NavDeepLinkRequest.Builder
                .fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                .build()

        val options =
            NavOptions
                .Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopEnterAnim(android.R.anim.fade_in)
                .setPopExitAnim(android.R.anim.fade_out)
                .build()

        navController.navigate(request, options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
