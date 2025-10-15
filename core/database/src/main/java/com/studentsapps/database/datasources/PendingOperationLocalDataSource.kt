package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.PendingOperationDao
import com.studentsapps.database.model.PendingOperationEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PendingOperationLocalDataSource
    @Inject
    constructor(
        private val pendingOperationDao: PendingOperationDao,
        @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    ) {
        suspend fun insert(pendingOperation: PendingOperationEntity) =
            withContext(ioDispatcher) {
                pendingOperationDao.insert(pendingOperation)
            }

        fun getPendingOperations(
            status: String,
            userId: String,
        ): Flow<List<PendingOperationEntity>> = pendingOperationDao.getPendingOperations(status, userId)

        suspend fun updateStatus(
            id: Int,
            status: String,
        ) = withContext(ioDispatcher) {
            pendingOperationDao.updateStatus(id, status)
        }

        fun getPendingReadOperations(
            operationType: String,
            entityType: String,
            userId: String,
        ): Flow<List<PendingOperationEntity>> = pendingOperationDao.getPendingReadOperations(operationType, entityType, userId)

        fun getFirstPendingOperation(
            status: String,
            userId: String,
        ): Flow<PendingOperationEntity?> = pendingOperationDao.getFirstPendingOperation(status, userId)
    }
