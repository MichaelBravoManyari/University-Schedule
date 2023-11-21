plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.room")
}

android {
    namespace = "com.studentsapps.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Test
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.rules)
}