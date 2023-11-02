package com.studentsapps.testing

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestCoroutineModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideTestCoroutineScope(): TestScope = TestScope(UnconfinedTestDispatcher())
}