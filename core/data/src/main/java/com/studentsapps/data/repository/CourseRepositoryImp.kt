package com.studentsapps.data.repository

import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Course
import javax.inject.Inject

class CourseRepositoryImp @Inject constructor(
    private val courseLocalDataSource: CourseLocalDataSource
) : CourseRepository {
    override suspend fun getCourse(courseId: Int): Course =
        courseLocalDataSource.getCourse(courseId).asExternalModel()

    override suspend fun registerCourse(course: Course): Long =
        courseLocalDataSource.insert(with(course) { CourseEntity(id, name, nameProfessor, color) })
}