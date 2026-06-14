package com.mbm.login.email_login

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
import com.studentsapps.login.databinding.FragmentEmailLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import theme.UniversityScheduleTheme

/**
 * Fragment for the email login screen.
 */
@AndroidEntryPoint
class EmailLoginFragment : Fragment() {
    private var _binding: FragmentEmailLoginBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: EmailLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmailLoginBinding.inflate(inflater, container, false)
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

    private fun setupComposeView() {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UniversityScheduleTheme {
                    EmailLoginScreen(
                        viewModel = viewModel,
                        onRegisterClick = ::navigateToEmailSignUp,
                        onNavigateToSchedule = ::navigateToScheduleFragment,
                        onBackClick = ::navigateToAuthFragment,
                    )
                }
            }
        }
    }

    private fun navigateToAuthFragment() {
        navController.navigate(
            EmailLoginFragmentDirections.actionEmailLoginFragmentToAuthFragment()
        )
    }

    private fun navigateToEmailSignUp() {
        navController.navigate(
            EmailLoginFragmentDirections.actionEmailLoginFragmentToEmailSignUpFragment()
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
