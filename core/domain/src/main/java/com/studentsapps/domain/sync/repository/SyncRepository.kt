package com.studentsapps.domain.sync.repository

/**
 * Repository interface for data-synchronization operations.
 *
 * The implementation lives in the data layer ([com.studentsapps.data.sync.SyncRepositoryImpl]).
 */
interface SyncRepository {

    /**
     * Starts a full cloud→local synchronization only when all pre-conditions
     * are met:
     * - A Firebase user is authenticated.
     * - The device has a validated internet connection.
     * - There are no pending local write operations still awaiting upload.
     *
     * Safe to call from any coroutine scope; suspends until the sync completes
     * or a pre-condition is not met (in which case it returns immediately).
     */
    suspend fun startSyncIfNeeded()

    /**
     * Runs the full bidirectional sync for the given [userId]:
     * 1. Fetches all remote courses and schedules from Firestore.
     * 2. Compares each remote record with its local counterpart by [lastModified].
     * 3. Inserts or updates local records that are behind the remote version.
     * 4. Deletes local records that no longer exist remotely.
     * 5. Re-schedules all device alarms to reflect the updated local data.
     *
     * Callers that need finer control (e.g. a background Worker) can invoke
     * this directly, bypassing the pre-condition checks of [startSyncIfNeeded].
     */
    suspend fun startSynchronization(userId: String)
}