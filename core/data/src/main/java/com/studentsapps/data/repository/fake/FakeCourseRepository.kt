package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.model.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeCourseRepository @Inject constructor() : CourseRepository {

    private val courseDao = TestCourseDao()

    override fun getCourse(courseId: Int): Flow<Course> =
        courseDao.getCourseById(courseId).map(CourseEntity::asExternalModel)

    override suspend fun registerCourse(course: Course): Long = courseDao.insert(with(course) {
        CourseEntity(
            id, name, nameProfessor, color
        )
    })

    override fun getAllCourse(): Flow<List<Course>> {
        return courseDao.getAll().map { it.map(CourseEntity::asExternalModel) }
    }

    override suspend fun updateCourse(course: Course) = courseDao.update(with(course) {
        CourseEntity(
            id, name, nameProfessor, color
        )
    })

    fun restoreDatabase() = courseDao.restoreDatabase()
}