package com.mbm.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.studentsapps.login.R
import com.studentsapps.login.databinding.FragmentAuthBinding
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                                val request =
                                    NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                                        .build()
                                navController.navigate(request)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.login_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.login_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: GoogleIdTokenParsingException) {
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

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val request =
                NavDeepLinkRequest.Builder.fromUri("android-app://studentsapps.app/scheduleFragment".toUri())
                    .build()
            navController.navigate(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}