package com.studentsapps.universityschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.sync.SynchronizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val remoteConfigHelper: RemoteConfigHelper,
        private val synchronizationManager: SynchronizationManager,
    ) : ViewModel() {
        private val _isReady = MutableLiveData(false)
        val isReady: LiveData<Boolean> get() = _isReady

        private val _isSyncing = MutableStateFlow(false)
        val isSyncing: StateFlow<Boolean> = _isSyncing

        private val _forceUpdateRequired = MutableLiveData(false)
        val forceUpdateRequired: LiveData<Boolean> get() = _forceUpdateRequired

        init {
            checkAppVersion()
        }

        fun startSyncIfNeeded() {
            if (_isSyncing.value) return

            viewModelScope.launch {
                _isSyncing.value = true
                synchronizationManager.startSyncIfNeeded()
                _isSyncing.value = false
            }
        }

        private fun checkAppVersion() {
            viewModelScope.launch {
                remoteConfigHelper.fetchAndActivate()
                val minSupported = remoteConfigHelper.getMinSupportedVersion()
                val currentVersion = BuildConfig.VERSION_CODE

                if (currentVersion < minSupported) {
                    _forceUpdateRequired.value = true
                } else {
                    _isReady.value = true
                }
            }
        }
    }
