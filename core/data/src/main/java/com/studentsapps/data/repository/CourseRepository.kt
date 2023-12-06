package com.studentsapps.data.repository

import com.studentsapps.model.Course

interface CourseRepository {

    suspend fun getCourse(courseId: Int): Course

    suspend fun registerCourse(course: Course): Long
}