package com.studentsapps.data.repository

import com.studentsapps.model.PendingOperation
import kotlinx.coroutines.flow.Flow

interface PendingOperationRepository {
    suspend fun insert(
        pendingOperation: PendingOperation,
        userId: String,
    )

    fun getPendingOperations(
        status: String,
        userId: String,
    ): Flow<List<PendingOperation>>

    suspend fun updateStatus(
        id: Int,
        status: String,
    )

    fun getPendingReadOperations(
        operationType: String,
        entityType: String,
        userId: String,
    ): Flow<List<PendingOperation>>

    fun getFirstPendingOperation(
        status: String,
        userId: String,
    ): Flow<PendingOperation?>
}
