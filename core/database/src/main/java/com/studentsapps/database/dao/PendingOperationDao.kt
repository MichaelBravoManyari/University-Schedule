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

    @Query("SELECT * FROM pending_operations WHERE status = :status ORDER BY timestamp ASC")
    fun getPendingOperations(status: String): Flow<List<PendingOperationEntity>>

    @Query("UPDATE pending_operations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}