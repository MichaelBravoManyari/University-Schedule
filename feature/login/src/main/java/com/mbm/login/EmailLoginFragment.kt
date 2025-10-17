package com.mbm.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.common.UserManager
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentEmailLoginBinding
import com.studentsapps.sync.SynchronizationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EmailLoginFragment : Fragment() {
    private var _binding: FragmentEmailLoginBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: SynchronizationViewModel by viewModels()
    private var dialog: AlertDialog? = null

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var synchronizationManager: SynchronizationManager

    @Inject
    lateinit var userManager: UserManager

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
        setupClickListeners()
        observeViewModelSync()
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            navigateToAuthFragment()
        }
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun navigateToAuthFragment() {
        navController.navigate(R.id.action_emailLoginFragment_to_authFragment)
    }

    private fun handleLogin() {
        val email =
            binding.editTextEmail.text
                .toString()
                .trim()
        val password =
            binding.editTextPassword.text
                .toString()
                .trim()
        if (isValidEmail(email) && password.isNotEmpty()) {
            signInWithEmail(email, password)
        } else {
            showValidationErrors(email, password)
        }
    }

    private fun signInWithEmail(
        email: String,
        password: String,
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userManager.updateUserId()
                viewModel.startSyncIfNeeded()
                val request =
                    NavDeepLinkRequest.Builder
                        .fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                        .build()
                navController.navigate(request)
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setTitle(getString(R.string.login_error))
                    setMessage(getString(R.string.login_error_message))
                    setNegativeButton(getString(R.string.accept), null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
    }

    private fun observeViewModelSync() {
        lifecycleScope.launch {
            viewModel.isSyncing.collect { isSyncing ->
                if (isSyncing) {
                    if (dialog == null) dialog = createLoadingDialog()
                    dialog?.show()
                } else {
                    dialog?.dismiss()
                    dialog = null
                }
            }
        }
    }

    private fun showValidationErrors(
        email: String,
        password: String,
    ) {
        if (email.isNotEmpty()) {
            if (!isValidEmail(email)) {
                binding.editTextLayoutEmail.error = getString(R.string.invalid_email)
            }
        } else {
            binding.editTextLayoutEmail.error = getString(R.string.enter_your_email)
        }
        if (password.isEmpty()) {
            binding.editTextLayoutPassword.error = getString(R.string.enter_your_password)
        }
    }

    private fun createLoadingDialog(): AlertDialog =
        MaterialAlertDialogBuilder(requireActivity())
            .setView(com.studentsapps.common.R.layout.dialog_progress)
            .setTitle(this.getText(com.studentsapps.common.R.string.synchronizing))
            .setCancelable(false)
            .create()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
