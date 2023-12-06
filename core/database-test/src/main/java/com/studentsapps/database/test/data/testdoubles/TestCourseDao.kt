package com.studentsapps.database.test.data.testdoubles

import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.test.data.courseList

class TestCourseDao : CourseDao() {
    override suspend fun getCourseById(courseId: Int): CourseEntity =
        courseList.find { it.id == courseId }!!

    override suspend fun insert(obj: CourseEntity): Long = obj.id.toLong()

    override suspend fun update(obj: CourseEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: CourseEntity) {
        TODO("Not yet implemented")
    }
}