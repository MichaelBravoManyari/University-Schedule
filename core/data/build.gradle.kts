plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":core:database-test"))
    androidTestImplementation(project(":core:datastore-test"))

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.datastore.preferences)

    testImplementation(libs.kotlinx.coroutines.test)
}