package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CourseDao : BaseDao<CourseEntity> {

    @Query("SELECT * FROM courses where id = :courseId")
    abstract fun getCourseById(courseId: Int): Flow<CourseEntity>

    @Query("SELECT * FROM courses")
    abstract fun getAll(): Flow<List<CourseEntity>>
}