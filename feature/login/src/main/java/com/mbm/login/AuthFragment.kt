package com.mbm.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.common.UserManager
import com.studentsapps.login.BuildConfig
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentAuthBinding
import com.studentsapps.sync.SynchronizationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
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

        binding.btnLoginEmail.setOnClickListener {
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToEmailLoginFragment())
        }

        binding.newUser.setOnClickListener {
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToEmailSignUpFragment())
        }

        binding.btnLoginGoogle.setOnClickListener {
            signInWithGoogle()
        }

        observeViewModelSync()

        viewModel.startSyncIfNeeded()
    }

    private fun signInWithGoogle() {

        showLoading(true)

        val googleIdOption =
            GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = CredentialManager.create(requireActivity())
                    .getCredential(requireActivity(), request)
                handleSignIn(result)
            } catch (_: GetCredentialException) {
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun handleSignIn(result: androidx.credentials.GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    userManager.updateUserId()
                                    checkAndRegisterUser(userId)
                                } else {
                                    showLoading(false)
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.login_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                showLoading(false)
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.login_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            showLoading(false)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.login_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (_: GoogleIdTokenParsingException) {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkAndRegisterUser(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->

            if (!document.exists()) {
                userDocRef.set(mapOf("userId" to userId)).addOnSuccessListener {
                    viewModel.startSyncIfNeeded()
                    navigateToScheduleFragment()
                }.addOnFailureListener { _ ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.login_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                viewModel.startSyncIfNeeded()
                navigateToScheduleFragment()
            }
        }.addOnFailureListener { _ ->
            Toast.makeText(
                requireContext(),
                getString(R.string.login_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val progressIndicator = binding.progressIndicator
        val blockingLayer = binding.blockingLayer

        if (isLoading) {
            progressIndicator.visibility = View.VISIBLE
            blockingLayer.visibility = View.VISIBLE
            blockingLayer.isClickable = true
        } else {
            progressIndicator.visibility = View.GONE
            blockingLayer.visibility = View.GONE
            blockingLayer.isClickable = false
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

    private fun navigateToScheduleFragment() {
        val request =
            NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
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

    private fun createLoadingDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(requireActivity())
            .setView(com.studentsapps.common.R.layout.dialog_progress)
            .setTitle(this.getText(com.studentsapps.common.R.string.synchronizing))
            .setCancelable(false)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}