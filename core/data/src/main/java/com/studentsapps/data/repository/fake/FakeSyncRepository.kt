package com.studentsapps.data.repository.fake

import com.studentsapps.domain.sync.repository.SyncRepository

/**
 * Fake implementation of [SyncRepository] for use in tests.
 */
class FakeSyncRepository : SyncRepository {

    /**
     * Number of times [startSyncIfNeeded] has been invoked.
     * Reset between tests by calling [reset].
     */
    var startSyncIfNeededCallCount: Int = 0

    override suspend fun startSyncIfNeeded() {
        startSyncIfNeededCallCount++
    }

    override suspend fun startSynchronization(userId: String) {
        TODO("Not yet implemented")
    }

    /**
     * Resets [startSyncIfNeededCallCount] to zero.
     * Call this in `@Before` so each test starts from a clean state.
     */
    fun reset() {
        startSyncIfNeededCallCount = 0
    }
}