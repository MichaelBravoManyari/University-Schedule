package com.studentsapps.universityschedule

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val FETCH_AND_ACTIVATE_INTERVAL = 3600L

@Singleton
class RemoteConfigHelper
    @Inject
    constructor() {
        private val remoteConfig = Firebase.remoteConfig

        init {
            val configSettings =
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = FETCH_AND_ACTIVATE_INTERVAL
                }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(
                mapOf("min_supported_version" to BuildConfig.VERSION_CODE),
            )
        }

        suspend fun fetchAndActivate(): Boolean =
            try {
                remoteConfig.fetchAndActivate().await()
            } catch (_: Exception) {
                false
            }

        fun getMinSupportedVersion(): Int = remoteConfig.getLong("min_supported_version").toInt()
    }
