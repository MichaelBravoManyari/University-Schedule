package com.studentsapps.data.repository

import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CourseRepositoryImp @Inject constructor(
    private val courseLocalDataSource: CourseLocalDataSource
) : CourseRepository {
    override fun getCourse(courseId: Int): Flow<Course> =
        courseLocalDataSource.getCourse(courseId).map(CourseEntity::asExternalModel)

    override suspend fun registerCourse(course: Course): Long =
        courseLocalDataSource.insert(with(course) { CourseEntity(id, name, nameProfessor, color) })

    override fun getAllCourse(): Flow<List<Course>> =
        courseLocalDataSource.getAllCourse().map { it.map(CourseEntity::asExternalModel) }

    override suspend fun updateCourse(course: Course) =
        courseLocalDataSource.updateCourse(with(course) {
            CourseEntity(
                id, name, nameProfessor, color
            )
        })
}