plugins {
    id("universityschedule.android.library")
}

android {
    namespace = "com.studentsapps.universityschedule.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}