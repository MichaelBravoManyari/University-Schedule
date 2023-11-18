package com.studentsapps.database.dao

import androidx.room.Dao
import com.studentsapps.database.model.CourseEntity

@Dao
abstract class CourseDao : BaseDao<CourseEntity>