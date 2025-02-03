plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.studentsapps.network"
}

dependencies {

    implementation(project(":core:common"))

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.kotlinx.serialization.json)
}