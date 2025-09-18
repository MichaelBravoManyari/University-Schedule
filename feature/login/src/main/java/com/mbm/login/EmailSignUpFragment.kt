package com.mbm.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.common.UserManager
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentEmailSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class EmailSignUpFragment : Fragment() {
    private var _binding: FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userManager: UserManager

    private lateinit var backPressCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        setupBackPressHandler()
        setupClickListeners()
    }

    private fun setupBackPressHandler() {
        backPressCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                // No se hace nada mientras la carga esté activa
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallback)
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            navController.navigate(R.id.action_emailSignUpFragment_to_authFragment)
        }
        binding.btnSignUp.setOnClickListener {
            handleSignUp()
        }
    }

    private fun handleSignUp() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val passwordRe = binding.editTextValPassword.text.toString().trim()
        if (isInputValid(email, password, passwordRe)) {
            createUser(email, password)
        }
    }

    private fun isInputValid(email: String, password: String, passwordRe: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.editTextLayoutEmail.error = getString(R.string.enter_your_email)
                false
            }

            !isValidEmail(email) -> {
                binding.editTextLayoutEmail.error = getString(R.string.invalid_email)
                false
            }

            password.isEmpty() -> {
                binding.editTextLayoutPassword.error = getString(R.string.enter_your_password)
                false
            }

            !isValidPassword(password) -> {
                binding.editTextLayoutPassword.error = getString(R.string.low_security_password)
                false
            }

            password != passwordRe -> {
                binding.editTextLayoutValPassword.error = getString(R.string.passwords_do_not_match)
                false
            }

            else -> true
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun createUser(email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userManager.updateUserId()
                if (userManager.userId.value != null) {
                    saveUserToFirestore(userManager.userId.value!!)
                }
            } else {
                showLoading(false)
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setTitle(getString(R.string.user_creation_error_message))
                    setMessage(getString(R.string.email_already_registered))
                    setNegativeButton(getString(R.string.accept), null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
    }

    private fun saveUserToFirestore(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).set(emptyMap<String, Any>())
            .addOnSuccessListener {
                showLoading(false)
                val request =
                    NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                        .build()
                navController.navigate(request)
            }
            .addOnFailureListener { e ->
                showLoading(false)
                val builder = AlertDialog.Builder(requireContext())
                builder.apply {
                    setTitle(getString(R.string.user_creation_error_message))
                    setMessage(getString(R.string.firestore_save_error))
                    setNegativeButton(getString(R.string.accept), null)
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                Log.d("EmailSignUpFragment", "Error: $e")
            }
    }

    private fun showLoading(show: Boolean) {
        binding.progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        binding.blockingLayer.visibility = if (show) View.VISIBLE else View.GONE
        backPressCallback.isEnabled = show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}