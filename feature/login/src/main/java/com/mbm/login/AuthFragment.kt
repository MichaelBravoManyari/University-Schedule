package com.mbm.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

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
    }

    private fun signInWithGoogle() {

        showLoading(true)

        val googleIdOption =
            GetSignInWithGoogleOption.Builder(getString(R.string.web_client_id))
                .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = CredentialManager.create(requireActivity())
                    .getCredential(requireActivity(), request)
                handleSignIn(result)
            } catch (e: GetCredentialException) {
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
                    } catch (e: GoogleIdTokenParsingException) {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_error),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "AuthFragment",
                            "Se recibió una respuesta de token de identificación de Google no válida",
                            e
                        )
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
                    Log.d("AuthFragment", "Usuario registrado correctamente en Firestore.")
                    navigateToScheduleFragment()
                }.addOnFailureListener { e ->
                    Log.e("AuthFragment", "Error al registrar el usuario en Firestore.", e)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.login_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.d("AuthFragment", "Usuario ya existe en Firestore.")
                navigateToScheduleFragment()
            }
        }.addOnFailureListener { e ->
            Log.e("AuthFragment", "Error al verificar el usuario en Firestore.", e)
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


    private fun navigateToScheduleFragment() {
        val request =
            NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                .build()
        navController.navigate(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}