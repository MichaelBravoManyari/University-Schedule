package com.studentsapps.admodule.di

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.studentsapps.admodule.AdManager
import com.studentsapps.admodule.InterstitialAdCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Provides
    @Singleton
    fun provideAdManager(app: Application): AdManager {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(app) {}
        }
        return AdManager(InterstitialAdCache())
    }
}