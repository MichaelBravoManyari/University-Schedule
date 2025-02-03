package com.studentsapps.data.repository

import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.model.PendingOperationEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.PendingOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PendingOperationRepositoryImp @Inject constructor(
    private val pendingOperationLocalDataSource: PendingOperationLocalDataSource,
) : PendingOperationRepository {
    override suspend fun insert(pendingOperation: PendingOperation) =
        pendingOperationLocalDataSource.insert(with(pendingOperation) {
            PendingOperationEntity(
                id,
                operationType,
                entityType,
                payload,
                status,
                timestamp
            )
        })

    override fun getPendingOperations(status: String): Flow<List<PendingOperation>> =
        pendingOperationLocalDataSource.getPendingOperations(status)
            .map { it.map(PendingOperationEntity::asExternalModel) }

    override suspend fun updateStatus(id: Int, status: String) =
        pendingOperationLocalDataSource.updateStatus(id, status)
}