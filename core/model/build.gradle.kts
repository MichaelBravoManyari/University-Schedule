plugins {
    id("universityschedule.android.library")
}

android {
    namespace = "com.studentsapps.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.common)
    implementation(project(":core:network"))
}
