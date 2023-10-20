plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.datastore"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.datastore.preferences)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
}