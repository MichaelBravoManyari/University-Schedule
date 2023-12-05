package com.studentsapps.data.repository

import com.studentsapps.model.Course

interface CourseRepository {

    suspend fun getCourse(courseId: Int): Course
}