package com.studentsapps.database

import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.dao.PendingOperationDao
import com.studentsapps.database.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesScheduleDao(database: UniversityScheduleDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun providesCourseDao(database: UniversityScheduleDatabase): CourseDao = database.courseDao()

    @Provides
    fun providesPendingOperationDao(database: UniversityScheduleDatabase): PendingOperationDao =
        database.pendingOperationDao()
}
