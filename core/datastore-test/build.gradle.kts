plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.datastore_test"
}

dependencies {
    api(project(":core:datastore"))
    implementation(project(":core:testing"))

    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.datastore.preferences)
}