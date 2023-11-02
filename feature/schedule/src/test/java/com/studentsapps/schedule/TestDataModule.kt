package com.studentsapps.schedule

import com.studentsapps.data.di.DataModule
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface TestDataModule {

    @Binds
    @Singleton
    fun bindTimetableUserPreferencesRepository(
        timetableUserPreferencesRepository: FakeTimetableUserPreferencesRepository
    ): TimetableUserPreferencesRepository
}
