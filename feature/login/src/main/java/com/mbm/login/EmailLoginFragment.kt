package com.mbm.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentEmailLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailLoginFragment : Fragment() {
    private var _binding: FragmentEmailLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        setupClickListeners()
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
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        if (isValidEmail(email) && password.isNotEmpty()) {
            signInWithEmail(email, password)
        } else {
            showValidationErrors(email, password)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val request =
                    NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
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

    private fun showValidationErrors(email: String, password: String) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}