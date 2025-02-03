package com.studentsapps.data.repository

import com.studentsapps.model.PendingOperation
import kotlinx.coroutines.flow.Flow

interface PendingOperationRepository {
    suspend fun insert(pendingOperation: PendingOperation)

    fun getPendingOperations(status: String): Flow<List<PendingOperation>>

    suspend fun updateStatus(id: Int, status: String)
}