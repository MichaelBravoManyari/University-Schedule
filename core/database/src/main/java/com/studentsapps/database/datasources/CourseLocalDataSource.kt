package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CourseLocalDataSource @Inject constructor(
    private val courseDao: CourseDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getCourse(courseId: Int): CourseEntity = withContext(ioDispatcher) {
        courseDao.getCourseById(courseId)
    }
}