package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CourseDao : BaseDao<CourseEntity> {

    @Query("SELECT * FROM courses WHERE id = :courseId")
    abstract fun getCourseById(courseId: String): Flow<CourseEntity>

    @Query("SELECT * FROM courses WHERE user_id = :userId")
    abstract fun getAll(userId: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id IN (:courseIds)")
    abstract fun getCoursesByIds(courseIds: List<String>): Flow<List<CourseEntity>>
}