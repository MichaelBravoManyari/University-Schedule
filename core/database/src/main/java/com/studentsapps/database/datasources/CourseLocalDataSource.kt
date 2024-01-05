package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CourseLocalDataSource @Inject constructor(
    private val courseDao: CourseDao, @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {

    fun getCourse(courseId: Int): Flow<CourseEntity> = courseDao.getCourseById(courseId)

    suspend fun insert(course: CourseEntity): Long = withContext(ioDispatcher) {
        courseDao.insert(course)
    }

    fun getAllCourse(): Flow<List<CourseEntity>> = courseDao.getAll()
}