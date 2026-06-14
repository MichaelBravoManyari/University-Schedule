package com.studentsapps.data.repository

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.studentsapps.domain.sync.model.NetworkConnectivityChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Android implementation of [NetworkConnectivityChecker].
 *
 * Lives in the data layer so that the domain layer stays free of
 * Android platform dependencies.
 *
 * Bind this implementation to the interface in your Hilt module:
 * ```kotlin
 * @Binds
 * abstract fun bindNetworkConnectivityChecker(
 *     impl: NetworkConnectivityCheckerImpl
 * ): NetworkConnectivityChecker
 * ```
 */
class NetworkConnectivityCheckerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NetworkConnectivityChecker {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}