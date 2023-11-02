plugins {
    id("universityschedule.android.library")
}

android {
    namespace = "com.studentsapps.testing"
}

dependencies {
    api(libs.hilt.testing)
    api(libs.kotlinx.coroutines.test)

    debugImplementation(libs.fragment.testing)
    implementation(libs.androidx.appcompat)
    implementation(project(":ui-test-hilt-manifest"))
}