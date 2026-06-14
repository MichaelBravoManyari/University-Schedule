package com.studentsapps.data.di

import com.studentsapps.data.repository.NetworkConnectivityCheckerImpl
import com.studentsapps.data.repository.SyncRepositoryImpl
import com.studentsapps.domain.sync.model.NetworkConnectivityChecker
import com.studentsapps.domain.sync.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds the sync-layer interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindNetworkConnectivityChecker(
        impl: NetworkConnectivityCheckerImpl,
    ): NetworkConnectivityChecker
}