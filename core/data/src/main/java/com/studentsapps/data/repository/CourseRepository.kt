package com.studentsapps.data.repository

import com.studentsapps.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun getCourse(courseId: Int): Flow<Course>

    suspend fun registerCourse(course: Course): Long

    fun getAllCourse(): Flow<List<Course>>
}