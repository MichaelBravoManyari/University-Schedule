package com.studentsapps.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studentsapps.model.Course

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "name_professor")
    val nameProfessor: String?,
    val color: Int
)

fun CourseEntity.asExternalModel() = Course(
    id = id,
    name = name,
    nameProfessor = nameProfessor,
    color = color
)