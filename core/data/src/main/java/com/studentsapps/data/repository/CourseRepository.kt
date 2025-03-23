package com.studentsapps.data.repository

import com.studentsapps.database.model.CourseEntity
import com.studentsapps.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun getCourse(courseId: String): Flow<Course>

    suspend fun registerCourse(course: Course, userId: String): String

    suspend fun registerCourseEntity(courseEntity: CourseEntity)

    fun getAllCourse(shouldSync: Boolean = true, userId: String): Flow<List<Course>>

    suspend fun updateCourse(course: Course, userId: String)

    suspend fun updateCourseEntity(courseEntity: CourseEntity)

    suspend fun deleteCourse(courseId: String)

    fun getCoursesByIds(courseIds: List<String>): Flow<List<Course>>

    fun getAllCourseEntity(userId: String): Flow<List<CourseEntity>>
}