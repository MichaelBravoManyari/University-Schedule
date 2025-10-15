package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.model.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class FakeCourseRepository
    @Inject
    constructor() : CourseRepository {
        private val courseDao = TestCourseDao()

        override fun getCourse(courseId: String): Flow<Course> = courseDao.getCourseById(courseId).map(CourseEntity::asExternalModel)

        override suspend fun registerCourse(
            course: Course,
            userId: String,
        ): String =
            courseDao
                .insert(
                    with(course) {
                        CourseEntity(
                            id,
                            name,
                            nameProfessor,
                            color,
                            LocalDateTime.now(),
                            userId = userId,
                        )
                    },
                ).toString()

        override suspend fun registerCourseEntity(courseEntity: CourseEntity) {
            TODO("Not yet implemented")
        }

        override fun getAllCourse(
            shouldSync: Boolean,
            userId: String,
        ): Flow<List<Course>> {
            TODO("Not yet implemented")
        }

        override suspend fun updateCourse(
            course: Course,
            userId: String,
        ) = courseDao.update(
            with(course) {
                CourseEntity(
                    id,
                    name,
                    nameProfessor,
                    color,
                    LocalDateTime.now(),
                    userId = userId,
                )
            },
        )

        override suspend fun updateCourseEntity(courseEntity: CourseEntity) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteCourse(courseId: String) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteCourseEntity(courseId: String) {
            TODO("Not yet implemented")
        }

        override fun getCoursesByIds(courseIds: List<String>): Flow<List<Course>> {
            TODO("Not yet implemented")
        }

        override fun getAllCourseEntity(userId: String): Flow<List<CourseEntity>> {
            TODO("Not yet implemented")
        }

        fun restoreDatabase() = courseDao.restoreDatabase()
    }
