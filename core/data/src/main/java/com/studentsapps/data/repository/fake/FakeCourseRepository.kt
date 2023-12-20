package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.model.Course
import javax.inject.Inject

class FakeCourseRepository @Inject constructor() : CourseRepository {

    private val courseDao = TestCourseDao()

    override suspend fun getCourse(courseId: Int): Course =
        courseDao.getCourseById(courseId).asExternalModel()

    override suspend fun registerCourse(course: Course): Long = courseDao.insert(with(course) {
        CourseEntity(
            id, name, nameProfessor, color
        )
    })

    override suspend fun getAllCourse(): List<Course> {
        return courseDao.getAll().map(CourseEntity::asExternalModel)
    }
}