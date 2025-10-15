plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.lint")
}

android {
    namespace = "com.studentsapps.designsystem"
}

dependencies {
    implementation(libs.material)
}
