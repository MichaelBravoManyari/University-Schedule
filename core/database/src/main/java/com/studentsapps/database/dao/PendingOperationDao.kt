package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.studentsapps.database.model.PendingOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: PendingOperationEntity)

    @Query("SELECT * FROM pending_operations WHERE status = :status AND user_id = :userId ORDER BY timestamp ASC")
    fun getPendingOperations(
        status: String,
        userId: String,
    ): Flow<List<PendingOperationEntity>>

    @Query("UPDATE pending_operations SET status = :status WHERE id = :id")
    suspend fun updateStatus(
        id: Int,
        status: String,
    )

    @Query(
        "SELECT * FROM pending_operations " +
                "WHERE status = 'PENDING' " +
                "AND operation_type = :operationType " +
                "AND entity_type = :entityType " +
                "AND user_id = :userId",
    )
    fun getPendingReadOperations(
        operationType: String,
        entityType: String,
        userId: String,
    ): Flow<List<PendingOperationEntity>>

    @Query("SELECT * FROM pending_operations " +
            "WHERE status = :status " +
            "AND user_id = :userId " +
            "ORDER BY timestamp ASC LIMIT 1")
    fun getFirstPendingOperation(
        status: String,
        userId: String,
    ): Flow<PendingOperationEntity?>
}
