package com.studentsapps.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatchers: Dispatchers)

enum class Dispatchers {
    Default,
    IO,
}