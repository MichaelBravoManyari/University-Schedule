package com.mbm.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.sync.SynchronizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SynchronizationViewModel @Inject constructor(
    private val synchronizationManager: SynchronizationManager
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    fun startSyncIfNeeded() {
        if (_isSyncing.value) return

        viewModelScope.launch {
            _isSyncing.value = true
            synchronizationManager.startSyncIfNeeded()
            _isSyncing.value = false
        }
    }
}
