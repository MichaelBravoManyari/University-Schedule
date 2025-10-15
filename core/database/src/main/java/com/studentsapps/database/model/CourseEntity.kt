package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studentsapps.model.Course
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    @ColumnInfo(name = "name_professor")
    val nameProfessor: String?,
    val color: Int,
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime,
    @ColumnInfo(name = "user_id")
    val userId: String,
)

fun CourseEntity.asExternalModel() =
    Course(
        id = id,
        name = name,
        nameProfessor = nameProfessor,
        color = color,
    )
