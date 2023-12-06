package com.studentsapps.database.test.data

import com.studentsapps.database.model.CourseEntity

val courseList = mutableListOf(
    createTestCourse(1, "Math"),
    createTestCourse(2, "History"),
    createTestCourse(3, "Sciences"),
    createTestCourse(4, "Statistics"),
)

private fun createTestCourse(id: Int, name: String) = CourseEntity(id = id, name = name, null, 1234)