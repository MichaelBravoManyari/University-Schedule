package com.studentsapps.domain.sync.model

/**
 * Domain interface for checking network availability.
 */
interface NetworkConnectivityChecker {
    /**
     * Returns true if the device currently has a validated internet connection.
     */
    fun isConnected(): Boolean
}