package com.studentsapps.datastore_test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
fun TestScope.runTestAndClearPreferences(testDataStore: DataStore<Preferences>, block: suspend () -> Unit) {
    runTest {
        testDataStore.edit { it.clear() }
        block()
    }
}