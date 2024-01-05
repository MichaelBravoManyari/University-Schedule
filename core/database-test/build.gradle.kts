plugins {
    id("universityschedule.android.library")
}

android {
    namespace = "com.studentsapps.database.test"
}

dependencies {
    implementation(project(":core:database"))
    implementation(libs.kotlinx.coroutines.core)
}