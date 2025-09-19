package com.studentsapps.common

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
    class UserManager @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val _userId = MutableStateFlow(auth.currentUser?.uid)
    val userId: StateFlow<String?> = _userId

    fun updateUserId() {
        _userId.value = auth.currentUser?.uid
    }
}