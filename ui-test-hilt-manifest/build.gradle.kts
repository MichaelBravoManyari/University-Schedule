plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.ui_test_hilt_manifest"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}