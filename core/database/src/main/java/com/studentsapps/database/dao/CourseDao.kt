package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.CourseEntity

@Dao
abstract class CourseDao : BaseDao<CourseEntity> {

    @Query("SELECT * FROM courses where id = :courseId")
    abstract suspend fun getCourseById(courseId: Int): CourseEntity
}