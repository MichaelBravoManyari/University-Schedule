plugins {
    id("universityschedule.android.library")
}

android {
    namespace = "com.studentsapps.testing"
}

dependencies {
    debugImplementation(libs.fragment.testing)
    implementation(libs.androidx.appcompat)
    implementation(project(":ui-test-hilt-manifest"))
}