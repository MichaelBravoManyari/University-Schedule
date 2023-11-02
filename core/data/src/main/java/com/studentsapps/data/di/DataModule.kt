package com.studentsapps.data.di

import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.data.repository.TimetableUserPreferencesRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindTimetableUserPreferencesRepository(
        timetableUserPreferencesRepository: TimetableUserPreferencesRepositoryImp
    ): TimetableUserPreferencesRepository
}