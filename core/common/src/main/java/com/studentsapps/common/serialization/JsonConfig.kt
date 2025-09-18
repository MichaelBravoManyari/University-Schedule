package com.studentsapps.common.serialization

import kotlinx.serialization.json.Json

object JsonConfig {
    val appJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}