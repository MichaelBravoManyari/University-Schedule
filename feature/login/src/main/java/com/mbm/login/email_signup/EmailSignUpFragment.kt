package com.mbm.login.email_signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.studentsapps.login.databinding.FragmentEmailSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import theme.UniversityScheduleTheme

/**
 * Fragment for the email sign-up screen.
 */

@AndroidEntryPoint
class EmailSignUpFragment : Fragment() {
    private var _binding: FragmentEmailSignUpBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController

    private val viewModel: EmailSignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        setupComposeView()
    }

    /**
     * Mounts [EmailSignUpScreen] inside the fragment's ComposeView.
     * Navigation callbacks are injected here so Compose has no knowledge of
     * Fragment-based navigation.
     */
    private fun setupComposeView() {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UniversityScheduleTheme {
                    EmailSignUpScreen(
                        viewModel = viewModel,
                        onNavigateToSchedule = ::navigateToScheduleFragment,
                        onLoginClick = ::navigateToAuthFragment,
                        onBackClick = ::navigateToAuthFragment,
                    )
                }
            }
        }
    }

    // ── Navigation helpers ────────────────────────────────────────────────────
    // TODO: Migrate to Compose Navigation when the app-wide migration is complete.

    private fun navigateToAuthFragment() {
        navController.navigate(
            EmailSignUpFragmentDirections.actionEmailSignUpFragmentToAuthFragment()
        )
    }

    private fun navigateToScheduleFragment() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
            .build()

        val options = NavOptions.Builder()
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