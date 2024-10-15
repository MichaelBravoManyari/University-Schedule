plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.common"
}

dependencies {
    implementation(libs.firebase.auth.ktx)
}