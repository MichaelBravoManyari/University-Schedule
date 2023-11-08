plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.data.test"
}

dependencies {

    implementation(project(":core:data"))

    api(libs.hilt.testing)
}