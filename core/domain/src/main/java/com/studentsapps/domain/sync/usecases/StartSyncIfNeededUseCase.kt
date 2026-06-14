package com.studentsapps.domain.sync.usecases

import com.studentsapps.domain.sync.repository.SyncRepository
import javax.inject.Inject

/**
 * Use case that triggers a cloud→local synchronization when all pre-conditions
 * are satisfied.
 */
class StartSyncIfNeededUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) {
    /**
     * Delegates to [SyncRepository.startSyncIfNeeded].
     *
     * Suspends until the sync completes or a pre-condition is not met.
     */
    suspend operator fun invoke() {
        syncRepository.startSyncIfNeeded()
    }
}