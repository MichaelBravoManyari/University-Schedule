package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studentsapps.model.PendingOperation
import java.time.LocalDateTime

@Entity(tableName = "pending_operations")
data class PendingOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "operation_type")
    val operationType: String,
    @ColumnInfo(name = "entity_type")
    val entityType: String,
    @ColumnInfo(name = "payload")
    val payload: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: LocalDateTime,
    @ColumnInfo(name = "user_id")
    val userId: String
)

fun PendingOperationEntity.asExternalModel() = PendingOperation(
    id = id,
    operationType = operationType,
    entityType = entityType,
    payload = payload,
    status = status,
    timestamp = timestamp
)
