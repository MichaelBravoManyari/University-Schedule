package com.studentsapps.database.test.data

import com.studentsapps.database.model.CourseEntity

private val backupCourseList = listOf(
    createTestCourse(1, "Math"),
    createTestCourse(2, "History"),
    createTestCourse(3, "Sciences"),
    createTestCourse(4, "Statistics"),
)

val courseList = mutableListOf<CourseEntity>().apply {
    addAll(backupCourseList)
}

fun restoreCoursesBackup() {
    courseList.clear()
    courseList.addAll(backupCourseList)
}

private fun createTestCourse(id: Int, name: String) = CourseEntity(id = id, name = name, null, 1234)