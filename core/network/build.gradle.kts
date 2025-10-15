plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.lint")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.studentsapps.network"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {

    implementation(project(":core:common"))

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.kotlinx.serialization.json)
}
