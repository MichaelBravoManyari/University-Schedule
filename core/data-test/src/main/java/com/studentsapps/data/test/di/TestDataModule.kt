package com.studentsapps.data.test.di

import com.studentsapps.data.di.DataModule
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.data.repository.fake.FakeScheduleRepository
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [DataModule::class]
)
interface TestDataModule {

    @Binds
    @Singleton
    fun bindTimetableUserPreferencesRepository(
        timetableUserPreferencesRepository: FakeTimetableUserPreferencesRepository
    ): TimetableUserPreferencesRepository

    @Binds
    @Singleton
    fun bindScheduleRepository(
        scheduleRepository: FakeScheduleRepository
    ): ScheduleRepository

    @Binds
    @Singleton
    fun bindCourseRepository(
        courseRepository: FakeCourseRepository
    ): CourseRepository
}
