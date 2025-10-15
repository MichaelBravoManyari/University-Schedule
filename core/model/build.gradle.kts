plugins {
    id("universityschedule.android.library")
    alias(libs.plugins.kotlin.serialization)
    id("universityschedule.android.lint")
}

android {
    namespace = "com.studentsapps.model"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.common)
    implementation(project(":core:network"))
    implementation(project(":core:common"))
}
