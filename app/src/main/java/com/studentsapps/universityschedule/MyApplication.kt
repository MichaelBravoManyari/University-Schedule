package com.studentsapps.universityschedule

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        )

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MyApplication) {
                Log.d("AdMob", "SDK initialized Application")
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}