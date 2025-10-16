package com.studentsapps.database.test.data.testdoubles

import com.studentsapps.database.dao.PendingOperationDao
import com.studentsapps.database.model.PendingOperationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestPendingOperationDao : PendingOperationDao {
    override suspend fun insert(operation: PendingOperationEntity) {

    }

    override fun getPendingOperations(
        status: String,
        userId: String
    ): Flow<List<PendingOperationEntity>> {
        return flowOf(emptyList())
    }

    override suspend fun updateStatus(id: Int, status: String) {

    }

    override fun getPendingReadOperations(
        operationType: String,
        entityType: String,
        userId: String
    ): Flow<List<PendingOperationEntity>> {
        return flowOf(emptyList())
    }

    override fun getFirstPendingOperation(
        status: String,
        userId: String
    ): Flow<PendingOperationEntity?> {
        return flowOf(null)
    }
}