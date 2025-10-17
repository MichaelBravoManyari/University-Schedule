package com.mbm.login

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}
