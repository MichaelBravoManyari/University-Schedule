package com.studentsapps.database.test.data.testdoubles

import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.test.data.courseList
import com.studentsapps.database.test.data.restoreCoursesBackup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestCourseDao : CourseDao() {
    override fun getCourseById(courseId: Int): Flow<CourseEntity> =
        flowOf(courseList.find { it.id == courseId }!!)

    override fun getAll(): Flow<List<CourseEntity>> = flowOf(courseList)

    override suspend fun insert(obj: CourseEntity): Long = obj.id.toLong()

    override suspend fun update(obj: CourseEntity) {
        val index = courseList.indexOfFirst { it.id == obj.id }
        if (index != -1) courseList[index] = obj
    }

    override suspend fun delete(obj: CourseEntity) {
        TODO("Not yet implemented")
    }

    fun restoreDatabase() = restoreCoursesBackup()
}